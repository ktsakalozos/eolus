package org.opennebula.bridge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.opennebula.bridge.CloudConstants.HostAction;
import org.opennebula.bridge.CloudConstants.VMAction;
import org.opennebula.bridge.CloudConstants.VNetAction;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import org.opennebula.client.vnet.VirtualNetwork;
import org.opennebula.client.vnet.VirtualNetworkPool;
import org.uoa.eolus.InternalErrorException;

public class CloudClient {
	public Client cloudClinet;

	public CloudClient() throws Exception {
		cloudClinet = new Client();
	}


	//////////////////////////////////////////
	// VMs /////////////////////////////////
	//////////////////////////////////////////
	public List<VMInfo> getAllVMs() throws InternalErrorException{
		List<VMInfo> ans = new ArrayList<VMInfo>();

		OneResponse res = VirtualMachinePool.info(cloudClinet,-2);
		if (res.isError()){
			throw new InternalErrorException("Error getting VM info. "+res.getErrorMessage());
		}else{
			String xml = res.getMessage();
			int nameend = 0;
			int namestart = 0;

			int idend = 0;
			int idstart = 0;

			while(nameend >= namestart){
				VMInfo vm = new VMInfo();

				idstart = xml.indexOf("<ID>",idend);
				if (idstart <= idend)
					break;
				idstart+=4;
				idend = xml.indexOf("</ID>",idstart);
				String id = xml.substring(idstart, idend);
				if (!id.startsWith("<![CDATA")){
					vm.id = id; 
					System.out.println("ID ->:"+ vm.id);
				}

				namestart = xml.indexOf("<NAME>",idend);
				namestart+=6;
				if (namestart <= nameend)
					break;
				nameend = xml.indexOf("</NAME>",namestart);
				String name = xml.substring(namestart, nameend);
				if (!name.startsWith("<![CDATA")){
					vm.name = name; 
					System.out.println("NAME ->:"+ vm.name);
				}
				ans.add(vm);
			}
		}
		return ans;
	}

	public VMInfo vmAllocate(File desc) throws IOException, InternalErrorException {
		OneResponse res = VirtualMachine.allocate(cloudClinet, FileUtils.readFileToString(desc));
		if (res.isError()){
			throw new InternalErrorException("Error allocating a VM. "+res.getErrorMessage());
		}else{
			VMInfo info = new VMInfo();
			info.id = res.getMessage();
			return info;
		}

	}

	public boolean vmAction(VMAction action, int vmid) throws InternalErrorException {
		VirtualMachine vm = new VirtualMachine(vmid,cloudClinet);
		if (action == VMAction.poweroff){
			OneResponse res = vm.finalizeVM();
			if (res.isError()){
				throw new InternalErrorException("Poweroff failed. "+res.getErrorMessage());
			}
		}
		if (action == VMAction.restart){
			OneResponse res = vm.restart();
			if (res.isError()){
				throw new InternalErrorException("Restart failed. "+res.getErrorMessage());
			}
		}
		if (action == VMAction.resume){
			OneResponse res = vm.resume();
			if (res.isError()){
				throw new InternalErrorException("Resume failed. "+res.getErrorMessage());
			}
		}
		if (action == VMAction.shutdown){
			OneResponse res = vm.shutdown();
			if (res.isError()){
				throw new InternalErrorException("Shutdown failed. "+res.getErrorMessage());
			}
		}
		if (action == VMAction.suspend){
			OneResponse res = vm.suspend();
			if (res.isError()){
				throw new InternalErrorException("Suspend failed. "+res.getErrorMessage());
			}
		}
		return true;
	}

	public void VMMigrate(int vmid, int hostID, boolean livemigration) throws InternalErrorException {
		VirtualMachine vmo = new VirtualMachine(vmid,cloudClinet);

		if (livemigration){
			OneResponse res = vmo.liveMigrate(hostID);
			if (res.isError()){
				throw new InternalErrorException("Error livemigrating VM. "+res.getErrorMessage());
			}
		}else{
			OneResponse res = vmo.migrate(hostID);
			if (res.isError()){
				throw new InternalErrorException("Error migrating VM info. "+res.getErrorMessage());
			}
		}

	}

	private String extractString(String xml, int start, int end) {
		String str = xml.substring(start, end);
		if (str.startsWith("<![CDATA[")){
			str = str.substring(9, str.length()-3);
		}
		//		System.out.println("Extracted string: " + str);
		return str;
	}

