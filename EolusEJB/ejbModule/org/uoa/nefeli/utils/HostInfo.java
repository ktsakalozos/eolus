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

package org.uoa.nefeli.utils;

/**
 * Host information class. All infrastructures should fill out this
 * class.
 * @author Konstantinos Tsakalozos
 *
 */
public class HostInfo {

	public int Mem_free;
	public int Mem_total;
	public double CPU_free;
	public double CPU_total;
	public String name;
	public String site;
	
	
	public HostInfo(){
		Mem_free = 0;
		Mem_total = 0;
		CPU_free = 0;
		CPU_total = 0;
		name ="NONE";
		site = "NONE";
	}

	public HostInfo(HostInfo h){
		this.Mem_free = h.Mem_free;
		this.Mem_total = h.Mem_total;
		this.CPU_free = h.CPU_free;
		this.CPU_total = h.CPU_total;
		this.name = h.name;
		this.site = h.site;
	}
}
