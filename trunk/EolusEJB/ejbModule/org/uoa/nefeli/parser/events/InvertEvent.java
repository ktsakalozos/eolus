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

package org.uoa.nefeli.parser.events;

/**
 *  Class to represent a composite NOT event.
 * @author Konstantinos Tsakalozos
 *
 */
public class InvertEvent extends Event {
	
	private Integer EventToInvert = null;
	

	/**
	 * Getter of the "NOTed" event 
	 * @return The event that triggers this NOT event
	 */
	public Integer getEventToInvert() {
		return EventToInvert;
	}

	/**
	 * Setter for "NOTed" event
	 * @param eventToInvert The event to be inverted by this NOT event
	 */
	public void setEventToInvert(Integer eventToInvert) {
		EventToInvert = eventToInvert;
	}

}