	public VMInfo getVMInfo(int vmid) throws InternalErrorException {
		VirtualMachine vmo = new VirtualMachine(vmid,cloudClinet);

		OneResponse res = vmo.info();
		if (res.isError()){
			throw new InternalErrorException("Error getting VM info. "+res.getErrorMessage());
		}else{
			String xml = res.getMessage();
			System.out.println(xml);
			int nameend = 0;
			int namestart = 0;

			int hnameend = 0;
			int hnamestart = 0;

			int idend = 0;
			int idstart = 0;

			int memend = 0;
			int memstart = 0;

			int cpuend = 0;
			int cpustart = 0;

			int ipstart = 0;
			int ipend = 0;

			VMInfo vm = new VMInfo();

			idstart = xml.indexOf("<ID>",idend);
			idstart+=4;
			idend = xml.indexOf("</ID>",idstart);
			String id = extractString(xml, idstart, idend);
			vm.id = id; 

			namestart = xml.indexOf("<NAME>",nameend);
			namestart+=6;
			nameend = xml.indexOf("</NAME>",nameend);
			vm.name = extractString(xml, namestart, nameend);

			hnamestart = xml.indexOf("<HOSTNAME>",hnameend);
			hnamestart+=10;
			hnameend = xml.indexOf("</HOSTNAME>",hnameend);
			vm.hostname = extractString(xml, hnamestart, hnameend);

			while (ipstart <= ipend){
				ipstart = xml.indexOf("<IP>",ipend);
				ipstart+=4;
				if (ipstart < ipend)
					break;
				ipend = xml.indexOf("</IP>",ipstart);
				vm.IPs.add(extractString(xml, ipstart, ipend));
			}

			memstart = xml.indexOf("<MEMORY>",nameend);
			memstart+=8;
			memend = xml.indexOf("</MEMORY>",memend);
			vm.memory = extractString(xml, memstart, memend);

			cpustart = xml.indexOf("<CPU>",cpuend);
			cpustart+=5;
			cpuend = xml.indexOf("</CPU>",cpuend);
			vm.cpu = extractString(xml, cpustart, cpuend);

			String vmstate = vmo.stateStr(); 
			//			String lcmstate = vmo.lcmStateStr();
			if (vmstate.equalsIgnoreCase("INIT") || vmstate.equalsIgnoreCase("PENDING")) {
				vm.state = CloudConstants.VMState.STAGING;
			}
			if (vmstate.equalsIgnoreCase("HOLD") || vmstate.equalsIgnoreCase("STOPPED") || vmstate.equalsIgnoreCase("SUSPENDED")){
				vm.state = CloudConstants.VMState.SUSPENDED;
			}
			if (vmstate.equalsIgnoreCase("DONE")){
				vm.state = CloudConstants.VMState.STOPPED;
			}
			if (vmstate.equalsIgnoreCase("ACTIVE")){
				vm.state = CloudConstants.VMState.RUNNING;
			}
			if (vmstate.equalsIgnoreCase("FAILED")){
				vm.state = CloudConstants.VMState.FAILED;
			}

			return vm;
		}
	}

	//////////////////////////////////////////
	// HOSTS /////////////////////////////////
	//////////////////////////////////////////	
	public HostInfo hostAllocate(String hostname, String im, String vmm, String tm) throws IOException, InternalErrorException {	
		OneResponse res = Host.allocate(cloudClinet, hostname, im, vmm, tm);
		if (res.isError()){
			throw new InternalErrorException("Error allocating Host. "+res.getErrorMessage());
		}
		HostInfo hostinfo = new HostInfo();
		hostinfo.id = res.getMessage();
		return hostinfo;
	}

	public boolean hostAction(HostAction action, int hostid) throws InternalErrorException {
		Host host = new Host(hostid,cloudClinet);
		if (action == HostAction.delete){
			OneResponse res = host.delete();
			if (res.isError()){
				throw new InternalErrorException("Deletion failed. "+res.getErrorMessage());
			}
		}
		if (action == HostAction.disable){
			OneResponse res = host.disable();
			if (res.isError()){
				throw new InternalErrorException("Host disabling failed. "+res.getErrorMessage());
			}
		}
		if (action == HostAction.enable){
			OneResponse res = host.enable();
			if (res.isError()){
				throw new InternalErrorException("Host enabling failed. "+res.getErrorMessage());
			}
		}
		return true;

	}

