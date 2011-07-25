/*
* Copyright 2010 Konstantinos Tsakalozos
*
* Licensed under the EUPL, Version 1.1 or – as soon they will be
* approved by the European Commission - subsequent versions of the EUPL (the Licence);
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://ec.europa.eu/idabc/eupl
*
* Unless required by applicable law or agreed to in  writing, software
* distributed under the Licence is distributed on an AS IS basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uoa.eolus;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.uoa.eolus.config.ConfigurationsManager;
import org.uoa.eolus.config.UnknownParameter;
import org.uoa.eolus.host.Host;
import org.uoa.eolus.host.HostManager;
import org.uoa.eolus.net.UnknownVNException;
import org.uoa.eolus.net.VNExistsException;
import org.uoa.eolus.script.UnknownScriptException;
import org.uoa.eolus.script.VMContactErrorException;
import org.uoa.eolus.template.MultipleTemplatesException;
import org.uoa.eolus.template.TemplateManager;
import org.uoa.eolus.template.TemplateNotReadyException;
import org.uoa.eolus.template.UnknownTemplateException;
import org.uoa.eolus.user.ReservedUserException;
import org.uoa.eolus.user.UnknownUserException;
import org.uoa.eolus.vm.UnknownVMException;
import org.uoa.eolus.vm.VMExistsException;
import org.uoa.eolus.vm.VirtualMachineManager;

/**
 * @author jackal
 *
 */
@DeclareRoles(value={"admin","user"})
@Stateless
@WebContext(contextRoot="/Madgik", urlPattern="/Eolus",
        authMethod = "BASIC",
        secureWSDLAccess = false)
@SOAPBinding(style=SOAPBinding.Style.RPC)
@SecurityDomain(value = "JBossWS")
@WebService
public class Eolus implements EolusRemote, EolusLocal{

	@PersistenceContext
	EntityManager em;

	@Resource 
	SessionContext ctx;
	
    public Eolus() {
    }


    ////////////// VM & Tepmplates //////////////////////////////////////

    /* (non-Javadoc)
     * @see org.uoa.eolus.EolusInterface#adminCreateVM(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, java.lang.String[])
     */
    @RolesAllowed("admin")
    @WebMethod
    public synchronized void adminCreateVM(
    		@WebParam(name = "userOwner") String user, 
    		@WebParam(name = "VMtemplateName") String VMclass,
    		@WebParam(name = "VMname") String VMname,
    		@WebParam(name = "hostname") String host ,
    		@WebParam(name = "cores") int cores,
    		@WebParam(name = "memSize") int memSize, 
    		@WebParam(name = "networks") String []nets) 
				throws UnknownTemplateException, TemplateNotReadyException, DirectoryException, InternalErrorException, VMExistsException, IOException {
    	TemplateManager templateManager = Managers.getTemplateManager(em);
    	HostManager hostManager = Managers.getHostManager(em);
    	if (host.equals(""))
    		host = null;
        File desc = templateManager.createTemplateDesc(hostManager, user, VMclass, VMname, host, cores, memSize, nets);
        if (desc == null) throw new InternalErrorException("Tempate description not created.");
        VirtualMachineManager virtualMachineManager = Managers.getVirtualMachineManager(em);
        virtualMachineManager.Create(user, VMname, desc);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#createVM(java.lang.String, java.lang.String, int, int, java.lang.String[])
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public synchronized void createVM(
    		@WebParam(name = "VMtemplateName") String VMclass,
    		@WebParam(name = "VMname") String VMname,
    		@WebParam(name = "cores") int cores,
    		@WebParam(name = "memSize") int memSize,
            @WebParam(name = "networks") String []nets) 
				throws UnknownTemplateException, TemplateNotReadyException, DirectoryException, InternalErrorException, VMExistsException, IOException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
    	TemplateManager templateManager = Managers.getTemplateManager(em);
    	HostManager hostManager = Managers.getHostManager(em);
        File desc = templateManager.createTemplateDesc(hostManager, user, VMclass, VMname, cores, memSize, nets);
        if (desc == null) throw new InternalErrorException("Tempate description not created.");
        VirtualMachineManager virtualMachineManager = Managers.getVirtualMachineManager(em);
        virtualMachineManager.Create(user, VMname, desc);
    }

    class Templetize implements Runnable {

        String VM;
        String template;
        String user;
        
        public Templetize(String user, String mname, String templateName) {
            VM = mname;
            template = templateName;
            this.user = user;
        }

