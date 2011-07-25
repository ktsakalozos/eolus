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

package org.uoa.nefeli;

import java.util.List;

import org.uoa.nefeli.costfunctions.CostFunction;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * Base class implementation of a planner
 * @author Konstantinos Tsakalozos
 *
 */
public class Planner {

	List<CostFunction> functions;

	/**
	 * Check if all hard constraints are satisfied
	 * @param p the plan
	 * @return true if all constraints are satisfied
	 */
	public boolean CheckConstraints(Plan p){
		for(int i =0 ; i < functions.size();i++){
			if (functions.get(i).weight >= 100){
				if (functions.get(i).Evaluate(p)> 0.0001)
					return false;
			}
		}
		return true;
	}

	/**
	 * Add VMs to the planner
	 * @param listvms The list of VMs to handle
	 */
	public void AddVMs(List<VMTemplate> listvms) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Add cost function/constraint/wish to the planner
	 * @param f The wish to handle
	 */	
	public void AddCostFunction(CostFunction f) {
	}

	/**
	 * Add cost functions/constraints/wishes to the planner
	 * @param cfs The wishes to handle
	 */	
	public void AddCostFunctions(List<CostFunction> cfs) {
	}

	/**
	 * Produce a VM to host mapping
	 * @return The produced plan
	 * @throws Exception
	 */
	public Plan PlanDeployment()  throws Exception {
		return new Plan();
	}

	/**
	 * Reset the planner
	 */
	public void Reset() {
	}
}
