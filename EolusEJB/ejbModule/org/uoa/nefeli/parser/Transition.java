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

package org.uoa.nefeli.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used in representing a transition from one profile
 * to another in the XML user input
 * @author Konstantinos Tsakalozos
 *
 */
public class Transition {

	private Integer from;
	private Integer to;
	private List<Integer> events = new ArrayList<Integer>();

	/**
	 * Setter of the events that will trigger the profile transition
	 * @param events The list of events
	 */
	public void setEvents(List<Integer> events) {
		this.events = events;
	}

	/**
	 * Getter of the events that will trigger the profile transition
	 * @return The list of events
	 */
	public List<Integer> getEvents() {
		return events;
	}
	
	/**
	 * Setter of the target profile of the transition  
	 * @param to The target profile ID of the transition 
	 */
	public void setTo(Integer to) {
		this.to = to;
	}

	/**
	 * Getter of the target profile of the transition  
	 * @return The target profile ID of the transition 
	 */
	public Integer getTo() {
		return to;
	}

	/**
	 * Setter of the source profile of the transition  
	 * @param from The source profile ID of the transition 
	 */
	public void setFrom(Integer from) {
		this.from = from;
	}

	/**
	 * Getter of the source profile of the transition  
	 * @return The source profile ID of the transition 
	 */
	public Integer getFrom() {
		return from;
	}
}
