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

package org.uoa.nefeli;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.wsf.spi.annotation.WebContext;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.uoa.eolus.Eolus;
import org.uoa.eolus.user.User;
import org.uoa.nefeli.parser.WorkloadDesc;




@DeclareRoles(value={"admin","user"})
@Stateless
@WebContext(contextRoot="/Madgik", urlPattern="/Nefeli",
        authMethod = "BASIC",
        secureWSDLAccess = false)
@SOAPBinding(style=SOAPBinding.Style.RPC)
@SecurityDomain(value = "JBossWS")
@WebService
public class Nefeli implements NefeliRemote {

	@PersistenceContext
	EntityManager em;
	
	@Resource
	SessionContext ctx;
	
	@RolesAllowed({"admin","user"})
    @WebMethod
	public Long addWorkload(
			@WebParam(name = "workloadDescription") String w) throws Exception{

		String username = ctx.getCallerPrincipal().getName();
		System.out.println("Looking for user: "+ username);
        User user = em.find(User.class, username);
        if (user == null){
        	System.out.println("em.find could not find user "+username);
        }
		user = null;
		List<User> users_list = em.createQuery(
			"select u from User u ").getResultList();
		Iterator<User> user_it = users_list.iterator();
		while (user_it.hasNext()) {
			User u = (User) user_it.next();
        	System.out.println("Manully checking user "+u.getId());			
			if (u.getId().equals(username))
				user = u;
		}
//         em.find(User.class, username);
		if (user == null){
			System.out.println("Unknown user!");
			throw new Exception("Unknown user!");
		}

        System.out.println("Found user: "+user.getId());
        TaskflowEntry task = new TaskflowEntry();
		task.setUser(user);
		task.setDescription(w);
		task.setStatus("submitted");

		WorkloadDesc workload = task.BuildWorkloadObj();
		if (!Validate(workload))
			return new Long(-1);
		
		em.persist(task);
		task.setStatus("submitted");
		Submit(task.getId());
		return task.getId();
	}

	private void Submit(Long taskID) throws Exception{
		
		Context initial = new InitialContext();
		ConnectionFactory cf = (ConnectionFactory)initial.lookup("ConnectionFactory");
		Destination notify = (Destination)initial.lookup("queue/NefeliQueue");
		Connection connection = cf.createConnection();
		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(notify);
		ObjectMessage message = session.createObjectMessage();
		message.setObject(taskID);
		producer.send(message);
	}

	private Boolean Validate(WorkloadDesc workload) {
		// TODO Auto-generated method stub
		//User exists
		//Check the VMs belong to the user
		return true;
	}

	@RolesAllowed({"admin","user"})
    @WebMethod
	public Boolean removeWorkload(
			@WebParam(name = "workloadID") Long ID) throws Exception{

		TaskflowEntry task = em.find(TaskflowEntry.class, ID);
		if (task == null || task.getStatus().equals("finalize") || task.getStatus().equals("terminated")){
			System.out.println("Workflow with id "+ID+ " not found.");
			return false;
		}

		if (!ctx.isCallerInRole("admin")){
			String username = ctx.getCallerPrincipal().getName();
			if (username == null || (!username.equalsIgnoreCase(task.getUser().getId()))){
				return false;
			}
		}

		task.setStatus("finalize");
		em.persist(task);
		Submit(ID);
		return true;
	}
	
	@RolesAllowed({"admin","user"})
    @WebMethod
	public Boolean magicWord(
			@WebParam(name = "workloadID") Long ID,
			@WebParam(name = "reciever") Integer reciever,
			@WebParam(name = "keywordMessage") String keyWord) {
		TaskflowEntry task = em.find(TaskflowEntry.class, ID);
		if (task == null || !task.getStatus().equals("running")){
			System.out.println("Workflow with id "+ID+ " not ready.");
			return false;
		}

		if (!ctx.isCallerInRole("admin")){
			String username = ctx.getCallerPrincipal().getName();
			if (!username.equalsIgnoreCase(task.getUser().getId())){
				return false;
			}
		}
		Signal s = new Signal();
		s.setReciever(reciever);
		s.setTask(task);
		s.setWord(keyWord);
		em.persist(s);
		task.getKeyMap().add(s);
		task.setStatus("stimulated");
		em.persist(task);
		try {
			Submit(ID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
