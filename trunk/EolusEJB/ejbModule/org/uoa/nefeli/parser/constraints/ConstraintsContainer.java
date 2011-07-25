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
 * Constraint XML container of all constraints
 * @author jackal
 *
 */
public class ConstraintsContainer {
	private List<Constraint> Wishes = new ArrayList<Constraint>();

	/**
	 * Setter for constraints set
	 * @param wishes List of constraints/wishes
	 */
	public void setWishes(List<Constraint> wishes) {
		Wishes = wishes;
	}

	/**
	 * Getter of constraint set
	 * @return The constraints/wishes
	 */
	public List<Constraint> getWishes() {
		return Wishes;
	}

}
