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

import java.util.Iterator;

import org.uoa.nefeli.Signal;
import org.uoa.nefeli.TaskflowEntry;

/**
 * @author Konstantinos Tsakalozos
 *
 */
public class MagicWordTrap extends Trap{

	int port = -1;
	String keyWord = null;
	boolean result = false;
	TaskflowEntry task = null;
	
	/**
	 * 
	 * @param port The port the magic word will be send 
	 * @param keyWord The magic word 
	 */
	public MagicWordTrap(int port, String keyWord, TaskflowEntry task) {
		this.task = task;
		this.port = port;
		this.keyWord = keyWord;
	}

	public boolean Check() {
		System.out.println("Checking magic word.");
		Iterator<Signal> it = task.getKeyMap().iterator();
		while (it.hasNext()) {
			Signal signal = (Signal) it.next();
			if ((signal.getReciever() == port) && (signal.getWord().equals(keyWord))){
				System.out.println("Keyword "+ keyWord +" trigeres trap");
				return true;
			}
		}
		return false;
	}
}
