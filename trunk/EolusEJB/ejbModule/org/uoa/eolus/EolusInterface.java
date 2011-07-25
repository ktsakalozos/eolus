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

package org.uoa.eolus;

import java.io.IOException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.uoa.eolus.config.UnknownParameter;
import org.uoa.eolus.host.Host;
import org.uoa.eolus.net.UnknownVNException;
import org.uoa.eolus.net.VNExistsException;
import org.uoa.eolus.script.UnknownScriptException;
import org.uoa.eolus.script.VMContactErrorException;
import org.uoa.eolus.template.MultipleTemplatesException;
import org.uoa.eolus.template.TemplateNotReadyException;
import org.uoa.eolus.template.UnknownTemplateException;
import org.uoa.eolus.user.ReservedUserException;
import org.uoa.eolus.user.UnknownUserException;
import org.uoa.eolus.vm.UnknownVMException;
import org.uoa.eolus.vm.VMExistsException;

/**
 * @author jackal
 *
 */
@Remote
public interface EolusInterface {

	////////////// VM & Tepmplates //////////////////////////////////////
    /** Create a VM acting as admin.
     * @param user Username of user that will own the VM
     * @param VMclass Template to be used to instantiate a VM 	
     * @param VMname VM name
     * @param host VM will be placed in this host. Optional, null if not to be used
     * @param cores Number of cores
     * @param memSize Memory size in MB
     * @param nets Array of network names
     * @throws UnknownTemplateException, TemplateNotReadyException, DirectoryException, InternalErrorException, VMExistsException
     */
	public abstract void adminCreateVM(String user, String VMclass,
			String VMname, String host, int cores, int memSize, String[] nets) 
	throws UnknownTemplateException, TemplateNotReadyException, DirectoryException, InternalErrorException, VMExistsException, IOException;

    /** Create a VM.
     * @param VMclass Template to be used to instantiate a VM 	
     * @param VMname VM name
     * @param cores Number of cores
     * @param memSize Memory size in MB
     * @param nets Array of network names
     * @throws UnknownTemplateException, TemplateNotReadyException, DirectoryException, InternalErrorException, VMExistsException
     */
	public abstract void createVM(String VMclass, String VMname, int cores,
			int memSize, String[] nets) 
	throws UnknownTemplateException, TemplateNotReadyException, DirectoryException, InternalErrorException, VMExistsException, IOException;

    /** Create a template from any running VM (acting as admin).
     * @param user User to own the template 	
     * @param VMname VM name
     * @param templateName Template name
     * @return true on success
     */
	public abstract boolean adminVMtoTemplate(String user, String VMname,
			String templateName);

    /** Create a template from a running VM.
     * @param VMname VM name
     * @param templateName Template name
     * @return true on success
     */
	public abstract boolean VMtoTemplate(String VMname, String templateName);
	//////////////////////// VM management //////////////////////////
	// Admin actions on VMs //

    /** Shutdown any VM.
     * @param VMname VM name
     * @param force Poweroff/destroy the VM
     * @throws UnknownVMException, InternalErrorException 
     */
	public abstract void adminShutdownVM(String VMname, boolean force) throws UnknownVMException, InternalErrorException;

	
    /** Suspend any running VM.
     * @param VMname VM name
     * @throws UnknownVMException, InternalErrorException 
     */
	public abstract void adminSuspendVM(String VMname) throws UnknownVMException, InternalErrorException;

    /** Resume any suspended VM.
     * @param VMname VM name
     * @throws UnknownVMException, InternalErrorException 
     */
	public abstract void adminResumeVM(String VMname) throws UnknownVMException, InternalErrorException;

    /** Get the IP of a VM.
     * @param VMname VM name
     * @return The VM's IP
     * @throws InternalErrorException, UnknownVMException 
     */
	public abstract String adminGetVMIP(String VMname) throws InternalErrorException, UnknownVMException;

    /** Get information on a running VM.
     * @param VMname VM name
     * @return The XML containing all Info
     * @throws InternalErrorException, UnknownVMException 
     */
	public abstract String adminGetVMInfo(String VMname) throws InternalErrorException, UnknownVMException;

    /** Get the status of a running VM.
     * @param VMname VM name
     * @return The status (STAGING, RUNNING, FAILING)
     * @throws InternalErrorException, UnknownVMException 
     */
	public abstract String adminGetVMStatus(String VMname) throws UnknownVMException, InternalErrorException;

