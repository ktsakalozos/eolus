package org.opennebula.bridge;

import org.opennebula.bridge.CloudConstants.HostState;

public class HostInfo {
	public String name = "";
	public String id = "";
	public HostState state;
	public String runvms;
	public String totalcpu;
	public String freecpu;
	public String totalmem;
	public String freemem;
	
	public String toXML(){
		String xml = "";
		xml += "<HOST>";
		xml += "<ID>"+id+"</ID>";
		xml += "<NAME>"+name+"</NAME>";
		xml += "<TOTALCPU>"+totalcpu+"</TOTALCPU>";
		xml += "<FREE_CPU>"+freecpu+"</FREE_CPU>";
		xml += "<TOTALMEMORY>"+totalmem+"</TOTALMEMORY>";
		xml += "<FREE_MEM>"+freemem+"</FREE_MEM>";
		xml += "<RUNNING_VMS>"+runvms+"</RUNNING_VMS>";
		xml += "<STATE>"+state.toString()+"</STATE>";
		xml += "</HOST>";		
		return xml;
	}
	
	public void fromXML(String xml){
		id = xml.substring(xml.indexOf("<ID>")+4,xml.indexOf("</ID>"));
		name = xml.substring(xml.indexOf("<NAME>")+6,xml.indexOf("</NAME>"));
		runvms = xml.substring(xml.indexOf("<RUNNING_VMS>")+13,xml.indexOf("</RUNNING_VMS>"));
		totalcpu = xml.substring(xml.indexOf("<TOTALCPU>")+10,xml.indexOf("</TOTALCPU>"));
		totalmem = xml.substring(xml.indexOf("<TOTALMEMORY>")+13,xml.indexOf("</TOTALMEMORY>"));
		freecpu = xml.substring(xml.indexOf("<FREE_CPU>")+10,xml.indexOf("</FREE_CPU>"));
		freemem = xml.substring(xml.indexOf("<FREE_MEM>")+10,xml.indexOf("</FREE_MEM>"));
		String strstate = xml.substring(xml.indexOf("<STATE>")+7,xml.indexOf("</STATE>"));
		state = HostState.valueOf(strstate);
	}
}
