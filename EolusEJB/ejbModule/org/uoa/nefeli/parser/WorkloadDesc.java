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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.uoa.nefeli.parser.constraints.Constraint;
import org.uoa.nefeli.parser.events.Event;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Workload container class. This class is the top level user input XML element
 * @author Konstantinos Tsakalozos
 *
 */
public class WorkloadDesc {
	private String User;
	private List<Machine> Machines = new ArrayList<Machine>();
	private List<Constraint> Constraints = new ArrayList<Constraint>();
	private List<Profile> Profiles = new ArrayList<Profile>();
	private List<Event> Events = new ArrayList<Event>();
	private List<Transition> Transitions = new ArrayList<Transition>();
	private Integer StartingProfile = new Integer(0);

	/**
	 * Add a VM in the list of the VMs involved in this workload
	 * @param m The VM to be added in this workload
	 */
	public void add(Machine m) {
		Machines.add(m);
	}

	/**
	 * Getter of the VMs involved in this workload
	 * @return The list of VMs in this workload
	 */
	public List<Machine> getMachines() {
		return Machines;
	}

	/**
	 * Setter of the VMs involved in this workload
	 * @param macines The list of VMs in this workload
	 */
	public void setMachines(List<Machine> macines) {
		Machines = macines;
	}

	/**
	 * Setter of the constraints involved in this workload
	 * @param constraints The list of the constraints in this workload
	 */
	public void setConstraints(List<Constraint> constraints) {
		Constraints = constraints;
	}

	/**
	 * Getter of the constraints involved in this workload
	 * @return The list of the constraints in this workload
	 */
	public List<Constraint> getConstraints() {
		return Constraints;
	}

	/**
	 * Setter of the profiles involved in this workload
	 * @param profiles The list of the profiles in this workload
	 */
	public void setProfiles(List<Profile> profiles) {
		Profiles = profiles;
	}

	/**
	 * Getter of the profiles involved in this workload
	 * @return The list of the profiles in this workload
	 */
	public List<Profile> getProfiles() {
		return Profiles;
	}

	/**
	 * Setter of the events involved in this workload
	 * @param events The list of the events in this workload
	 */
	public void setEvents(List<Event> events) {
		Events = events;
	}

	/**
	 * Getter of the events involved in this workload
	 * @return The list of the events in this workload
	 */
	public List<Event> getEvents() {
		return Events;
	}

	/**
	 * Setter of the transitions involved in this workload
	 * @param transitions The list of the transitions in this workload
	 */
	public void setTransitions(List<Transition> transitions) {
		Transitions = transitions;
	}

	/**
	 * Getter of the transitions involved in this workload
	 * @return The list of the transitions in this workload
	 */
	public List<Transition> getTransitions() {
		return Transitions;
	}


	/**
	 * Check the status of the traps and accordingly decide
	 * on the current profile
	 * @param traps The mapping of all traps along with their ID
	 * @return true icase the current profile changes 
	 */
//	public boolean Redeploy(Map<Integer, Trap> traps) {
//		if (Transitions == null)
//			return false;
//		for (int t = 0; t < Transitions.size(); t++) {
//			if (Transitions.get(t).getFrom().equals(CurentProfile)) {
//				List<Integer> events = Transitions.get(t).getEvents();
//				for (int e = 0; e < events.size(); e++) {
//					if (!traps.get(events.get(e)).Check()){
//						return false;
//					}
//				}
//				CurentProfile = Transitions.get(t).getTo();
//				System.out.println("Current profile becomes: "+CurentProfile);
//				return true;
//			}
//		}
//		return false;
//	}

	/**
	 * Getter of the constraints involved in this workload
	 * @return The list of the constraints in this workload
	 */
	public List<Integer> getTrapIDs(Integer CurentProfile) {
		if (Transitions == null)
			return new ArrayList<Integer>();
		for (int t = 0; t < Transitions.size(); t++) {
			if (Transitions.get(t).getFrom().equals(CurentProfile)) {
				return Transitions.get(t).getEvents();
			}
		}
		return null;
	}

	public void setUser(String user) {
		User = user;
	}

	public String getUser() {
		return User;
	}

	public void setStartingProfile(Integer startingProfile) {
		StartingProfile = startingProfile;
	}

	public Integer getStartingProfile() {
		return StartingProfile;
	}

}
