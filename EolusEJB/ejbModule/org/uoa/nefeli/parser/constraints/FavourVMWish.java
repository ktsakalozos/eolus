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
 * Class used to represent the favor a VM constraint/wish.
 * @author Konstantinos Tsakalozos
 *
 */
public class FavourVMWish extends Constraint {
	private String PC = new String();

	/**
	 * Getter of the VM ID we wish to favor
	 * @return The VM ID
	 */
	public String getPC() {
		return PC;
	}

	/**
	 * Setter of the VM ID we wish to favor
	 * @param i The VM ID
	 */
	public void setPC(String i) {
		PC = i;
	}

}
