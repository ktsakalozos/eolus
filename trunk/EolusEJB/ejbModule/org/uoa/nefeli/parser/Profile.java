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
import java.util.List;
import java.util.Map;

/**
 * The profile class to be used in representing the XML user input.
 * @author Konstantinos Tsakalozos
 *
 */
public class Profile {
	private Integer ID;
	private Map<Integer,Double> preferences = new HashMap<Integer,Double>();
	private List<Integer> constrains = new ArrayList<Integer>();
	
	/**
	 * Getter of the profile ID
	 * @return The profile ID
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * Setter of the profile ID
	 * @param id The profile ID
	 */
	public void setID(Integer id) {
		ID = id;
	}
	
	/**
	 * Getter of the mapping between wishes and weights 
	 * @return The mapping between wishes and weights
	 */
	public Map<Integer, Double> getPreferences() {
		return preferences;
	}

	/**
	 * Setter of the mapping between wishes and weights 
	 * @param preferences The mapping between wishes and weights
	 */
	public void setPreferences(Map<Integer, Double> preferences) {
		this.preferences = preferences;
	}

	/**
	 * Getter of the list of hard constraints 
	 * @return The list of hard constraints
	 */
	public List<Integer> getConstrains() {
		return constrains;
	}

	/**
	 * Setter of the list of hard constraints 
	 * @param constrains The list of hard constraints
	 */
	public void setConstrains(List<Integer> constrains) {
		this.constrains = constrains;
	}
}
