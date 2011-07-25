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
 * Class used to represent the EmptyHost constraint/wish.
 * @author Konstantinos Tsakalozos
 *
 */
public class EmptyHostNodeWish extends Constraint {
	
	private String Host = null;
	
	/**
	 * Getter of the host ID we wish to off load
	 * @return The host ID
	 */
	public String getHost() {
		return Host;
	}

	/**
	 * Setter of the host ID we wish to off load 
	 * @param i The host ID
	 */
	public void setHost(String i) {
		Host = i;
	}
}
