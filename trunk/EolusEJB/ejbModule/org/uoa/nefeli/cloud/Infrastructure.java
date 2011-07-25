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

import java.util.List;

import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * Interface that has to be provided be each infrastructure supported
 * @author Konstantinos Tsakalozos
 *
 */
public interface Infrastructure {

    /**
     * Gather details for each node of the infrastructure.
     * @return The list of physical node information objects 
     * @throws Exception
     */
	public List<HostInfo> GatherDetails() throws Exception;

	/**
	 * Instantiate a new Virtual Machine
	 * @param vm Virtual Machine template describing the VM to be created
	 * @param host The information of the host where the VM will be deployed 
	 * @return The ID of the instantiated VM
	 * @throws Exception
	 */
	public int Spawn(VMTemplate vm, HostInfo host) throws Exception;
	
	/**
	 * Get the IP of the instantiated Virtual Machine described by vm. The VM is
	 * identified by the vm.id.  
	 * @param vm Virtual Machine template describing the VM
	 * @return The IP address
	 * @throws Exception
	 */
	public String getIP(VMTemplate vm) throws Exception;
	
	/**
	 * Get the status of a Virtual Machine. he VM is identified by the vm.id.
	 * @param vm The VM template that is used to identify the VM in question
	 * @return the VMState of the VM 
	 * @throws Exception
	 */
	public String getStatus(VMTemplate vm) throws Exception;

	/**
	 * Have a VM migrate from a old hosting physical node to a new one.
	 * @param vm The VM template that is used to identify the VM in question
	 * @param hostOld The host info identifying the physical host where the VM is currently
	 * deployed 
	 * @param hostNew The host info identifying the physical host where the VM is going to be
	 * deployed
	 * @throws Exception
	 */
	public void Migrate(VMTemplate vm, HostInfo hostOld, HostInfo hostNew) throws Exception;
	
	/**
	 * Stop a virtual machine
	 * @param vm The VM template that is used to identify the VM in question
	 * @throws Exception
	 */
	public Boolean Kill(VMTemplate vm) throws Exception;
	
	/**
	 * Check if there are any pending requests to the infrastructure. That is
	 * any of the running VMs in the STAGING state.
	 * @return true if all VMs are in the running state
	 */
	public boolean Ready();
}
