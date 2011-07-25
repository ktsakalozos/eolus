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

package org.uoa.eolus.template;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.StreamMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.uoa.eolus.DirectoryException;

/**
 * Message-Driven Bean implementation class for: TemplateJini
 *
 */
@SecurityDomain(value = "JBossWS")
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"
		) })
public class TemplateGenie implements MessageListener {

	@PersistenceContext
	EntityManager em;
	
	public TemplateGenie() {
		// TODO Auto-generated constructor stub
	}

	public void onMessage(Message message) {

		try {
			StreamMessage msg = (StreamMessage)message;

			String action = msg.getStringProperty("Action");
			String user = msg.getStringProperty("User");
			String template = msg.getStringProperty("Template");
			String target = msg.getStringProperty("Target");
			boolean move = msg.getBooleanProperty("Move");
			String repo = msg.getStringProperty("Repo");
			String VMsTemplates = msg.getStringProperty("VMteplates");
			String kernel = msg.getStringProperty("Kernel");
			String initrd = msg.getStringProperty("Initrd");

			System.out.println("Got the Msg:" + msg);

			int sleeps = 0;
			Template t = null;
			while(t == null){
				List<Template> ts = em.createQuery(
						"select ts from Template ts where ts.name='" + target
								+ "' and ts.user.id='" + user + "' and ts.status='Pending'")
								.getResultList();
				if (ts.size() > 1){
					System.out.println("Multiple instances of Template "+target);
					return;
				} if (ts.size() == 0){
					if (sleeps >= 20){
						System.out.println("Cannot find entry for template "+target+" after 20*5 secs.");
						return;						
					}
					System.out.println("Template entry "+template+"not ready yet. Waiting 5 secs.");
					try {
						Thread.currentThread().sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sleeps++;
				}else{				
					t = (Template)ts.get(0);
					t.setName(target);
					Nest n = new Nest(repo, VMsTemplates, kernel, initrd);
					Boolean res;
					try {
						n.copyUserTemplate(user, template, target, move);
						t.setStatus("Ready");
					} catch (DirectoryException e) {
						t.setStatus("Failed");
						System.out.println("Failed to copy template!"); 
						e.printStackTrace();
					}
				}
			}
			
			System.out.println("Done with template handling.");
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}