	public List<HostInfo> getAllHosts() throws InternalErrorException {
		List<HostInfo> ans = new ArrayList<HostInfo>();
		OneResponse res = HostPool.info(cloudClinet);
		if (res.isError()){
			throw new InternalErrorException("Error getting Host pool info. "+res.getErrorMessage());
		}else{
			String xml = res.getMessage();
			System.out.println("Host pool info: "+xml);
			int nameend = 0;
			int namestart = 0;

			int idend = 0;
			int idstart = 0;

			while(nameend >= namestart){
				HostInfo h = new HostInfo();

				idstart = xml.indexOf("<ID>",idend);
				idstart+=4;
				idend = xml.indexOf("</ID>",idstart);
				String id = xml.substring(idstart, idend);
				if (!id.startsWith("<![CDATA")){
					h.id = id; 
					System.out.println("ID ->:"+ h.id);
				}

				namestart = xml.indexOf("<NAME>",idend);
				namestart+=6;
				if (namestart <= nameend)
					break;
				nameend = xml.indexOf("</NAME>",namestart);
				String name = xml.substring(namestart, nameend);
				if (!name.startsWith("<![CDATA")){
					h.name = name; 
					System.out.println("NAME ->:"+ h.name);
				}
				ans.add(h);
			}
			return ans;
		}

	}

	public int getHostIDfromHostName(String hostname) throws InternalErrorException {
		List<HostInfo> hosts = getAllHosts();
		for (int i =0 ; i< hosts.size(); i++){
			if (hosts.get(i).name.equals(hostname))
				return Integer.parseInt(hosts.get(i).id);
		}
		throw new InternalErrorException("Host "+hostname+" not found.");
	}


	public HostInfo getHostInfo(int hostid) throws InternalErrorException {
		Host hosto = new Host(hostid,cloudClinet);

		OneResponse res = hosto.info();
		if (res.isError()){
			throw new InternalErrorException("Error getting Host info. "+res.getErrorMessage());
		}else{
			String xml = res.getMessage();
			System.out.println("Host info: "+xml);
			int nameend = 0;
			int namestart = 0;

			int idend = 0;
			int idstart = 0;

			//			int ipstart = 0;
			//			int ipend = 0;

			int totcpustart = 0;
			int totcpuend = 0;

			int totmemstart = 0;
			int totmemend = 0;

			int runvmsstart = 0;
			int runvmsend = 0;

			int freecpustart = 0;
			int freecpuend = 0;

			int freememstart = 0;
			int freememend = 0;

			HostInfo h = new HostInfo();

			idstart = xml.indexOf("<ID>",idend);
			idstart+=4;
			idend = xml.indexOf("</ID>",idstart);
			String id = extractString(xml, idstart, idend);
			h.id = id; 

			namestart = xml.indexOf("<HOSTNAME>",nameend);
			if (namestart < 0) {
				System.out.println("No hostname found!");
				namestart = xml.indexOf("<NAME>",nameend);
				namestart+=6;
				nameend = xml.indexOf("</NAME>",nameend);
				h.name = extractString(xml, namestart, nameend);
				h.runvms = "0";
				h.totalcpu = "0";
				h.totalmem = "0";
				h.freecpu = "0";
				h.freemem = "0";
			}else{
				namestart+=10;
				nameend = xml.indexOf("</HOSTNAME>",nameend);
				h.name = extractString(xml, namestart, nameend);

				runvmsstart = xml.indexOf("<RUNNING_VMS>",runvmsend);
				runvmsstart+=13;
				runvmsend = xml.indexOf("</RUNNING_VMS>",runvmsend);
				h.runvms = extractString(xml, runvmsstart, runvmsend);

				totcpustart = xml.indexOf("<TOTALCPU>",totcpuend);
				totcpustart+=10;
				totcpuend = xml.indexOf("</TOTALCPU>",totcpuend);
				h.totalcpu = extractString(xml, totcpustart, totcpuend);

				freecpustart = xml.indexOf("<FREE_CPU>",freecpuend);
				freecpustart+=10;
				freecpuend = xml.indexOf("</FREE_CPU>",freecpuend);
				h.freecpu = extractString(xml, freecpustart, freecpuend);

				totmemstart = xml.indexOf("<TOTALMEMORY>",totmemend);
				totmemstart+=13;
				totmemend = xml.indexOf("</TOTALMEMORY>",totmemend);
				h.totalmem = extractString(xml, totmemstart, totmemend);

				freememstart = xml.indexOf("<FREEMEMORY>",freememend);
				freememstart+=12;
				freememend = xml.indexOf("</FREEMEMORY>",freememend);
				h.freemem = extractString(xml, freememstart, freememend);
			}


			String hstate = hosto.stateStr(); 
			//			"INIT", "MONITORING", "MONITORED", "ERROR", "DISABLED"
			//			String lcmstate = vmo.lcmStateStr();
			if (hstate.equalsIgnoreCase("INIT") || hstate.equalsIgnoreCase("MONITORING")) {
				h.state = CloudConstants.HostState.STAGING;
			}
			if (hstate.equalsIgnoreCase("MONITORED")) {
				h.state = CloudConstants.HostState.MONITORED;
			}
			if (hstate.equalsIgnoreCase("DISABLED")) {
				h.state = CloudConstants.HostState.DISABLED;
			}
			if (hstate.equalsIgnoreCase("ERROR")) {
				h.state = CloudConstants.HostState.FAILED;
			}

			return h;
		}
	}


