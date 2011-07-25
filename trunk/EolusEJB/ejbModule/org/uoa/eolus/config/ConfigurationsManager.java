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
package org.uoa.eolus.config;

import javax.persistence.EntityManager;

import org.opennebula.bridge.CloudClient;
import org.uoa.eolus.script.ScriptRepo;
import org.uoa.eolus.template.TemplatesRepo;

/**
 *
 * @author jackal
 */
public class ConfigurationsManager {

    EntityManager em = null;

    public ConfigurationsManager(EntityManager em) {
        this.em = em;
	}
    
    public ONEInfo getONEInfo() {
        return em.find(ONEInfo.class,"current");
    }

    public static CloudClient getCloudConnector(EntityManager em) 
    		throws Exception{
        ONEInfo one = em.find(ONEInfo.class,"current");
        if (one == null){
        	System.out.println("Current configuration of OpenNebula connector does not exist. Falling back to defaults.");
        	one = new ONEInfo();
        }
        return new CloudClient();
    }
    
    public Boolean setONEInfo(ONEInfo cur) {
    	ONEInfo inf = em.find(ONEInfo.class,"current");
        if (inf == null) {
        	cur.setId("current");
            em.persist(cur);
            return true;
        }else{
            inf.cloneFrom(cur);
            em.persist(inf);
            return true;
        }
    }
    
    public ScriptRepo getScriptRepo() {
        return em.find(ScriptRepo.class,"current");
    }

    public Boolean setScriptRepo(ScriptRepo cur) {
    	ScriptRepo inf = em.find(ScriptRepo.class,"current");
        if (inf == null) {
        	cur.setId("current");
            em.persist(cur);
            return true;
        }else{
            inf.cloneFrom(cur);
            em.persist(inf);
            return true;
        }
    }

    public TemplatesRepo getTemplatesRepo() {
        return em.find(TemplatesRepo.class,"current");
    }

    public Boolean setTemplatesRepo(TemplatesRepo cur) {
    	TemplatesRepo inf = em.find(TemplatesRepo.class,"current");
        if (inf == null) {
        	cur.setId("current");
            em.persist(cur);
            return true;
        }else{
            inf.cloneFrom(cur);
            em.persist(inf);
            return true;
        }
    }

	public void updateConfigParam(String key, String value) throws UnknownParameter {
		if (key.startsWith("ONE")){
			ONEInfo newconf = getONEInfo();
			if (newconf == null)
				newconf = new ONEInfo();

			if (key.equalsIgnoreCase("ONEnetbridge")){	
				newconf.setNetBridge(value);
			} else if (key.equalsIgnoreCase("ONEdir")){	
				newconf.setONEdir(value);
			} else {
				throw new UnknownParameter(key + " is not a valid configuration parameter");
			}
			setONEInfo(newconf);
		} else if (key.startsWith("Script")){
			ScriptRepo newconf = getScriptRepo();
			if (newconf == null)
				newconf = new ScriptRepo();

			if (key.equalsIgnoreCase("ScriptRepo")){	
				newconf.setScriptsRepo(value);
			} else {
				throw new UnknownParameter(key + " is not a valid configuration parameter");
			}
			setScriptRepo(newconf);
		} else if (key.startsWith("Template")){
			TemplatesRepo newconf = getTemplatesRepo();
			if (newconf == null)
				newconf = new TemplatesRepo();

			if (key.equalsIgnoreCase("TemplateTmpDir")){	
				newconf.setTemplate2VMConfDir(value);
			} else if (key.equalsIgnoreCase("TemplateRepo")){	
				newconf.setTemplatesRepo(value);
			} else if (key.equalsIgnoreCase("TemplateKernel")){	
				newconf.setKernel(value);
			} else if (key.equalsIgnoreCase("TemplateInitrd")){	
				newconf.setInitrd(value);
			} else {
				throw new UnknownParameter(key + " is not a valid configuration parameter");
			}
			setTemplatesRepo(newconf);
		} else {
			throw new UnknownParameter(key + " is not a valid configuration parameter");
		}
		
	}

	public String getConfigParam(String key) throws UnknownParameter {
		if (key.startsWith("ONE")){
			ONEInfo newconf = getONEInfo();
			if (newconf == null)
				newconf = new ONEInfo();

			if (key.equalsIgnoreCase("ONEnetbridge")){	
				return newconf.getNetBridge();
			} else if (key.equalsIgnoreCase("ONEdir")){	
				return newconf.getONEdir();
			} else {
				throw new UnknownParameter(key + " is not a valid configuration parameter");
			}
		} else if (key.startsWith("Script")){
			ScriptRepo newconf = getScriptRepo();
			if (newconf == null)
				newconf = new ScriptRepo();

			if (key.equalsIgnoreCase("ScriptRepo")){	
				return newconf.getScriptsRepo();
			} else {
				throw new UnknownParameter(key + " is not a valid configuration parameter");
			}
		} else if (key.startsWith("Template")){
			TemplatesRepo newconf = getTemplatesRepo();
			if (newconf == null)
				newconf = new TemplatesRepo();

			if (key.equalsIgnoreCase("TemplateTmpDir")){	
				return newconf.getTemplate2VMConfDir();
			}
			if (key.equalsIgnoreCase("TemplateRepo")){	
				return newconf.getTemplatesRepo();
			}
			if (key.equalsIgnoreCase("TemplateKernel")){	
				return newconf.getKernel();
			}
			if (key.equalsIgnoreCase("TemplateInitrd")){	
				return newconf.getInitrd();
			} else {
				throw new UnknownParameter(key + " is not a valid configuration parameter");
			}
		} else {
			throw new UnknownParameter(key + " is not a valid configuration parameter");
		}
	}

}
