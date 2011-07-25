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

package org.uoa.eolus.user;


 
import javax.persistence.EntityManager;


import java.util.List;

/**
 *
 * @author jackal
 */
public class UserManager {

    EntityManager em = null;

    public UserManager(EntityManager em) {
    	this.em = em;
		User publicUser = em.find(User.class, "public");
		if (publicUser == null){
			publicUser = new User();
			publicUser.setId("public");
			em.persist(publicUser);
		}
		User privateuser = em.find(User.class, "private");
		if (privateuser == null){
			privateuser = new User();
			privateuser.setId("private");
			em.persist(privateuser);
		}
    }
 
    public void addUser(String userName) throws ReservedUserException {
    	if (userName.equalsIgnoreCase("public") || userName.equalsIgnoreCase("private"))
    		throw new ReservedUserException(userName+" is a reserved username.");

    	User u = em.find(User.class, userName);
    	if (u!=null)
    		throw new ReservedUserException(userName+" already exists.");

    	u = new User();
    	u.setId(userName);
    	em.persist(u);
    }

    public void deleteUser(String userName) throws ReservedUserException, UnknownUserException {
    	if (userName.equalsIgnoreCase("public") || userName.equalsIgnoreCase("private"))
    		throw new ReservedUserException(userName+" is a reserved username.");
    	
        User userx = em.find(User.class, userName);
        if (userx ==null)
        	throw new UnknownUserException("User "+userName+" does not exist.");
        
        em.remove(userx); 
    }

    public List<String> getUserNames(){
    	return em.createQuery("select u.id from User u").getResultList();
    }
    
}