    /** Get a listing of all VMs, regardless their owner.
     * @return The list of VM names
     * @throws InternalErrorException
     */
	public abstract String[] adminGetVMlist() throws InternalErrorException;

    /** Get a listing of VMs without an owner.
     * @return The list of VM names
     * @throws InternalErrorException
     */
	public abstract String[] adminGetStrayVMlist() throws InternalErrorException;

    /** Assign a VM to a user.
     * @param user The user that will get the VM
     * @param VMname The name of the VM
     * @throws UnknownUserException, UnknownVMException, InternalErrorException
     */
	public abstract void adminAssignVMtoUser(String user, String VMname) throws UnknownUserException, UnknownVMException, InternalErrorException;

	// User VM actions //
    /** Shutdown a VM.
     * @param VMname VM name
     * @param force Power off/destroy the VM
     * @throws UnknownVMException, InternalErrorException, UnknownUserException
     */
	public abstract void shutdownVM(String VMname, boolean force) throws UnknownVMException, InternalErrorException, UnknownUserException;

    /** Suspend a running VM.
     * @param VMname VM name
     * @throws UnknownVMException, InternalErrorException, UnknownUserException 
     */
	public abstract void suspendVM(String VMname) throws UnknownVMException, InternalErrorException, UnknownUserException;

    /** Resume a suspended VM.
     * @param VMname VM name
     * @throws UnknownVMException, InternalErrorException, UnknownUserException 
     */
	public abstract void resumeVM(String VMname) throws UnknownVMException, InternalErrorException, UnknownUserException;

	 /** Get the IP of a VM.
     * @param VMname VM name
     * @return The VM's IP
     * @throws InternalErrorException, UnknownVMException, UnknownUserException 
     */
	public abstract String getVMIP(String VMname) throws InternalErrorException, UnknownVMException, UnknownUserException;

    /** Get information on a running VM.
     * @param VMname VM name
     * @return The XML containing all Info
     * @throws InternalErrorException, UnknownVMException, UnknownUserException 
     */
	public abstract String getVMInfo(String VMname) throws InternalErrorException, UnknownVMException, UnknownUserException;

    /** Get the status of a running VM.
     * @param VMname VM name
     * @return The status (STAGING, RUNNING, FAILING)
     * @throws InternalErrorException, UnknownVMException, UnknownUserException
     */
	public abstract String getVMStatus(String VMname) throws UnknownVMException, InternalErrorException, UnknownUserException;

    /** Get a listing of all VMs owned by the caller.
     * @return The list of VM names
     * @throws InternalErrorException
     */
	public abstract String[] getVMlist() throws InternalErrorException;

    /** Get a listing of all VMs owned by a user.
     * @param user The user owning the VMs
     * @return The list of VM names
     * @throws InternalErrorException
     */
	public abstract String[] adminGetUserVMlist(String user) throws InternalErrorException;
	
	///////////////// VM & Scripting ///////////////////////
	// User Methods //
    /** Run a script on a VM.
     * @param user The user owning the script
     * @param script The script to be executed
     * @param VMname The VM to execute the script in
     * @throws InternalErrorException, VMContactErrorException, UnknownVMException
     */
	public abstract void adminApplyScript(String user, String script,
			String VMname) throws InternalErrorException, VMContactErrorException, UnknownVMException;
	
    /** Run a user script on a VM.
     * @param script The script to be executed
     * @param VMname The VM to execute the script in
     * @throws InternalErrorException, VMContactErrorException, UnknownVMException, UnknownUserException 
     */
	public abstract void applyScript(String script, String VMname) throws InternalErrorException, VMContactErrorException, UnknownVMException, UnknownUserException;

    /** Run a shell command on a VM.
     * @param cmd The shell command
     * @param VMname The VM to execute the script in
     * @return The stdout and stderr strings
     * @throws InternalErrorException, VMContactErrorException, UnknownVMException
     */
	public abstract String[] adminExecCMD(String cmd, String VMname) throws InternalErrorException, VMContactErrorException, UnknownVMException;

    /** Run a shell command on a VM.
     * @param cmd The shell command
     * @param VMname The VM to execute the script in
     * @return The stdout and stderr strings
     * @throws InternalErrorException, VMContactErrorException, UnknownVMException, UnknownUserException
     */
	public abstract String[] execCMD(String cmd, String VMname) throws InternalErrorException, VMContactErrorException, UnknownVMException, UnknownUserException;

