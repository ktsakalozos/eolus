package org.opennebula.bridge;

import java.util.ArrayList;
import java.util.List;

import org.opennebula.bridge.CloudConstants.VMState;

public class VMInfo {
	public String name = "";
	public String id = "";
	public VMState state;
	public List<String> IPs = new ArrayList<String>();
	public String memory;
	public String cpu;
	public String hostname;
	
	public String toXML(){
		String xml = "";
		xml += "<VM>";
		xml += "<ID>"+id+"</ID>";
		xml += "<NAME>"+name+"</NAME>";
		xml += "<IPs>";
		for(String ip : IPs){
			xml += "<IP>"+ip+"</IP>";
		}
		xml += "</IPs>";
		xml += "<MEMORY>"+memory+"</MEMORY>";
		xml += "<CPU>"+cpu+"</CPU>";
		xml += "<HOSTNAME>"+hostname+"</HOSTNAME>";
		xml += "<STATE>"+state.toString()+"</STATE>";
		xml += "</VM>";		
		return xml;
	}
}
