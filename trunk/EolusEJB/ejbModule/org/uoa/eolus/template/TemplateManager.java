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
package org.uoa.eolus.template;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;

import org.opennebula.bridge.CloudConstants.HostState;
import org.uoa.eolus.DirectoryException;
import org.uoa.eolus.InternalErrorException;
import org.uoa.eolus.config.ConfigurationsManager;
import org.uoa.eolus.host.Host;
import org.uoa.eolus.host.HostManager;
import org.uoa.eolus.host.Site;
import org.uoa.eolus.user.UnknownUserException;
import org.uoa.eolus.user.User;
import org.uoa.nefeli.utils.HostInfo;

/**
 * 
 * @author jackal
 */
public class TemplateManager {

	Nest n = null;
	EntityManager em = null;
	TemplatesRepo tempsinfo = null;

	public TemplateManager(EntityManager em) {
		this.em = em;
		Init();
	}

	private void Init() {
		ConfigurationsManager cm = new ConfigurationsManager(em);
		tempsinfo = cm.getTemplatesRepo();
		if (tempsinfo == null)
			tempsinfo = new TemplatesRepo();

		n = new Nest(tempsinfo.getTemplatesRepo(), tempsinfo.getTemplate2VMConfDir(),
				tempsinfo.getKernel(), tempsinfo.getInitrd());
		syncWithRepo();
	}

	public Boolean syncWithRepo() {
		String[] users = n.getAllUsersOfRepo();
		for (int j = 0; j < users.length; j++) {
			try {
				syncUserTemplates(users[j]);
			} catch (UnknownUserException e) {
				System.out.println("Unknown user "+users[j]+" proceeding.");
				//e.printStackTrace();
			}
		}

		return true;
	}

	public void transferTemplate(String user, String template,
			String target, Boolean move) throws InternalErrorException {

		try {
			sendTemplateMessage("TemplateTransfer", user, template, target, move);
		} catch (Exception e) {
			throw new InternalErrorException("Error dispatching new template creation job.", e);
		}

		if (move) {
			List<Template> ts = em.createQuery(
					"select ts from Template ts where ts.name='" + template
					+ "' and ts.user.id='" + user + "'")
					.getResultList();
			for (Object m : ts) {
				Template t = (Template) m;
				t.setName(target);
				t.setStatus("Pending");
			}
		} else {
			Template t = new Template();
			t.setName(target);
			User u = em.find(User.class, user);
			t.setUser(u);
			t.setStatus("Pending");
			em.persist(t);
		}

	}

	private void sendTemplateMessage(String action, String user, String template, String target, boolean move)
	throws Exception{

		Context initial = new InitialContext();
		ConnectionFactory cf = (ConnectionFactory)initial.lookup("ConnectionFactory");
		Destination notify = (Destination)initial.lookup("queue/EolusQueue");
		Connection connection = cf.createConnection();
		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(notify);
		StreamMessage message = session.createStreamMessage();
		message.setStringProperty("Action", action);
		message.setStringProperty("User", user);
		message.setStringProperty("Template", template);
		message.setStringProperty("Target", target);
		message.setBooleanProperty("Move", move);
		message.setStringProperty("Repo", tempsinfo.getTemplatesRepo());
		message.setStringProperty("VMteplates", tempsinfo.getTemplate2VMConfDir());
		message.setStringProperty("Kernel", tempsinfo.getKernel());
		message.setStringProperty("Initrd", tempsinfo.getInitrd());
		producer.send(message);
		System.out.println("Sending message");
	}

	public void makeTemplatePublic(String user, String template, String target)
	throws DirectoryException, UnknownTemplateException, MultipleTemplatesException, TemplateNotReadyException, InternalErrorException {

		User us = em.find(User.class, "public");
		if (us == null){
			System.out.println("Public user does not exist");
			addUser("public");
		}
		Template t = null;
		List<Template> ts = em.createQuery(
				"select ts from Template ts where ts.name='" + template
				+ "' and ts.user.id='" + user + "'").getResultList();
		if (ts.size() <= 0){
			System.out.println("No template found");
			throw new UnknownTemplateException("Template not found.");
		}
		if (ts.size() > 1) {
			System.out.println("Multiple templates found");
			throw new MultipleTemplatesException("Template "+template+ " has multiple entries.");
		}
		for (Object m : ts) {
			t = (Template) m;
			if (!t.getStatus().equalsIgnoreCase("Ready")){
				System.out.println("Template not ready");
				throw new TemplateNotReadyException("Tempalte "+template+" not ready.");
			}			
		}

		n.copyUserToUserTemplate(user, "public", template, target, true);
		t.setName(target);
		t.setUser(us);
	}

