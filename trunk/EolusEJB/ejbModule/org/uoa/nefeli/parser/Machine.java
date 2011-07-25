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

/**
 * Class representing a Machine in the XML user input representation
 * 
 * @author Konstantinos Tsakalozos
 *
 */
public class Machine {
	private Integer RAM;
	private Integer Cores;
	private String Name;
	private String Template;
	private String[] Network;
	
	/**
	 * Getter for the VM RAM
	 * @return The VM RAM
	 */
	public Integer getRAM() {
		return RAM;
	}
	
	/**
	 * Setter for the VM RAM
	 * @param ram The VM ram
	 */
	public void setRAM(Integer ram) {
		RAM = ram;
	}
	
	/**
	 * Getter for the VM HD requirement
	 * @return The VM HD requirement
	 */
	public Integer getCores() {
		return Cores;
	}

	/**
	 * Setter for the VM HD requirement
	 * @param hd The VM HD requirement
	 */
	public void setCores(Integer cores) {
		Cores = cores;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getName() {
		return Name;
	}

	public void setNetwork(String[] network) {
		Network = network;
	}

	public String[] getNetwork() {
		return Network;
	}

	public void setTemplate(String template) {
		Template = template;
	}

	public String getTemplate() {
		return Template;
	}
}
