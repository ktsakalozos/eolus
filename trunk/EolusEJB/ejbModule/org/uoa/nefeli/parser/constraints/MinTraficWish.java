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

package org.uoa.nefeli.parser.constraints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to represent the minimize traffic over the physical
 *  network constraint/wish.
 * @author Konstantinos Tsakalozos
 *
 */
public class MinTraficWish extends Constraint {
	private Map<String, List<String>> Edges = new HashMap<String, List<String>>();

	/**
	 * Getter of the edges over which we wish to minimize network traffic
	 * @return The mapping of the VM ID to VMs IDs connections
	 */
	public Map<String, List<String>> getEdges() {
		return Edges;
	}

	/**
	 * Setter of the edges over which we wish to minimize network traffic
	 * @param cs The mapping of the VM ID to VMs IDs connections
	 */
	public void setEdges(Map<String, List<String>> cs) {
		Edges = cs;
	}

}