        public void run() {

            try {

                System.out.println("Started templetize thread");
                VirtualMachineManager virtualMachineManager = Managers.getVirtualMachineManager(em);
                int VMID = virtualMachineManager.Stop(VM);
                System.out.println("VM stop request posted");
                virtualMachineManager.WaitForFullStop(VM);
                // Permanently added 'XX.XX.XX.XX' (RSA) to
                // the list of known hosts.

                //Template created
                TemplateManager templateManager = Managers.getTemplateManager(em);
                templateManager.makeTemplate(user, VMID, template, virtualMachineManager.getONEDir());

                virtualMachineManager.Resume(VM);
                System.out.println("All done");
            } catch (Exception x) {
                x.printStackTrace();
                return;
            }

        }
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminVMtoTemplate(java.lang.String, java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public boolean adminVMtoTemplate(
    		@WebParam(name = "userOwner") String user,
    		@WebParam(name = "VMname") String VMname, 
    		@WebParam(name = "templateName") String templateName) {
        new Thread(new Templetize(user, VMname, templateName)).start();
        return true;
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#VMtoTemplate(java.lang.String, java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public boolean VMtoTemplate(
    		@WebParam(name = "VMname") String VMname,
    		@WebParam(name = "templateName") String templateName) {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        new Thread(new Templetize(user, VMname, templateName)).start();
        return true;
    }


    //////////////////////// VM management //////////////////////////
    // Admin actions on VMs //
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminShutdownVM(java.lang.String, boolean)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized void adminShutdownVM(
    		@WebParam(name = "VMname") String VMname,
    		@WebParam(name = "forceShutdown") boolean force) throws UnknownVMException, InternalErrorException {
        Managers.getVirtualMachineManager(em).Kill(VMname, force);
    }

	
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminSuspendVM(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized void adminSuspendVM(
    		@WebParam(name = "VMname") String VMname) throws UnknownVMException, InternalErrorException {
        Managers.getVirtualMachineManager(em).Pause(VMname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminResumeVM(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized void adminResumeVM(
    		@WebParam(name = "VMname") String VMname) throws UnknownVMException, InternalErrorException {
        Managers.getVirtualMachineManager(em).Resume(VMname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetVMIP(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized String adminGetVMIP(
    		@WebParam(name = "VMname") String VMname) throws InternalErrorException, UnknownVMException {
        return Managers.getVirtualMachineManager(em).getIP(VMname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetVMInfo(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized String adminGetVMInfo(
    		@WebParam(name = "VMname") String VMname) throws InternalErrorException, UnknownVMException {
    	return Managers.getVirtualMachineManager(em).getInfo(VMname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetVMStatus(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized String adminGetVMStatus(
    		@WebParam(name = "VMname") String VMname) throws UnknownVMException, InternalErrorException {
        return Managers.getVirtualMachineManager(em).getStatus(VMname);
    }

	
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetVMlist()
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized String[] adminGetVMlist() throws InternalErrorException {
        return Managers.getVirtualMachineManager(em).getVMlist().toArray(new String[]{});
    }
    
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetStrayVMlist()
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized String[] adminGetStrayVMlist() throws InternalErrorException {
        return Managers.getVirtualMachineManager(em).getVMlist("private").toArray(new String[]{});
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminAssignVMtoUser(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized void adminAssignVMtoUser(
    		@WebParam(name = "userOwner") String user, 
    		@WebParam(name = "VMname") String VMname) throws UnknownUserException, UnknownVMException, InternalErrorException {
        Managers.getVirtualMachineManager(em).assignVMtoUser(user, VMname);
    }

    // User VM actions //
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#shutdownVM(java.lang.String, boolean)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public synchronized void shutdownVM(
    		@WebParam(name = "VMname") String VMname,
    		@WebParam(name = "forceShutdown") boolean force) throws UnknownVMException, InternalErrorException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getVirtualMachineManager(em).Kill(user, VMname, force);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#suspendVM(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public synchronized void suspendVM(
    		@WebParam(name = "VMname") String VMname) throws UnknownVMException, InternalErrorException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getVirtualMachineManager(em).Pause(user, VMname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#resumeVM(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public synchronized void resumeVM(
    		@WebParam(name = "VMname") String VMname) throws UnknownVMException, InternalErrorException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getVirtualMachineManager(em).Resume(user, VMname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getVMIP(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public synchronized String getVMIP(
    		@WebParam(name = "VMname") String VMname) throws InternalErrorException, UnknownVMException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        return Managers.getVirtualMachineManager(em).getIP(user, VMname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getVMInfo(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public synchronized String getVMInfo(
    		@WebParam(name = "VMname") String VMname) throws InternalErrorException, UnknownVMException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
    	return Managers.getVirtualMachineManager(em).getInfo(user, VMname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getVMStatus(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public synchronized String getVMStatus(
    		@WebParam(name = "VMname") String VMname) throws UnknownVMException, InternalErrorException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        return Managers.getVirtualMachineManager(em).getStatus(user, VMname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getVMlist()
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public synchronized String[] getVMlist() throws InternalErrorException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
    	String[] l = {};
        return Managers.getVirtualMachineManager(em).getVMlist(user).toArray(l);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetUserVMlist(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public synchronized String[] adminGetUserVMlist(
    		@WebParam(name = "userOwner") String user) throws InternalErrorException {
    	String[] l = {};
        return Managers.getVirtualMachineManager(em).getVMlist(user).toArray(l);
    }

    ///////////////// VM & Scripting ///////////////////////
    // User Methods //
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminApplyScript(java.lang.String, java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public void adminApplyScript(
    		@WebParam(name = "userOwner") String user,
    		@WebParam(name = "scriptName") String script, 
    		@WebParam(name = "VMname") String VMname) throws InternalErrorException, VMContactErrorException, UnknownVMException {
		Managers.getScriptManager(em).applyScript(user, script, Managers.getVirtualMachineManager(em).getIP(VMname));
	}

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#applyScript(java.lang.String, java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public void applyScript(
    		@WebParam(name = "scriptName") String script, 
    		@WebParam(name = "VMname") String VMname) throws InternalErrorException, VMContactErrorException, UnknownVMException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
		Managers.getScriptManager(em).applyScript(user, script, Managers.getVirtualMachineManager(em).getIP(user, VMname));
	}

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminExecCMD(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
	public String[] adminExecCMD(
			@WebParam(name = "cmd") String cmd, 
			@WebParam(name = "VMname") String VMname) throws InternalErrorException, VMContactErrorException, UnknownVMException {
		return Managers.getScriptManager(em).execCMD(cmd, Managers.getVirtualMachineManager(em).getIP(VMname));
	}

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#execCMD(java.lang.String, java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
	public String[] execCMD(
			@WebParam(name = "cmd") String cmd, 
			@WebParam(name = "VMname") String VMname) throws InternalErrorException, VMContactErrorException, UnknownVMException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
		return Managers.getScriptManager(em).execCMD(cmd, Managers.getVirtualMachineManager(em).getIP(user, VMname));
	}
    
    //////////////// Script management ///////////////////////
    // User methods //
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetScriptList(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public String[] adminGetScriptList(
    		@WebParam(name = "userOwner") String user) {
    	String[] l = {};
    	return Managers.getScriptManager(em).getUserScriptList(user).toArray(l);
	}

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getScriptList()
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public String[] getScriptList() {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
    	String[] l = {};
    	return Managers.getScriptManager(em).getUserScriptList(user).toArray(l);
	}

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminAddScript(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
	public void adminAddScript(
			@WebParam(name = "userOwner") String user, 
			@WebParam(name = "scriptName") String name,
			@WebParam(name = "scriptContnet") String content,
			@WebParam(name = "scriptDescription") String description) throws DirectoryException {
		Managers.getScriptManager(em).addScript(user, name, content, description);
	}

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#addScript(java.lang.String, java.lang.String, java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
	public void addScript(
			@WebParam(name = "scriptName") String name,
			@WebParam(name = "scriptContnet") String content,
			@WebParam(name = "scriptDescription") String description) throws DirectoryException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
		Managers.getScriptManager(em).addScript(user, name, content, description);
	}

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetDescription(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
	public String adminGetDescription(
			@WebParam(name = "userOwner") String user, 
			@WebParam(name = "scriptName") String name) throws UnknownScriptException {
        return Managers.getScriptManager(em).getDescription(user, name);
	}

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getDescription(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
	public String getDescription(
			@WebParam(name = "scriptName") String name) throws UnknownScriptException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        return Managers.getScriptManager(em).getDescription(user, name);
	}

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#removeScript(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
	public void removeScript(
			@WebParam(name = "scriptName") String name) throws UnknownScriptException, DirectoryException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getScriptManager(em).removeScript(user, name);
	}

	// admin methods //
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminRemoveScript(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
	public void adminRemoveScript(
			@WebParam(name = "userOwner") String user, 
			@WebParam(name = "scriptName") String name) 
			throws UnknownScriptException, DirectoryException {
        Managers.getScriptManager(em).removeScript(user, name);
	}
	
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminSyncUserScripts(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void adminSyncUserScripts(
    		@WebParam(name = "user") String user) throws UnknownUserException {
        Managers.getScriptManager(em).syncUserScripts(user);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#syncScripts()
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public void syncScripts() throws UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getScriptManager(em).syncUserScripts(user);
    }

	
/*
 * TODO future work
	@WebMethod
	public boolean makeScritPublic(String user, String name) {
        return Managers.getScriptManager(em).makeScriptPublic(user, name);
	}

	@WebMethod
	public boolean removePublicScript(String name) {
        return Managers.getScriptManager(em).removeScript("public", name);
	}
*/
	
    ////////////////// Template management /////////////////////
	// User methods //
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#transferTemplate(java.lang.String, java.lang.String, boolean)
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public void transferTemplate(
    		@WebParam(name = "templateName") String template, 
    		@WebParam(name = "newUserOwner") String target,
    		@WebParam(name = "move") boolean move) 
			throws InternalErrorException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getTemplateManager(em).transferTemplate(user, template, target, move);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminTransferTemplate(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void adminTransferTemplate(
    		@WebParam(name = "userOwner") String user,
    		@WebParam(name = "templateName") String template,
    		@WebParam(name = "newUser") String target,
    		@WebParam(name = "move") boolean move) 
		throws InternalErrorException {
        Managers.getTemplateManager(em).transferTemplate(user, template, target, move);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getPublicTemplateStatus(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public String getPublicTemplateStatus(
    		@WebParam(name = "templateName") String template) throws UnknownTemplateException, MultipleTemplatesException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = "public";
        return Managers.getTemplateManager(em).getTemplateStatus(user, template);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getTemplateStatus(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public String getTemplateStatus(
    		@WebParam(name = "templateName") String template) throws UnknownTemplateException, MultipleTemplatesException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        return Managers.getTemplateManager(em).getTemplateStatus(user, template);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetTemplateStatus(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public String adminGetTemplateStatus(
    		@WebParam(name = "userOwner") String user, 
    		@WebParam(name = "templateName") String template) 
		throws UnknownTemplateException, MultipleTemplatesException {
        return Managers.getTemplateManager(em).getTemplateStatus(user, template);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#removeTemplate(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public void removeTemplate(
    		@WebParam(name = "templateName") String name) 
		throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getTemplateManager(em).removeTemplate(user, name);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminRemoveTemplate(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void adminRemoveTemplate(
    		@WebParam(name = "userOwner") String user, 
    		@WebParam(name = "templateName") String name) 
		throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException {
        Managers.getTemplateManager(em).removeTemplate(user, name);
    }
	
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getTemplates()
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public String[] getTemplates() {
		System.out.println("getTemplates called");
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
    	String[] l = {};
        return Managers.getTemplateManager(em).getTemplates(user).toArray(l);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetTemplates(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public String[] adminGetTemplates(
    		@WebParam(name = "userOwner") String user) {
		System.out.println("getTemplates called");
    	String[] l = {};
        return Managers.getTemplateManager(em).getTemplates(user).toArray(l);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getPublicTemplates()
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public String[] getPublicTemplates() {
    	String[] l = {};
        return Managers.getTemplateManager(em).getTemplates("public").toArray(l);
    }

	// Admin methods //
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#makeTemplatePublic(java.lang.String, java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public void makeTemplatePublic(
    		@WebParam(name = "templateName") String template, 
    		@WebParam(name = "templatePublicName") String target) 
		throws DirectoryException, UnknownTemplateException, MultipleTemplatesException, TemplateNotReadyException, InternalErrorException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getTemplateManager(em).makeTemplatePublic(user, template, target);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminRemovePublicTemplate(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void adminRemovePublicTemplate(
    		@WebParam(name = "templateName") String name) 
		throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException {
        Managers.getTemplateManager(em).removeTemplate("public" , name);
    }

	
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminSyncUserTemplates(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void adminSyncUserTemplates(
    		@WebParam(name = "user") String user) throws UnknownUserException{
        Managers.getTemplateManager(em).syncUserTemplates(user);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#syncTemplates()
	 */
	@RolesAllowed({"admin","user"})
	@WebMethod
    public void syncTemplates() throws UnknownUserException{
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getTemplateManager(em).syncUserTemplates(user);
    }

    //////////////// Configurations ////////////////////////////
	// Admin methods //
	/**
	 * {@inheritDoc} 
	 * 
	 * @see org.uoa.eolus.EolusRemote#setConfigurationParameter(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void setConfigurationParameter(
    		@WebParam(name = "key") String key,
    		@WebParam(name = "value") String value) throws InternalErrorException, UnknownParameter{
		ConfigurationsManager configManager = Managers.getConfigurationsManager(em);
		configManager.updateConfigParam(key, value);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getConfigurationParameter(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public String getConfigurationParameter(
    		@WebParam(name = "key") String key) throws InternalErrorException, UnknownParameter{
		ConfigurationsManager configManager = Managers.getConfigurationsManager(em);
		return configManager.getConfigParam(key);
    }

	//////////////////////////User Mangement///////////////
	// Admin methods //
	/** {@inheritDoc}
	 * 
	 * @see org.uoa.eolus.EolusInterface#addUser(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void addUser(
    		@WebParam(name = "username") String username) throws ReservedUserException, DirectoryException {
		Managers.getUserManager(em).addUser(username);
		Managers.getTemplateManager(em).addUser(username);
		Managers.getScriptManager(em).addUser(username);
    }
	
	/** {@inheritDoc}
	 * @see org.uoa.eolus.EolusInterface#deleteUser(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void deleteUser(
    		@WebParam(name = "username") String username) throws ReservedUserException, UnknownUserException, DirectoryException {
        Managers.getUserManager(em).deleteUser(username);
        Managers.getTemplateManager(em).removeUser(username);
        Managers.getScriptManager(em).removeUser(username);
    }
	
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getUsers()
	 */
	@RolesAllowed("admin")
	@WebMethod
    public String [] getUsers() {
		String[] l = {};
		List<String> usernames = Managers.getUserManager(em).getUserNames(); 
    	return usernames.toArray(l);
    }

    //////////////////////////Host Mangement///////////////
	// Admin methods //
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getHostInfo(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public String getHostInfo(
    		@WebParam(name = "hostname") String hostname) throws InternalErrorException {
		String info = Managers.getHostManager(em).getHostInfo(hostname); 
    	return info;
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getHostList()
	 */
	@RolesAllowed("admin")
	@WebMethod
    public String[] getHostList() throws InternalErrorException {
		List<String> list = Managers.getHostManager(em).getHostList(); 
    	return list.toArray(new String[]{});
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#deleteHost(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void deleteHost(
    		@WebParam(name = "hostname") String hostname) throws InternalErrorException {
		Managers.getHostManager(em).Delete(hostname); 
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#enableHost(java.lang.String, boolean)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void enableHost(
    		@WebParam(name = "hostname") String hostname,
    		@WebParam(name = "enable") boolean enable) throws InternalErrorException {
		Managers.getHostManager(em).Enable(hostname, enable); 
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#createHost(java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
    public void createHost(
    		@WebParam(name = "hostname") String hostname) throws InternalErrorException, IOException {
		Managers.getHostManager(em).Create(hostname); 
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#migrateVM(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
	@WebMethod
	public void migrateVM(
			@WebParam(name = "VMName") String vmname,
			@WebParam(name = "hostname") String hostname) throws InternalErrorException, UnknownVMException {
		Integer hostID = Managers.getHostManager(em).getHostIDfromHostName(hostname);
		Managers.getVirtualMachineManager(em).migrate(vmname, hostID);
	}

	////////////////// Virtual Networks //////////////////////////////
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetVNetList(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public String[] adminGetVNetList(
    		@WebParam(name = "user") String user) throws InternalErrorException {
        return Managers.getVirtualNetManager(em).getVNetList(user).toArray(new String[]{});
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getVNetList()
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public String[] getVNetList() throws InternalErrorException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        return Managers.getVirtualNetManager(em).getVNetList(user).toArray(new String[]{});
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getVNetPublicList()
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public String[] getVNetPublicList() throws InternalErrorException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        return Managers.getVirtualNetManager(em).getPublicVNetList().toArray(new String[]{});
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetAllVNetList()
	 */
	@RolesAllowed("admin")
    @WebMethod
    public String[] adminGetAllVNetList() throws InternalErrorException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
        return Managers.getVirtualNetManager(em).getVNetList().toArray(new String[]{});
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#getVNetInfo(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public String getVNetInfo(
    		@WebParam(name = "VNetName") String VNname) throws UnknownVNException, InternalErrorException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        return Managers.getVirtualNetManager(em).getInfo(user, VNname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetVNetInfo(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public String adminGetVNetInfo(
    		@WebParam(name = "VNetName") String VNname) throws InternalErrorException, UnknownVNException {
        return Managers.getVirtualNetManager(em).getInfo(VNname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminCreateVNet(java.lang.String, int)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public void adminCreateVNet(
    		@WebParam(name = "VNetName") String VNname,
    		@WebParam(name = "VNetType") int type,
			@WebParam(name = "VNetDesc") String Description) 
	throws InternalErrorException, VNExistsException{
		Managers.getVirtualNetManager(em).Create("public", VNname, type, Description);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#createVNet(java.lang.String, int)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public void createVNet(
    		@WebParam(name = "VNetName") String VNname,
    		@WebParam(name = "VNetType") int type, 
			@WebParam(name = "VNetDesc") String Description) 
	throws InternalErrorException, VNExistsException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
		Managers.getVirtualNetManager(em).Create(user, VNname, type, Description);
	}
	
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminRemoveVNet(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public void adminRemoveVNet(
    		@WebParam(name = "VNetName") String VNname) throws InternalErrorException, UnknownVNException  {
        Managers.getVirtualNetManager(em).Remove(VNname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#removeVNet(java.lang.String)
	 */
	@RolesAllowed({"admin","user"})
    @WebMethod
    public void removeVNet(
    		@WebParam(name = "VNetName") String VNname) throws InternalErrorException, UnknownVNException, UnknownUserException {
		Principal cp = ctx.getCallerPrincipal();
		System.out.println("User calling me:" + cp.getName());
		String user = cp.getName();
        Managers.getVirtualNetManager(em).Remove(user, VNname);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminAssignVNettoUser(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public void adminAssignVNettoUser(
    		@WebParam(name = "user") String user, 
    		@WebParam(name = "VNetName") String VNname) throws UnknownUserException, UnknownVNException, InternalErrorException {
        Managers.getVirtualNetManager(em).assignVNettoUser(user, VNname);
    }

	////////////////// Sites //////////////////////////////
	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminCreateSite(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public void adminCreateSite(
    		@WebParam(name = "site") String site
    		) throws InternalErrorException {
        Managers.getHostManager(em).CreateSite(site);
    }

	@RolesAllowed("admin")
    @WebMethod
    public void adminUpdateSite(
    		@WebParam(name = "site") String site,
    		@WebParam(name = "property") String property,
    		@WebParam(name = "value") String value
    		) throws InternalErrorException, IOException {
        Managers.getHostManager(em).UpdateSite(site, property, value);
    }

	@RolesAllowed("admin")
    @WebMethod
    public String adminGetSiteInfo(
    		@WebParam(name = "site") String site
    		) throws InternalErrorException, IOException {
        return Managers.getHostManager(em).getSiteInfo(site);
    }

	@RolesAllowed("admin")
    public String adminReadSite(String site, String property)
		throws InternalErrorException, IOException {
        return Managers.getHostManager(em).ReadSite(site, property);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminDeleteSite(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public void adminDeleteSite(
    		@WebParam(name = "site") String site) throws InternalErrorException {
        Managers.getHostManager(em).DeleteSite(site);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminAssignHostToSite(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public void adminAssignHostToSite(
    		@WebParam(name = "hostname") String hostname,
    		@WebParam(name = "site") String site) throws InternalErrorException {
        Managers.getHostManager(em).AddHostToSite(hostname, site);
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminGetHostsofSite(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public String[] adminGetHostsofSite(
    		@WebParam(name = "site") String site) throws InternalErrorException {
        return Managers.getHostManager(em).getSiteHosts(site).toArray(new String[]{});
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#sitename(java.lang.String, java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public String[] adminGetSites() throws InternalErrorException {
        return Managers.getHostManager(em).getSiteList().toArray(new String[]{});
    }

	/* (non-Javadoc)
	 * @see org.uoa.eolus.EolusInterface#adminMoveHostToDefaultSite(java.lang.String)
	 */
	@RolesAllowed("admin")
    @WebMethod
    public void adminMoveHostToDefaultSite(
    		@WebParam(name = "hostname") String hostname) throws InternalErrorException {
        Managers.getHostManager(em).RemoveHostFromSite(hostname);
    }

	@RolesAllowed("admin")
    public Host getHost(String hostname) throws InternalErrorException {
        return Managers.getHostManager(em).getHostStats(hostname);
    }


}
