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
import java.util.Iterator;
import java.util.List;

import org.uoa.nefeli.Plan;
import org.uoa.nefeli.utils.HostInfo;

/**
 * Cost function evaluating the satisfaction degree of keeping a specific number of
 * physical nodes empty
 * @author Konstantinos Tsakalozos
 *
 */
public class PowerSave extends CostFunction  {

	double max_hosts;
	
	/**
	 * @param hosts_num The number of hosts we need to keep empty
	 */
	public PowerSave(int hosts_num)
	{
		max_hosts = hosts_num;
	}
	
	public double Evaluate(Plan p)
	{

		System.out.println("Check plan");
		List<String> hosts = new ArrayList<String>();
		
		Iterator<HostInfo> it = p.match.values().iterator();
	    while (it.hasNext()) {
			HostInfo h = (HostInfo)it.next();
			if (!hosts.contains(new String(h.name))){
				System.out.println("New host "+h.name);				
				hosts.add(new String(h.name));
			}
	    }
 
	    double used = hosts.size();
//	    System.out.println("Power: "+this.weight * used / max_hosts); 
		return this.weight * used / max_hosts;
	}

}