	//////////////// Script management ///////////////////////
	// User methods //
    /** Get a list of all scripts of a single user.
     * @param user The user owning the script
     * @return The names of user scripts
     */
	public abstract String[] adminGetScriptList(String user);

    /** Get a list of all scripts of the calling user.
     * @return The names of user scripts
     */
	public abstract String[] getScriptList();

    /** Add a script to a user's collection of scripts.
     * @param user The user that will own the script
     * @param name The script name
     * @param content The content of the script
     * @param description A short description of the script
     * @throws DirectoryException
     */
	public abstract void adminAddScript(String user, String name,
			String content, String description) throws DirectoryException;

    /** Add a script to the caller's collection of scripts.
     * @param name The script name
     * @param content The content of the script
     * @param description A short description of the script
     * @throws DirectoryException
     */
	public abstract void addScript(String name, String content,
			String description) throws DirectoryException;

    /** Get description of a script.
     * @param user The user owning the script
     * @param name The name of the script
     * @return The description of the script
     * @throws UnknownScriptException
     */
	public abstract String adminGetDescription(String user, String name) throws UnknownScriptException;

    /** Get description of a script owning the script.
     * @param name The name of the script
     * @return The description of the script
     * @throws UnknownScriptException
     */
	public abstract String getDescription(String name) throws UnknownScriptException;

    /** Remove a user script.
     * @param name The name of the script
     * @throws UnknownScriptException, DirectoryException
     */
	public abstract void removeScript(String name) throws UnknownScriptException, DirectoryException;

	// admin methods //
    /** Remove a user script.
     * @param user The user owning the script
     * @param name The name of the script
     * @throws UnknownScriptException, DirectoryException
     */
	public abstract void adminRemoveScript(String user, String name) throws UnknownScriptException, DirectoryException;

    /** Synchronize the DB with the contents of the file-system where scripts are stored.
     * @param user The user owning the script
     * @throws UnknownUserException
     */
	public abstract void adminSyncUserScripts(String user) throws UnknownUserException;

    /** Synchronize the DB with the contents of the file-system where scripts of the caller users are stored.
     * @throws UnknownUserException
     */
	public abstract void syncScripts() throws UnknownUserException;

    ////////////////// Template management /////////////////////
	// User methods //
    /** Transfer a template to another location.
     * @param template The template to be transfered
     * @param target The new name of the template
     * @param move True if you want to move the template, false if you want to copy it
     * @throws InternalErrorException
     */
	public abstract void transferTemplate(String template, String target,
			boolean move) throws InternalErrorException;

    /** Transfer a template to another location.
     * @param user The user currently owning the VM 
     * @param template The template to be transfered
     * @param target The new owner of the template
     * @param move True if you want to move the template, false if you want to copy it
     * @throws InternalErrorException
     */
	public abstract void adminTransferTemplate(String user, String template,
			String target, boolean move) throws InternalErrorException;
 
	/** Get the status of a template.
	 * @param template The template name
	 * @return "pending", "ready" or "failed"
	 */
	public abstract String getTemplateStatus(String template) throws UnknownTemplateException, MultipleTemplatesException;

	
	/** Get the status of a template.
	 * @param template The template name
	 * @return "pending", "ready" or "failed"
	 */
    public abstract String getPublicTemplateStatus(String template) throws UnknownTemplateException, MultipleTemplatesException;

	
	/** Get the status of a template.
	 * @param user The user owning the template
	 * @param template The template name
	 * @return "pending", "ready" or "failed"
	 */
	public abstract String adminGetTemplateStatus(String user, String template) throws UnknownTemplateException, MultipleTemplatesException;

	/**
	 * Remove a template.
	 * @param name The template name 
	 * @throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException
	 */
	public abstract void removeTemplate(String name) throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException;

	/**
	 * Remove a template.
	 * @param user The user owning the template
	 * @param name The template name 
	 * @throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException
	 */
	public abstract void adminRemoveTemplate(String user, String name) throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException;

	/**
	 * Get list of template names owned by the calling user.
	 * @return the template names
	 */
	public abstract String[] getTemplates();

	/**
	 * Get list of template names owned by a user.
	 * @param user The owner of templates
	 * @return the template names
	 */
	public abstract String[] adminGetTemplates(String user);

