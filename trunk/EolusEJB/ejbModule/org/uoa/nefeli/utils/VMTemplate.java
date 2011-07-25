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

public class VMTemplate {

	public String Name;
	public int HD = 0;
	public int Mem;
	public int CPU;
	public String VMtemplateName;
	public String[] networks;
	public String User;
	
	public VMTemplate(VMTemplate t)
	{
		User = t.User;
		Name = t.Name;
		Mem = t.Mem;
		CPU = t.CPU;
		VMtemplateName = t.VMtemplateName;
		networks = t.networks;
	}
	
	public VMTemplate(String user, String name, int cpu, int mem, String VMtemplateName, String[] nets)
	{
		User = user;
		Name = name;
		CPU = cpu;
		Mem = mem;
		this.VMtemplateName = VMtemplateName;
		this.networks = nets;
		
	}

}
