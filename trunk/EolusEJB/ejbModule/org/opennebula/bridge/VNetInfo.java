package org.opennebula.bridge;

import java.util.ArrayList;
import java.util.List;

import org.opennebula.bridge.CloudConstants.VNetState;

public class VNetInfo {
	public String name = "";
	public String id = "";
	public VNetState state;
	public List<String> IPs = new ArrayList<String>();
	
	public String toXML(){
		String xml = "";
		xml += "<VNet>";
		xml += "<ID>"+id+"</ID>";
		xml += "<NAME>"+name+"</NAME>";
		xml += "<IPs>";
		for(String ip : IPs){
			xml += "<IP>"+ip+"</IP>";
		}
		xml += "</IPs>";
		xml += "<STATE>"+state.toString()+"</STATE>";
		xml += "</VNet>";		
		return xml;
	}
}