	/**
	 * Get list of public template.
	 * @return list of public template names
	 */
	public abstract String[] getPublicTemplates();

	// Admin methods //
	/**
	 * Make a template public.
	 * @param template The template name
	 * @param target The new template name
	 * @throws DirectoryException, UnknownTemplateException, MultipleTemplatesException, TemplateNotReadyException, InternalErrorException
	 */
	public abstract void makeTemplatePublic(String template, String target) throws DirectoryException, UnknownTemplateException, MultipleTemplatesException, TemplateNotReadyException, InternalErrorException;

	/**
	 * Remove a public template.
	 * @param name The template name
	 * @throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException
	 */
	public abstract void adminRemovePublicTemplate(String name) throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException;

    /** Synchronize the DB with the contents of the file-system where templates of users are stored.
	 * @param user The template name
     * @throws UnknownUserException
     */
	public abstract void adminSyncUserTemplates(String user) throws UnknownUserException;

    /** Synchronize the DB with the contents of the file-system where templates of the caller users are stored.
     * @throws UnknownUserException
     */
	public abstract void syncTemplates() throws UnknownUserException;

	//////////////// Configurations ////////////////////////////
	// Admin methods //
	/** Set a configuration parameter.
     * @param key The name of the configuration parameter
     * @param value The value of the configuration parameter
     * @throws InternalErrorException
     * @throws UnknownParameter
     */
    public abstract void setConfigurationParameter(String key, String value) throws InternalErrorException, UnknownParameter;

	/** Get a configuration parameter.
     * @param key The name of the configuration parameter
     * @return the value of the configuration parameter
     * @throws InternalErrorException
     * @throws UnknownParameter
     */
    public abstract String getConfigurationParameter(String key) throws InternalErrorException, UnknownParameter;

    //////////////////////////User Mangement///////////////
	// Admin methods //
    
	/** Add a new user.
	 * @param username The username of the new user
	 * @throws ReservedUserException
	 * @throws DirectoryException
	 */
	public abstract void addUser(String username) throws ReservedUserException, DirectoryException;

	/** Remove a user of the system.
	 * @param username The username of the user to delete
	 * @throws ReservedUserException
	 * @throws UnknownUserException
	 * @throws DirectoryException
	 */
	public abstract void deleteUser(String username) 
		throws ReservedUserException, UnknownUserException, DirectoryException;
	
	/** Get the list of all users.
 	 * @return the list of users
	 */
	public abstract String[] getUsers();
	
    //////////////////////////Host Mangement///////////////
	// Admin methods //
    /** Get info of a host.
     * @param hostname The hostname of the host
     * @return The XML containing all host info
     * @throws InternalErrorException
     */
    public abstract String getHostInfo(String hostname) throws InternalErrorException;
    
    /** Get the list of all hosts.
     * @return the list of all hosts
     * @throws InternalErrorException
     */
    public abstract String[] getHostList() throws InternalErrorException;
 
    /** Remove a host of the hosts pool.
     * @param hostname The hostname of the host to be deleted
     * @throws InternalErrorException
     */
    public abstract void deleteHost(String hostname) throws InternalErrorException;
    
    /** Enable/disable a host.
     * @param hostname The hostname of the host to be enabled
     * @param enable True if the host is to be enabled, false if the host id to be disabled.
     * @throws InternalErrorException
     */
    public abstract void enableHost(String hostname, boolean enable) throws InternalErrorException;

    /** Add a new host in the pool of hosts.
     * @param hostname The host to e added
     * @throws InternalErrorException
     */
    public abstract void createHost(String hostname) throws InternalErrorException, IOException;
    
    /** Migrate a VM to a specific host.
     * @param vmname The name of the VM to be migrated
     * @param hostname The hostname of the host to hold the VM
     * @throws InternalErrorException
     * @throws UnknownVMException
     */
    public abstract void migrateVM(String vmname, String hostname) throws InternalErrorException, UnknownVMException;
    
    ///////////////////////////VNet Management /////////////
    // Admin Methods //
    /** Get the list of a user's virtual networks. 
     * @param user The username of the user owning the VNets
     * @return the list of user VNets
     * @throws InternalErrorException
     */
    public abstract String[] adminGetVNetList(String user) throws InternalErrorException ;
    
    /** Get the list of all virtual networks.
     * @return the list of all VNets
     * @throws InternalErrorException
     */
    public abstract String[] adminGetAllVNetList() throws InternalErrorException ;
    
