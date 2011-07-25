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

package org.uoa.nefeli.cloud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opennebula.bridge.CloudConstants.HostState;
import org.uoa.eolus.EolusLocal;
import org.uoa.eolus.InternalErrorException;
import org.uoa.eolus.host.Host;
import org.uoa.eolus.vm.UnknownVMException;
import org.uoa.nefeli.Deployer;
import org.uoa.nefeli.Plan;
import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;


/**
 * A mock-up implementation of an infrastructure.
 * @author Konstantinos Tsakalozos
 *
 */
public class EolusConnector implements Infrastructure{

	private EolusLocal eolus = null;
	private Deployer deployer = null; 
	
	/**
	 * Construct a mock-up infrastructure with "size" hosting nodes 
	 * @param deployer 
	 * @param size The amount of the physical nodes
	 */
	public EolusConnector(EolusLocal eolus2, Deployer deployer)
	{
		this.deployer = deployer;
		this.eolus = eolus2;
	}

	public List<HostInfo> GatherDetails()
	{
		String[] hostnames =null;
		try {
			hostnames = eolus.getHostList();
		} catch (InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<HostInfo> hostlist = new ArrayList<HostInfo>();
		for (int i = 0 ; i < hostnames.length ; i++){
			String host_info = null;
			try {
				host_info = eolus.getHostInfo(hostnames[i]);
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(host_info);
			org.opennebula.bridge.HostInfo hi = new org.opennebula.bridge.HostInfo();
			hi.fromXML(host_info);
			if (hi.state == HostState.DISABLED || hi.state == HostState.FAILED)
				continue;

			
			HostInfo host = new HostInfo();
			host.name = hostnames[i];
			host.CPU_free = Double.parseDouble(hi.freecpu);
			host.CPU_total = Double.parseDouble(hi.totalcpu);
			host.Mem_free = Integer.parseInt(hi.freemem);
			host.Mem_total = Integer.parseInt(hi.totalmem);
			try {
				Host h = eolus.getHost(hostnames[i]);
				host.site = h.getSitename();
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			hostlist.add(host); 
		}
		return hostlist;
	}

	public void Migrate(VMTemplate vm, HostInfo hostOld, HostInfo hostNew)
			throws Exception {
		eolus.migrateVM(vm.Name, hostNew.name);
		System.out.println("Migrate VM from "+hostOld.name+" to "+hostNew.name);
	}

	public int Spawn(VMTemplate vm, HostInfo host) throws Exception {
		eolus.adminCreateVM(vm.User, vm.VMtemplateName,
				vm.Name, host.name, vm.CPU, vm.Mem, vm.networks);
		return 1;	
	}

	public String getIP(VMTemplate vm) throws Exception {
		return eolus.adminGetVMIP(vm.Name);
	}

	public String getStatus(VMTemplate vm) throws Exception {
		return eolus.adminGetVMStatus(vm.Name);
	}

	public Boolean Kill(VMTemplate vm) throws Exception {
		try{
		eolus.adminShutdownVM(vm.Name, false);
		}catch(Exception x){
			x.printStackTrace();
			eolus.adminShutdownVM(vm.Name, true);
		}
		System.out.println("Kill VM "+vm.Name);
		return true;
		
	}

	public boolean Ready() {
		Plan p = deployer.getCurrent_plan();
		System.out.println("Current plan ");
		p.Print();
		if (p == null) return true;
		Iterator<VMTemplate> vm_it = p.match.keySet().iterator();
		while (vm_it.hasNext()) {
			VMTemplate vm = (VMTemplate) vm_it.next();
			String name = vm.Name;
			System.out.println("Checking status of vm "+name);
			String vm_info = null;
			try {
				vm_info = eolus.adminGetVMStatus(name);
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (vm_info == null){
				System.out.println("No info for vm "+name);
				continue;
			}
			if (!vm_info.equalsIgnoreCase("RUNNING")){
				System.out.println("Infra is not ready");
				return false;
			}
		}
		System.out.println("Infra is ready");
		return true;
	}


}