	public void removeTemplate(String user, String name) throws DirectoryException, MultipleTemplatesException, TemplateNotReadyException {

		List<Template> ts = em.createQuery(
				"select ts from Template ts where ts.name='" + name
				+ "' and ts.user.id='" + user + "'").getResultList();
		if (ts.size() > 1) {
			System.out.println("Multiple templates found");
			throw new MultipleTemplatesException("Multiple entries for template "+name+".");
		}
		for (Object m : ts) {
			Template t = (Template) m;
			if (!t.getStatus().equalsIgnoreCase("Ready")){
				System.out.println("Template not ready");
				throw new TemplateNotReadyException("Template "+name+" not ready.");
			}
			em.remove(t);
		}

		n.removeUserTemplate(user, name);
	}

	public List<String> getTemplates(String user) {
		List<String> names = new ArrayList<String>();
		System.out.println("Looking for Templates");
		List<Template> ts = em.createQuery(
				"select ts from Template ts where ts.user.id='" + user + "'")
				.getResultList();
		System.out.println("Templates found " + ts.size());
		for (Object m : ts) {
			Template t = (Template) m;
			System.out.println("To return template: " + t.getName());
			names.add(t.getName());
		}
		return names;
	}

	public void syncUserTemplates(String user) throws UnknownUserException{
		User us = em.find(User.class, user);
		if (us == null) {
			System.out.println("User " + user + " not found.");
			throw new UnknownUserException("User "+user+ " does not exist.");
		}

		String[] names = n.getAllUserTemplates(user);

		// check repo against DB
		for (int i = 0; names != null && i < names.length; i++) {
			System.out.println("Looking for template: " + user + "/"
					+ names[i]);
			List<Template> ts = em.createQuery(
					"select ts from Template ts where ts.name='" + names[i]
					      + "' and ts.user.id='" + user + "'").getResultList();
			if (ts.size() != 1) {
				System.out.println("Adding template: " + names[i]+ " assuming status Ready");
				Template t = new Template();
				t.setStatus("Ready");
				t.setName(names[i]);
				t.setUser(us);
				em.persist(t);
			}
			if (ts.size() == 1) {
				Template t  = ts.get(0);
				if (t.getStatus().equalsIgnoreCase("noStatus")){
					System.out.println("Uninitialized template: " + names[i]+ " assuming status Ready");					
				}
				t.setStatus("Ready");
			}
		}

		// check DB against repo
		List<Template> ts = em.createQuery(
				"select ts from Template ts where ts.user.id='" + user + "'")
				.getResultList();
		System.out.println("Templates stored: " + ts.size());
		for (Object m : ts) {
			Template t = (Template) m;
			String name = t.getName();
			boolean found = false;
			System.out.println("Looking for dir name template: " + name);
			for (int i = 0; names != null && i < names.length; i++) {
				if (name.equals(names[i])) {
					System.out.println("found template: " + names[i]);
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println("Removing DB template: " + name);
				if (t.getStatus().equals("Ready"))
					em.remove(t);
				else
					System.out.println("Template "+t.getName()+ " has status "+t.getStatus()+
					" but no corresponding DB entry.");
			}
		}
	}

	synchronized public File createTemplateDesc(HostManager hostManager, String user, String VMclass, String VMname,
			Integer cores, Integer memSize, String[] nets) 
	throws UnknownTemplateException, TemplateNotReadyException, DirectoryException, InternalErrorException, IOException {
		return createTemplateDesc(hostManager, user, VMclass, VMname, null, cores, memSize, nets);
	}

	public File createTemplateDesc(HostManager hostManager, String user, String VMclass, String VMname,
			String host, Integer cores, Integer memSize, String[] nets)
	throws UnknownTemplateException, TemplateNotReadyException, DirectoryException, InternalErrorException, IOException {
		//Try to find template in user's template collection
		//Then try public templates
		String VMtemplateOwner = "";
		List<Template> ts = em.createQuery(
				"select ts from Template ts where ts.name='" + VMclass
				+ "' and ts.user.id='" + user + "'").getResultList();
		if (ts.size() != 1) {
			System.out.println("Template "+VMclass+" not found ("+ts.size()+") in user's collection.");
			ts = em.createQuery(
					"select ts from Template ts where ts.name='" + VMclass
					+ "' and ts.user.id='public'").getResultList();
			if (ts.size() != 1) {
				throw new UnknownTemplateException("Template " +VMclass+ " not found.");
			}else{
				VMtemplateOwner = "public";
			}
		}else{
			VMtemplateOwner = user;
		}
		for (Object m : ts) {
			Template t = (Template) m;
			if (!t.getStatus().equalsIgnoreCase("Ready")){
				System.out.println("Template not ready");
				throw new TemplateNotReadyException("Template " +VMclass+ " not ready.");
			}
		}

		List<String> sites = n.getSiteConfigurations(VMtemplateOwner, VMclass);
		if (sites.size() == 0 )
			throw new InternalErrorException("No site can serve the requested template.");				
		if (host != null){
			Host hs = em.find(Host.class, host);
			if (hs == null){
				throw new InternalErrorException("Host "+host+" does not exist.");
			}

			String hstr = hostManager.getHostInfo(host);
			org.opennebula.bridge.HostInfo hi = new org.opennebula.bridge.HostInfo();
			hi.fromXML(hstr);
			if (hi.state == HostState.DISABLED || hi.state == HostState.FAILED)
				throw new InternalErrorException("Host "+host+" not ready.");
			String sitename = hs.getSitename();
			if (!sites.contains("Site-"+sitename)){
				throw new InternalErrorException("Host "+host+" belongs to a site that cannot host the requested template.");				
			}
			return n.scheduleVMtoHost(VMtemplateOwner, VMclass, VMname, "Site-"+sitename,host, cores, memSize, nets);
		}else{
			n.startNewVMScheduling();
			for (String s : sites){
				String sitename = s.substring("Site-".length());
				List<HostInfo> hi = GatherDetails(sitename, hostManager);
				n.loadHostInfo(s,hi);
			}
			return n.newVMScheduling(VMtemplateOwner, VMclass, VMname, cores, memSize, nets);
		}
	}

	public List<HostInfo> GatherDetails(String site, HostManager hostManager)
	{
		List<HostInfo> hosts = new ArrayList<HostInfo>();
		Site s = em.find(Site.class, site);
		if (s == null){
			System.out.println("Site "+site+" does not exist.");
			return hosts;
		}
		for (String h : s.getHosts() ){
			String histr;
			try {
				histr = hostManager.getHostInfo(h);
			} catch (InternalErrorException e) {
				e.printStackTrace();
				continue;
			}
			org.opennebula.bridge.HostInfo hi = new org.opennebula.bridge.HostInfo();
			hi.fromXML(histr);
			if (hi.state == HostState.DISABLED || hi.state == HostState.FAILED)
				continue;
			HostInfo host = new HostInfo();
			host.name = h;
			host.CPU_free = Double.parseDouble(hi.freecpu);
			host.CPU_total = Double.parseDouble(hi.totalcpu);
			host.Mem_free = Integer.parseInt(hi.freemem);
			host.Mem_total = Integer.parseInt(hi.totalmem);
			host.site = site;
			hosts.add(host); 
		}
		return hosts;
	}

	public void makeTemplate(String user, int VMID, String template,
			String ONEdir) throws InterruptedException, IOException {
		String cmd = "mkdir " + tempsinfo.getTemplatesRepo() + "/" + user + "/." + template + " ; cp "
		+ ONEdir + "/var/" + VMID + "/images/disk.1 " + tempsinfo.getTemplatesRepo() + "/"
		+ user + "/." + template + "/disk.img ; mv " + tempsinfo.getTemplatesRepo() + "/"
		+ user + "/." + template + " " + tempsinfo.getTemplatesRepo() + "/" + user + "/"
		+ template + "  ; exit; \n";
		System.out.println("CMD: " + cmd);
		Runtime run = Runtime.getRuntime();
		Process child = run.exec("/bin/bash");
		BufferedWriter outCommand = new BufferedWriter(new OutputStreamWriter(
				child.getOutputStream()));
		outCommand.write(cmd);
		outCommand.flush();
		InputStream out = child.getInputStream();
		InputStreamReader isr = new InputStreamReader(out);
		BufferedReader output = new BufferedReader(isr);
		InputStream err = child.getErrorStream();
		InputStreamReader eisr = new InputStreamReader(err);
		BufferedReader error = new BufferedReader(eisr);
		child.waitFor();
		String o = "";
		String s;
		while ((s = output.readLine()) != null) {
			o += s + "\n";
		}
		System.out.println("stdout: " + o);
		String e = "";
		s = error.readLine(); // First line is a warning: Warning:
		// Permanently added 'XX.XX.XX.XX' (RSA) to
		// the list of known hosts.
		while ((s = error.readLine()) != null) {
			e += s + "\n";
		}
		System.out.println("stderr: " + e);
		syncWithRepo();
		// Template created
		System.out.println("Template created");
	}

	public void addUser(String user) throws DirectoryException {
		n.createUser(user);
	}

	public void removeUser(String user) throws DirectoryException {
		n.removeUser(user);
	}

	public void addUsers(List<String> userNames) {
		for (String user : userNames){
			try {
				addUser(user);
			} catch (DirectoryException e) {
				System.out.println("Template directory for user "+user+" failed. Proceeding....");
				//e.printStackTrace();
			}
		}
	}

	public String getTemplateStatus(String user, String template) 
	throws UnknownTemplateException, MultipleTemplatesException {
		List<Template> ts = em.createQuery(
				"select ts from Template ts where ts.name='" + template
				+ "' and ts.user.id='" + user + "'").getResultList();
		if (ts.size() != 1) {
			System.out.println("Multiple templates found");
			throw new MultipleTemplatesException("Multiple entries for template "+template+".");
		}
		for (Object m : ts) {
			Template t = (Template) m;
			return t.getStatus();
		}
		throw new UnknownTemplateException("Template "+ template +" not found.");
	}

}
