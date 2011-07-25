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
 *  Class to represent a magic word triggered event. The magic word is send
 *  to a port this event listens on
 * @author Konstantinos Tsakalozos
 *
 */
public class MagicWordEvent extends Event {

	private String keyWord = null; 
	private Integer port = null; 
	
	/**
	 * Getter for the magic word
	 * @return The magic word
	 */
	public String getKeyWord() {
		return keyWord;
	}

	/**
	 * Setter for the magic word
	 * @param keyWord The magic word
	 */
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	/**
	 * Getter for the port the event mechanism will listen at.
	 * @return The port on which the magic word will be sent
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * Setter for the port the event mechanism will listen at.
	 * @param port The port on which the magic word will be sent
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	
}
