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

import java.util.ArrayList;
import java.util.List;

/**
 *  Class to represent a composite AND event.
 * @author Konstantinos Tsakalozos
 *
 */
public class AndEvents extends Event {
	
	/**
	 * The list of event IDs placed in an AND expression 
	 */
	private List<Integer> Events = new ArrayList<Integer>();

	/**
	 * Getter of the "ANDed" events 
	 * @return The events that trigger this AND event
	 */
	public List<Integer> getEvents() {
		return Events;
	}

	/**
	 * Setter for "ANDed" events
	 * @param cs The events to be combined in this AND event
	 */
	public void setEvents(List<Integer> cs) {
		Events = cs;
	}

}