	//////////////////////////////////////////
	// VNETS /////////////////////////////////
	//////////////////////////////////////////
	public List<VNetInfo> getAllVNets() throws InternalErrorException {
		List<VNetInfo> ans = new ArrayList<VNetInfo>();

		OneResponse res = VirtualNetworkPool.info(cloudClinet,-2);
		if (res.isError()){
			throw new InternalErrorException("Error getting VNet pool info. "+res.getErrorMessage());
		}else{
			String xml = res.getMessage();
			int nameend = 0;
			int namestart = 0;

			int idend = 0;
			int idstart = 0;

			while(nameend >= namestart){
				VNetInfo vnet = new VNetInfo();

				idstart = xml.indexOf("<ID>",idend);
				idstart+=4;
				idend = xml.indexOf("</ID>",idstart);
				String id = xml.substring(idstart, idend);
				if (!id.startsWith("<![CDATA")){
					vnet.id = id; 
				}

				namestart = xml.indexOf("<NAME>",idend);
				namestart+=6;
				if (namestart <= nameend)
					break;
				nameend = xml.indexOf("</NAME>",namestart);
				String name = xml.substring(namestart, nameend);
				if (!name.startsWith("<![CDATA")){
					vnet.name = name; 
				}
				ans.add(vnet);
			}
		}
		return ans;
	}

	public VNetInfo vnetAllocate(String desc) throws InternalErrorException {
		OneResponse res = VirtualNetwork.allocate(cloudClinet, desc);
		if (res.isError()){
			throw new InternalErrorException("Error allocating VNet. "+res.getErrorMessage());
		}
		VNetInfo vnetinfo = new VNetInfo();
		vnetinfo.id = res.getMessage();
		return vnetinfo;
	}

	public boolean vnetAction(VNetAction action, int vnetid) throws InternalErrorException {
		VirtualNetwork vnet = new VirtualNetwork(vnetid,cloudClinet);
		if (action == VNetAction.delete){
			OneResponse res = vnet.delete();
			if (res.isError()){
				throw new InternalErrorException("Deletion failed. "+res.getErrorMessage());
			}
		}
		return true;	
	}

	public VNetInfo getVNetInfo(int vnid) throws InternalErrorException {
		VirtualNetwork vneto = new VirtualNetwork(vnid,cloudClinet);

		OneResponse res = vneto.info();
		if (res.isError()){
			throw new InternalErrorException("Error getting Host info. "+res.getErrorMessage());
		}else{
			String xml = res.getMessage();
			System.out.println("VNet info: "+xml);
			int nameend = 0;
			int namestart = 0;

			int idend = 0;
			int idstart = 0;

			int ipstart = 0;
			int ipend = 0;

			VNetInfo vn = new VNetInfo();

			idstart = xml.indexOf("<ID>",idend);
			idstart+=4;
			idend = xml.indexOf("</ID>",idstart);
			String id = extractString(xml, idstart, idend);
			vn.id = id; 

			namestart = xml.indexOf("<NAME>",nameend);
			namestart+=6;
			nameend = xml.indexOf("</NAME>",nameend);
			vn.name = extractString(xml, namestart, nameend);


			if (xml.indexOf("FIXED")!=-1){
				while (ipstart <= ipend){
					ipstart = xml.indexOf("<IP>",ipend);
					ipstart+=4;
					if (ipstart < ipend)
						break;
					ipend = xml.indexOf("</IP>",ipstart);
					String IP = extractString(xml, ipstart, ipend);
					if (!vn.IPs.contains(IP))
						vn.IPs.add(IP);
				}
			}else if (xml.indexOf("FIXED")==-1){
				ipstart = xml.indexOf("<NETWORK_ADDRESS>",nameend);
				ipstart+=17;
				ipend = xml.indexOf("</NETWORK_ADDRESS>",nameend);
				String IP = extractString(xml, ipstart, ipend);
				String sub = IP.substring(0,IP.lastIndexOf(".")+1);
				for (int i = 0; i < 256; i++  )
					vn.IPs.add(sub+i);
			}
			vn.state = CloudConstants.VNetState.READY;
			return vn;
		}
	}

}
