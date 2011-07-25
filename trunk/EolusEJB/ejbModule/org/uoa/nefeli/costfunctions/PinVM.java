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

package org.uoa.nefeli.costfunctions;

import java.util.Iterator;
import java.util.Map.Entry;

import org.uoa.nefeli.Plan;
import org.uoa.nefeli.Deployer;
import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * Cost function evaluating the satisfaction degree of not migrating a VM
 * @author Konstantinos Tsakalozos
 *
 */
public class PinVM extends CostFunction {

	private Deployer deployer = null;
	private String VMID = null;
	
	/**
	 * @param VMID The VM ID we need not to migrate
	 * @param deployer The deployer used. From this deployer
	 *  can get the current deployment of the VM
	 */
	public PinVM(String VMID, Deployer deployer){
		this.VMID = VMID;
		this.deployer = deployer;
	}
	
	@Override
	public double Evaluate(Plan p) {
		Plan cur = deployer.getCurrent_plan();
		if (cur == null){
			return 0;
		}
		
		String cur_host = null;
		Iterator<Entry<VMTemplate, HostInfo>> it = cur.match.entrySet().iterator();
		while(it.hasNext()){
			Entry<VMTemplate, HostInfo> element = it.next();
			if(element.getKey().Name.equalsIgnoreCase(VMID)){
				cur_host = element.getValue().name;
				break;
			}
		}

		String new_host = null;
		Iterator<Entry<VMTemplate, HostInfo>> nit = cur.match.entrySet().iterator();
		while(nit.hasNext()){
			Entry<VMTemplate, HostInfo> element = nit.next();
			if(element.getKey().Name.equalsIgnoreCase(VMID)){
				new_host = element.getValue().name;
				break;
			}
		}
		
		if (new_host != cur_host) 
			return this.weight;
		else
			return 0;
	}
}
