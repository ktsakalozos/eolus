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
import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * Cost function evaluating the satisfaction degree of keeping a specific
 * VM deployed being the sole tenant of a physical node
 * @author Konstantinos Tsakalozos
 *
 */
public class FavourVM extends CostFunction  {

	String nodeID = null;
	
	/**
	 * @param nodeID The VM ID we want to favor over the others
	 */
	public FavourVM(String nodeID)
	{
		this.nodeID = nodeID;
	}
	
	public double Evaluate(Plan p)
	{
		System.out.println("Evaluate plan");
		double maxCPU = 0;
		double maxMem = 0;
		double res = 0;
		
		
		Iterator<Entry<VMTemplate, HostInfo>> it  = p.match.entrySet().iterator();
		System.out.println("This is equal to the VMs number: "+p.match.size());
		while (it.hasNext()){
			Entry<VMTemplate, HostInfo> e =  it.next();
			HostInfo h = e.getValue();
			VMTemplate vm = e.getKey();
			
			if (vm.Name.equalsIgnoreCase(nodeID))
				continue;
			
			maxCPU = h.CPU_total -vm.CPU;
			maxMem = h.Mem_total -vm.Mem;
			res = 1.0 - ((double)h.Mem_free / maxMem * 0.5 +(double)h.CPU_free / maxCPU * 0.5); 
			System.out.println("HOST ID: "+ h.name +" Free mem: " + maxMem + " h.Mem_total =  " +h.Mem_total+ " vm.Mem " +vm.Mem  +" Plan evaluation: "+res);
			System.out.println("Free CPU: " + maxCPU + " h.CPU_total " + h.CPU_total + " vm.CPU " + vm.CPU );
			System.out.println("Free CPU: " + h.CPU_free + " Free mem: " + h.Mem_free );
			return weight * res;
		}
		return 1.0;
	}

}
