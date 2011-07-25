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

package org.uoa.nefeli.traps;

import java.util.List;

/**
 * Event triggered when one of the traps of a provided set
 * has been triggered
 * @author Konstantinos Tsakalozos
 *
 */
public class OrTraps extends Trap {

	List<Trap> traps = null;
	
	/**
	 * 
	 * @param traps The list of traps to be "ORed"
	 */
	public OrTraps(List<Trap> traps) {
		this.traps = traps;
	}
	
	public boolean Check(){
		for(int i = 0 ; i < traps.size(); i++)
			if (traps.get(i).Check() == true)
				return true;
		
		return false;
	}
}
