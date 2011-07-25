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

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to represent the parallel deployment of VMs constraint/wish.
 * @author Konstantinos Tsakalozos
 *
 */
public class ParallelPCsWish extends Constraint {
	private List<String> PCs = new ArrayList<String>();

	/**
	 * Getter of VMs to be deployed on different hosting nodes
	 * @return The list of VM IDs
	 */
	public List<String> getPCs() {
		return PCs;
	}

	/**
	 * Setter of VMs to be deployed on different hosting nodes
	 * @param cs The list of VM IDs
	 */
	public void setPCs(List<String> cs) {
		PCs = cs;
	}
}
