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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.uoa.nefeli.Plan;
import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * Cost function evaluating the satisfaction degree of evenly distribute
 * VMs over the physical nodes
 * @author Konstantinos Tsakalozos
 *
 */
public class EvenHostLoad extends CostFunction {

	
	public EvenHostLoad(){}
	
	public double Evaluate(Plan p)
	{
		HashMap<HostInfo, Integer> map = new HashMap<HostInfo, Integer>();
		int VMs = 0;
		int hosts = 0;
		
		Iterator<VMTemplate> it = p.match.keySet().iterator();
	    while (it.hasNext()) {
	    	VMs++;
			VMTemplate v = (VMTemplate)it.next();
	    	HostInfo h = p.match.get(v);
	    	if (map.containsKey(h)){
	    		Integer i = map.get(h);
	    		i++;
	    		map.remove(h);
	    		map.put(h, i);
	    	}else{
	    		hosts++;
	    		Integer i = new Integer(1);
	    		map.put(h, i);	    		
	    	}
	    }
		
		double r = (double)(VMs)/(double)(hosts);
		long fill = Math.round(Math.ceil(r));
		
	    double penalty = hosts;
	    double res = 0;
	    
	    Iterator<Entry<HostInfo, Integer>> mit = map.entrySet().iterator();
	    while(mit.hasNext()){
	    	Integer c = mit.next().getValue();
	    	if (c.longValue()-fill > 0){
	    		res+=(c.longValue()-fill)*penalty;
	    	}
	    }
//	    System.out.println("Result: "+res);
	    return this.weight * res;
	}

}
