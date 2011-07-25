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

import org.uoa.nefeli.Plan;


/**
 * Base class for all cost functions
 * @author Konstantinos Tsakalozos
 *
 */
public class CostFunction {
	
	/**
	 * The weight the cost function has
	 */
	public double weight;
	
	/**
	 * Compute the degree of the cost function satisfaction by the provided Plan
	 * @param p The plan to use in the cost function evaluation 
	 * @return the satisfaction degree. A value in [0,1], the lower the better,
	 * 0 means maximum satisfaction) 
	 */
	public double Evaluate(Plan p)
	{
		weight = 1.0;
		return 1.0;
	}
}
