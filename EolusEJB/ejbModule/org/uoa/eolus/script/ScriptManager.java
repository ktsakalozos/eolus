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

package org.uoa.eolus.script;

import java.util.List;
import javax.persistence.EntityManager;

import org.uoa.eolus.DirectoryException;
import org.uoa.eolus.InternalErrorException;
import org.uoa.eolus.config.ConfigurationsManager;
import org.uoa.eolus.user.UnknownUserException;
import org.uoa.eolus.user.User;

/**
 * 
 * @author jackal
 */
public class ScriptManager {

	ScriptRepo repo = new ScriptRepo();
	Scripting s = null;
	EntityManager em;

	public ScriptManager(EntityManager em) {
		this.em = em;
		Init();
	}

	private void Init() {
		ConfigurationsManager cm = new ConfigurationsManager(em);
		repo = cm.getScriptRepo();
		if (repo == null){
			repo = new ScriptRepo();
		}
		s = new Scripting(repo.getScriptsRepo());
		syncScripts();
	}

	private void syncScripts() {
		String[] users = s.getAllUsersOfRepo();
		for (int j = 0; j < users.length; j++) {
			try {
				syncUserScripts(users[j]);
			} catch (UnknownUserException e) {
				System.out.println("Unknown user "+users[j]+". Proceeding....");
				e.printStackTrace();
			}
		}

	}

	public List<String> getUserScriptList(String user) {
		List<String> res = em.createQuery("select ts.name from Script ts  where ts.user.id='"+user+"'")
				.getResultList();
		return res;
	}

	public void addScript(String user, String name, String content, String description)
			throws DirectoryException {
		Script sc = new Script();
		sc.setName(name);
		sc.setDescription(description);
		User us = em.find(User.class, user);
		if (us!=null){
			sc.setUser(us);
			s.addScript(user, name, content);
			em.persist(sc);
		}
	}

	public String getDescription(String user, String name) throws UnknownScriptException {

		List<String> sc = em.createQuery(
				"select s.description from Script s where s.name='" + name + "' and s.user.id='"+user+"'")
				.getResultList();
		if (sc.size() != 1) {
			throw new UnknownScriptException("Script " + name + "not found.");
		}
		return sc.get(0);
	}

	public void removeScript(String user, String name) 
			throws UnknownScriptException, DirectoryException {
		List<Script> sc = em.createQuery(
				"select s from Script s where s.name='" + name + "' and s.user.id='"+user+"'")
				.getResultList();
		if (sc.size() != 1) {
			throw new UnknownScriptException("Script " + name + "not found.");
		}
		em.remove(sc.get(0));

		s.removeUserScript(user, name);
	}

	public void applyScript(String user, String script, String IP) 
			throws InternalErrorException, VMContactErrorException {
		if (IP != null && !IP.equals("NONE")){
			s.applyScript(user, script, IP);
		}else{
			throw new VMContactErrorException("Probably IP("+IP+") format error.");
		}
	}

	public String[] execCMD(String cmd, String IP) 
			throws InternalErrorException, VMContactErrorException {
		if (IP != null && !IP.equals("NONE"))
			return s.runCMD(cmd, IP);
		throw new VMContactErrorException("Probably IP("+IP+") format error.");
	}
	
	public void addUser(String user) throws DirectoryException{
		s.createUser(user);
	}
	
	public void removeUser(String user) throws DirectoryException{
		s.removeUser(user);
	}

	public void addUsers(List<String> userNames) {
		for (String user : userNames){
			try {
				addUser(user);
			} catch (DirectoryException e) {
				System.out.println("User script directory failed for user "+user+".");
//				e.printStackTrace();
			}
		}
	}

	public void syncUserScripts(String user) throws UnknownUserException {
		User us = em.find(User.class, user);
		if (us == null){
			System.out.println("User " + user +" not found.");
			throw new UnknownUserException("User "+user+" dos not exist.");
		}
		String[] scripts = s.getUserScriptsList(user);
		System.out.println("For user "+user+" we found "+scripts.length+" scripts");

		// check repo against DB
		for (int i = 0; scripts != null && i < scripts.length; i++) {
			List<Script> sc = em.createQuery(
					"select s from Script s where s.name='" + scripts[i]
							+ "' and s.user.id='" + user + "'")
					.getResultList();
			if (sc.size() != 1) {
				Script t = new Script();
				t.setName(scripts[i]);
				t.setDescription("None");
				t.setUser(us);
				em.persist(t);
			}
		}

		// check DB against repo
		List<Script> sc = em.createQuery("select ts from Script ts where ts.user.id='"+user+"'")
				.getResultList();
		for (Object m : sc) {
			Script t = (Script) m;
			String name = t.getName();
			boolean found = false;
			for (int i = 0; scripts != null && i < scripts.length; i++) {
				if (name.equals(scripts[i])) {
					found = true;
					break;
				}
			}
			if (!found) {
				em.remove(t);
			}
		}
	}

}
