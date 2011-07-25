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
import java.util.Map;
import java.util.Map.Entry;

import org.uoa.nefeli.Plan;
import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * Cost function evaluating the satisfaction degree of minimizing
 * the network communication among a set of VMs over the physical network 
 * @author Konstantinos Tsakalozos
 *
 */
public class MinTrafic extends CostFunction {
	Map<String, List<String> > edges;
	int sum_edges;
	
	/**
	 * 
	 * @param edges Specify the set of network "egdes". A map of VM.ID to List of VM.IDs.
	 */
	public MinTrafic(Map<String, List<String> >  edges)
	{
		this.edges = edges;
		sum_edges = 0;
		Iterator<Entry<String, List<String>>> it = edges.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, List<String>> e = it.next();
			sum_edges+=e.getValue().size();
//			System.out.println("Edge from : "+e.getKey()+" to "+e.getValue().size()+ " nodes." );
		}
	}
	
	public double Evaluate(Plan p)
	{

		int cut_edges = 0;

		HashMap<String,List<String>> vms_per_host = new HashMap<String,List<String>>();

		Iterator<Entry<VMTemplate, HostInfo>> it  = p.match.entrySet().iterator();
		while (it.hasNext()){

			Entry<VMTemplate, HostInfo> e =  it.next();
			HostInfo h = e.getValue();
			VMTemplate vm = e.getKey();

			if (!edges.containsKey(vm.Name))
				continue;

//			System.out.println("Contained vm ID, host ID: "+vm.ID+ " , "+h.ID );
			
			if (!vms_per_host.containsKey(h.name)){
				vms_per_host.put(new String(h.name), new ArrayList<String>());
			}

			List<String> i = vms_per_host.get(h.name);
			i.add(vm.Name);
			vms_per_host.remove(h.name);
			vms_per_host.put(h.name,i);

		}
		
		Iterator<Entry<String, List<String>>> vmit = edges.entrySet().iterator();
		while(vmit.hasNext()){
			Entry<String, List<String>> e = vmit.next();
			String vmid = e.getKey();
			String hostid = null;
			
			Iterator<Entry<VMTemplate, HostInfo>> depit  = p.match.entrySet().iterator();
			while (depit.hasNext()){
				Entry<VMTemplate, HostInfo> en =  depit.next();
				HostInfo h = en.getValue();
				VMTemplate vm = en.getKey();
				if (vm.Name.equalsIgnoreCase(vmid)){
					hostid = new String(h.name);
				}
			}
			
			List<String> checkvms = e.getValue();
			List<String> depvms = vms_per_host.get(hostid);
			
			for(int cvm = 0 ; cvm < checkvms.size(); cvm++){
//				System.out.println("Out of CheckVMS: "+checkvms.get(cvm));
				if (!depvms.contains(checkvms.get(cvm))){
					cut_edges++;					
//					System.out.println("CheckVMS: "+checkvms.get(cvm));
				}
			}
		}
//		System.out.println("Result: "+(double)cut_edges/ (double)sum_edges+ " Cut/All "+ cut_edges +"/"+sum_edges);
		return (this.weight * ((double)cut_edges/ (double)sum_edges));
	}
}
