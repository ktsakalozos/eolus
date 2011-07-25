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

import javax.persistence.EntityManager;

import org.uoa.eolus.config.ConfigurationsManager;
import org.uoa.eolus.host.HostManager;
import org.uoa.eolus.net.VirtualNetManager;
import org.uoa.eolus.script.ScriptManager;
import org.uoa.eolus.template.TemplateManager;
import org.uoa.eolus.user.UserManager;
import org.uoa.eolus.vm.VirtualMachineManager;

public class Managers {

	private static UserManager userManagerInstance = null;
	private static ConfigurationsManager configManagerInstance = null;
	private static ScriptManager scriptManagerInstance = null;
	private static VirtualMachineManager virtualMachineManagerInstance = null;
	private static TemplateManager templateManagerInstance = null;
	private static HostManager hostManagerInstance = null;
	private static VirtualNetManager virtualNetManagerInstance = null;

	public static HostManager getHostManager(EntityManager em) throws InternalErrorException {
		if (hostManagerInstance == null) {
			hostManagerInstance = new HostManager(em);
		}
		return hostManagerInstance;
	}
	
	public static UserManager getUserManager(EntityManager em) {
		if (userManagerInstance == null) {
			userManagerInstance = new UserManager(em);
		}
		return userManagerInstance;
	}

	public static ConfigurationsManager getConfigurationsManager(EntityManager em) {
		if (userManagerInstance == null) {
			userManagerInstance = new UserManager(em);
		}
		if (configManagerInstance == null) {
			configManagerInstance = new ConfigurationsManager(em);
		}
		return configManagerInstance;
	}

	public static ScriptManager getScriptManager(EntityManager em) {
		if (userManagerInstance == null) {
			userManagerInstance = new UserManager(em);
		}
		if (scriptManagerInstance == null) {
			scriptManagerInstance = new ScriptManager(em);
		}
		return scriptManagerInstance;
	}

	public static VirtualMachineManager getVirtualMachineManager(EntityManager em) throws InternalErrorException {
		if (userManagerInstance == null) {
			userManagerInstance = new UserManager(em);
		}
		if (virtualMachineManagerInstance == null) {
			virtualMachineManagerInstance = new VirtualMachineManager(em);
		}
		return virtualMachineManagerInstance;
	}

	public static TemplateManager getTemplateManager(EntityManager em){
		if (userManagerInstance == null) {
			userManagerInstance = new UserManager(em);
		}
		if (templateManagerInstance == null) {
			templateManagerInstance = new TemplateManager(em);
		}
		return templateManagerInstance;
	}

	public static VirtualNetManager getVirtualNetManager(EntityManager em) throws InternalErrorException{
		if (userManagerInstance == null) {
			userManagerInstance = new UserManager(em);
		}
		if (virtualNetManagerInstance == null) {
			virtualNetManagerInstance = new VirtualNetManager(em);
		}
		return virtualNetManagerInstance;
	}

}
