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

/**
 * Base class implementation  for all constraints XML representations.
 * @author Konstantinos Tsakalozos
 *
 */
public class Constraint {
	
	/**
	 * The constraint ID
	 */
	private Integer ID;
	
	/**
	 * Constraint ID getter
	 * @return The constraint ID
	 */
	public Integer getID() {
		return ID;
	}
	
	/**
	 * Constraint setter
	 * @param id The constraint ID
	 */
	public void setID(Integer id) {
		ID = id;
	}
}
