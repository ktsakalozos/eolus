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

import java.util.Observable;

/**
 * Base class of all traps. Each trap corresponds to a specific event
 * supported by Nefeli. All traps are observable.
 * @author Konstantinos Tsakalozos
 *
 */
public class Trap{

	public int ID;
	
	/**
	 * Check if this trap has been triggered
	 * @return true if this trap is triggered
	 */
	public boolean Check()
	{
		return true;
	}
	
}
