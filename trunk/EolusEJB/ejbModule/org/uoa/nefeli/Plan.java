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

package org.uoa.nefeli;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The deployment plan includes the VM to Host mapping and a ranking  
 * @author Konstantinos Tsakalozos
 *
 */
public class Plan implements Comparable<Plan> {
	public double rank = 0;
	
	public Map<VMTemplate,HostInfo> match;

	public Plan(Plan p)
	{
		rank = p.rank;
		match = new HashMap<VMTemplate, HostInfo>();
		match.putAll(p.match);
	}

	
	public Plan()
	{
		match = new HashMap<VMTemplate, HostInfo>();
	}
	
	/**
	 * Clone a plan
	 * @param p The plan to be cloned
	 */
	public void ClonePlan(Plan p)
	{
		rank = p.rank;
		match = new HashMap<VMTemplate, HostInfo>();
		match.putAll(p.match);
		Ready();
	}

	/**
	 * make the assignment of VM to hosting node
	 * @param vm The vm
	 * @param host The host
	 */
	public void Deploy(VMTemplate vm, HostInfo host)
	{
		match.put(vm, host);
	}

	public int compareTo(Plan p) {
		if (rank > p.rank) return 1;
		else if (rank == p.rank) return 0;
		else return -1;
	}


	/**
	 * Clean the plan
	 */
	public void Clean() {
		rank = 0;
		match.clear();
	}

	/**
	 * Used to deep copy a map
	 */
	public void Ready() {
		Map<VMTemplate,HostInfo> m = new HashMap<VMTemplate, HostInfo>();
		Iterator<Entry<VMTemplate, HostInfo>> it = match.entrySet().iterator();
		while(it.hasNext()){
			Entry<VMTemplate, HostInfo> e = it.next();
			m.put(e.getKey(), new HostInfo(e.getValue()));
		}		
		match = m;
	}
	
	/**
	 * Print the plan
	 */
	public void Print(){
		Iterator<Entry<VMTemplate, HostInfo>> it = match.entrySet().iterator();
		while(it.hasNext()){
			Entry<VMTemplate, HostInfo> e = it.next();
			System.out.println("VM name: " + e.getKey().Name +" of user "+e.getKey().User+ " goes on host: "+e.getValue().name);
	//		System.out.println("VM user: " + e.getKey().User + " HOST: "+e.getValue().name);
	//		System.out.println("VM templatename: " + e.getKey().VMtemplateName + " HOST: "+e.getValue().name);
		}		
	}
	
	public String toXML(){
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(this);
	}
	
	public static Plan fromXML(String xml){
		XStream xstream = new XStream(new DomDriver());
		return (Plan)xstream.fromXML(xml);
	}
	
	public static void main(String[] args){
		Plan p = new Plan();
		VMTemplate vm1 = new VMTemplate("foo","bar",2,256,"tmpl1",null);
		VMTemplate vm2 = new VMTemplate("foo2","bar2",1,512,"tmpl2",null);
		HostInfo h1 = new HostInfo();
		h1.CPU_free = 1;
		h1.CPU_total =100 ;
		h1.Mem_free = 256;
		h1.Mem_total = 1024;
		p.match.put(vm1,h1);
		p.match.put(vm2,h1);
		p.Print();
		XStream xstream = new XStream(new DomDriver());
		String xml = xstream.toXML(p);
		System.out.println(xml);
		Plan p2 = (Plan)xstream.fromXML(xml);
		p2.Print();
	}
	
}
