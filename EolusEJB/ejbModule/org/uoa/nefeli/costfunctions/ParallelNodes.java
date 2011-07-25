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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.uoa.nefeli.Plan;
import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * Cost function evaluating the satisfaction degree of deploying a set of VMs to different
 * physical nodes
 * @author Konstantinos Tsakalozos
 *
 */
public class ParallelNodes extends CostFunction {
	List<VMTemplate> vms;
	int hostnum;
	int best;
	double mean;
	double max_dev;
	
	/**
	 * @param nodes The set of nodes to deploy on different physical nodes
	 */
	public ParallelNodes(List<VMTemplate> nodes) {
		hostnum = -1;
		vms = nodes;
	}
	
	public double Evaluate(Plan p)
	{
		if (hostnum < 0){
			hostnum = 0;
			Iterator<HostInfo> it  = p.match.values().iterator();
			List<String> host_ids = new ArrayList<String>();
			while(it.hasNext()){
				if (!host_ids.contains(new String(it.next().name))){
					hostnum++;
				}
			}
			mean = (double)vms.size() / (double)hostnum;
			max_dev = Math.abs(mean - vms.size());

		}
//		System.out.println("Hosts: "+ hostnum +" Mean: "+mean);	
		HashMap<String,Integer> vms_per_host = new HashMap<String,Integer>();
		Iterator<Entry<VMTemplate, HostInfo>> it  = p.match.entrySet().iterator();
		while (it.hasNext()){
			Entry<VMTemplate, HostInfo> e =  it.next();
			HostInfo h = e.getValue();
			VMTemplate vm = e.getKey();
			
			if (!ParallelVM(vm))
				continue;
			
			if (!vms_per_host.containsKey(h.name)){
				vms_per_host.put(h.name, new Integer(0));
			}
			
			Integer i = vms_per_host.get(h.name);
			i++;
			vms_per_host.remove(h.name);
			vms_per_host.put(h.name,i);
			
		}

//		PrintVMsPerHost(vms_per_host);
		
		double stddev = 0.0;
		Iterator<Entry<String, Integer>> hit  = vms_per_host.entrySet().iterator();
		while (hit.hasNext()){
			Entry<String, Integer> a = hit.next();

			stddev += Math.pow((Double.parseDouble(""+a.getValue()) - mean),2);
		}
		stddev /= (double)vms_per_host.size();
		stddev = Math.sqrt(stddev);
		
		double dist = Math.abs(stddev) / max_dev;
//		System.out.println("Result: " + dist);
		if (dist > 1.0) 
			return this.weight * 1.0; 
		else
			return this.weight * dist;
	}

	private void PrintVMsPerHost(HashMap<String, Integer> vms_per_host) {
		Iterator<Entry<String, Integer>> hit  = vms_per_host.entrySet().iterator();
		while (hit.hasNext()){
			Entry<String, Integer> a = hit.next();
			System.out.println("On host "+a.getKey()+" we have "+a.getValue()+" VMs");
		}
		
	}

	private boolean ParallelVM(VMTemplate vm) {
		Iterator<VMTemplate> it = vms.iterator();
		while (it.hasNext()){
			VMTemplate t = it.next();
			if (t.Name.equalsIgnoreCase(vm.Name))
				return true;
		}
		return false;
	}

}