    /** Get info regarding the VNet.
     * @param VNname The name of the VNet
     * @return the VNet info
     * @throws InternalErrorException
     * @throws UnknownVNException
     */
    public abstract String adminGetVNetInfo(String VNname) throws InternalErrorException, UnknownVNException ;
    
    /** Create a VNet.
     * @param VNname The name of the VNet
     * @param subnet The subnet the VNet will in 
     * @throws InternalErrorException 
     * @throws VNExistsException
     */
    public abstract void adminCreateVNet( String VNname, int type, String Description) throws InternalErrorException, VNExistsException;
    
    /** Remove a virtual network.
     * @param VNname The name of the VNet
     * @throws InternalErrorException
     * @throws UnknownVNException
     */
    public abstract void adminRemoveVNet(String VNname) throws InternalErrorException, UnknownVNException  ;
    
    /**
     * Assign a VNet to a user.
     * @param user The user that will hold the VNet ownership
     * @param VNname The name of the VNet
     * @throws UnknownUserException
     * @throws UnknownVNException
     * @throws InternalErrorException
     */
    public abstract void adminAssignVNettoUser(String user, String VNname) throws UnknownUserException, UnknownVNException, InternalErrorException ;

    // User Methods //
    /** Get the list of virtual networks owned by the caller.
     * @return the list of all VNets
     * @throws InternalErrorException
     */
    public abstract String[] getVNetList() throws InternalErrorException ;

    /** Get the list of public virtual networks owned by the caller.
     * @return the list of all public VNets
     * @throws InternalErrorException
     */
    public abstract String[] getVNetPublicList() throws InternalErrorException ;

    /** Get info regarding a VNet owned by the caller.
     * @param VNname The name of the VNet
     * @return the VNet info
     * @throws InternalErrorException
     * @throws UnknownVNException
     */
    public abstract String getVNetInfo(String VNname) throws InternalErrorException, UnknownVNException, UnknownUserException ;

    /** Create a VNet.
     * @param VNname The name of the VNet
     * @param subnet The subnet the VNet will in 
     * @throws InternalErrorException 
     * @throws VNExistsException
     */
    public abstract void createVNet( String VNname, int type, String Description) throws InternalErrorException, VNExistsException ;

    /** Remove a virtual network.
     * @param VNname The name of the VNet
     * @throws InternalErrorException
     * @throws UnknownVNException
     */
    public abstract void removeVNet(String VNname) throws InternalErrorException, UnknownVNException, UnknownUserException;
    
    
    /**
     * Update a property site
     * @param site The site
     * @param property The property to be updated
     * @param value The value of the property
     * @throws InternalErrorException
     * @throws IOException
     */
    public void adminUpdateSite(String site, String property, String value)
    	throws InternalErrorException, IOException;

    /**
     * Get all site info in xml 
     * @param site The site
     * @return The xml description of the site
     * @throws InternalErrorException
     * @throws IOException
     */
    public String adminGetSiteInfo(String site) throws InternalErrorException, IOException;

    /**
     * Get info on a property of a site
     * @param site The site
     * @param property The property under question
     * @return the value of the property
     * @throws InternalErrorException
     * @throws IOException
     */
    public String adminReadSite(String site, String property)
	throws InternalErrorException, IOException;
    
    /** Create a new site.
     * @param site The site name
     * @throws InternalErrorException
     */
    public abstract void adminCreateSite(String site) throws InternalErrorException;

    /** Remove a new site.
     * @param site The site name
     * @throws InternalErrorException
     */
    public abstract void adminDeleteSite(String site) throws InternalErrorException;

    /** Assign a host to a site.
     * @param hostname The name of the host
     * @param site The site name
     * @throws InternalErrorException
     */
    public abstract void adminAssignHostToSite(String hostname, String site) throws InternalErrorException;

    /** Un-associate a host from its site. The Host returns to the default site. 
     * @param hostname The name of the host
     * @throws InternalErrorException
     */
    public abstract void adminMoveHostToDefaultSite(String hostname) throws InternalErrorException;

    public abstract String[] adminGetHostsofSite(String site) throws InternalErrorException ;

    public abstract String[] adminGetSites() throws InternalErrorException;

    
    
    //Needed by Nefeli to retrieve the host's site
    public abstract Host getHost(String hostname) throws InternalErrorException;

}
