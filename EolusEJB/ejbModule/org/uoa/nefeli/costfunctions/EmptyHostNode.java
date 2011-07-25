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

import org.uoa.nefeli.Plan;
import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * Cost function evaluating the satisfaction degree of keeping a specific
 * physical node empty
 * @author Konstantinos Tsakalozos
 *
 */
public class EmptyHostNode extends CostFunction  {

	String nodeID = null;
	
	/**
	 * @param nodeID The physical node ID we want to keep empty 
	 */
	public EmptyHostNode(String nodeID)
	{
		this.nodeID = nodeID;
	}
	
	public double Evaluate(Plan p)
	{
		double maxCPU = 0;
		double nodeCPU = 0;
		double maxMem = 0;
		double nodeMem = 0;
		double res = 0;
		
		Iterator<VMTemplate> it = p.match.keySet().iterator();
	    while (it.hasNext()) {
			VMTemplate v = (VMTemplate)it.next();
			maxCPU += v.CPU;
			maxMem += v.Mem;
	    	HostInfo h = p.match.get(v);
			if (h.name.equalsIgnoreCase(nodeID)){
				nodeCPU += v.CPU;
				nodeMem += v.Mem;				
			}
	    }

		res = (nodeMem / maxMem * 0.5 +nodeCPU / maxCPU * 0.5); 
		
		return this.weight * res;
	}

}
