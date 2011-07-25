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

package org.uoa.eolus.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.persistence.EntityManager;

import org.opennebula.bridge.CloudClient;
import org.opennebula.bridge.CloudConstants;
import org.opennebula.bridge.VNetInfo;
import org.uoa.eolus.InternalErrorException;
import org.uoa.eolus.config.ConfigurationsManager;
import org.uoa.eolus.config.ONEInfo;
import org.uoa.eolus.user.UnknownUserException;
import org.uoa.eolus.user.User;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jackal
 */
public class VirtualNetManager {

	CloudClient cloud = null;
	EntityManager em = null;

	public VirtualNetManager(EntityManager em) throws InternalErrorException {
		this.em = em;
		Init();
	}

	private void Init() throws InternalErrorException {
		try {
			cloud = ConfigurationsManager.getCloudConnector(em);
		} catch (Exception ex) {
			cloud = null;
			throw new InternalErrorException(ex.getMessage(), ex);
		}
		syncVNets();
	}

	private void syncVNets() throws InternalErrorException {

		List<VirtualNet> vns = em.createQuery("select vn from VirtualNet vn ").getResultList();
		List<VNetInfo> cloudvnets = cloud.getAllVNets();

		
		//Cleanup local db
		for (Object n : vns) {
			VirtualNet vn = (VirtualNet) n;
			String name = vn.getId();
			boolean found = false;

			Iterator<VNetInfo> it = cloudvnets.iterator();
			while (it.hasNext()) {
				VNetInfo vnet = it.next();
				if (vnet.name.equals(name)) {
					found = true;
				}
			} 
			if (!found) {
				em.remove(vn);
			}
		}


		//Update local DB with unregistered vnets. Assign them to the private user.
		User u = em.find(User.class, "private");
		
		for (VNetInfo cloudvnet : cloudvnets) {
			boolean found = false;

			Iterator<VirtualNet> it = vns.iterator();
			while (it.hasNext()) {
				VirtualNet vn = it.next();
				String name = vn.getId();
				if (name.equals(cloudvnet.name)) {
					found = true;
				}
			} 
			if (!found) {
				try {
					long vnetID = Long.parseLong(cloudvnet.id);
					VirtualNet newvnet = new VirtualNet();
					newvnet.setId(cloudvnet.name);
					newvnet.setUser(u);
					newvnet.setOneID(vnetID);
					em.persist(newvnet);
				} catch (Exception e) {
					throw new InternalErrorException("Failed due to: " + e.getMessage(), e);
				}

			}
		}
	}

	
	public void Create(String username, String VNname, int type, String description) throws InternalErrorException, VNExistsException {
		List<VirtualNet> ts = em.createQuery("select vn from VirtualNet vn where vn.id='" + VNname + "'").getResultList();
		if (ts.size() != 0) {
			throw new VNExistsException("VNet with name "+VNname+" already exists");
		}

		User user = em.find(User.class, username);
		if (user == null){
			System.out.println("User with name "+username+" not found");
			user = em.find(User.class, "private");
		}

		String desc = BuildNetworkDescription(VNname, type, description);
		
		VNetInfo vnet = cloud.vnetAllocate(desc);
		VirtualNet vn = new VirtualNet();
		vn.setId(VNname);
		vn.setOneID(new Long(vnet.id));
		vn.setUser(user);
		em.persist(vn);

	}


	private String BuildNetworkDescription(String nname, int type, String description) throws InternalErrorException {
		
		// Given IPs
		if (type == 0) {
			ONEInfo oneinfo = getONEInfo();
	
			String filename = "/var/tmp/vnet-"+description+".txt";
			String content = "" +
			"NAME = \""+nname+"\"\n" +
			"TYPE = FIXED\n" +
			"BRIDGE = "+oneinfo.getNetBridge()+"\n";
			
			// Split the description
			String[] ips = description.split("-");
			for (int i = 0; i < ips.length; i++){
				//content += "LEASES = [IP=192.168."+description+"."+i+"]\n";
				String[] ipMac = ips[i].split(",");
				if (ipMac.length <2)
					content += "LEASES = [IP=" + ips[i] + "]\n";
				else
					content += "LEASES = [IP=" + ipMac[0] + ", MAC=" + ipMac[1] + "]\n";
			}
	
			System.out.println("PRINTING FILE: " + content);
			return content;
		} 
		// Subnetwork
		else {
			ONEInfo oneinfo = getONEInfo();
			String content = "" + 
			"NAME = \""+nname+"\"\n" +
			"TYPE = RANGED\n" +
			"PUBLIC = NO\n" +			// See if we need to make a distinction
			"BRIDGE = "+oneinfo.getNetBridge()+"\n" +
			"NETWORK_SIZE = C\n" +
			"NETWORK_ADDRESS = " + description +"\n";
			;
			
			System.out.println("Printing file: " + content);
			return content;
		}
	}

	public boolean userOwnsVNet(String user, String VNname){
		List<VirtualNet> ts = em.createQuery("select vn from VirtualNet vn where vn.id='" + VNname + "'" +
				" and vn.user.id='"+user+"'").getResultList();
		if (ts.size()!=0)
			return true;
		else
			return false;
	}

	public synchronized void Remove(String username, String VNname) 
	throws InternalErrorException, UnknownVNException, UnknownUserException{
		if (!userOwnsVNet(username, VNname)) 
			throw new UnknownUserException("Permission denied.");
		else
			Remove(VNname);
	}

	public synchronized void Remove(String VNname) throws InternalErrorException, UnknownVNException {

		List<VirtualNet> ts = em.createQuery("select vn from VirtualNet vn where vn.id='" + VNname + "'").getResultList();
		if (ts == null || ts.size()==0){
			throw new UnknownVNException("Virtual network "+VNname+" not found");
		}
		VirtualNet vn = ts.get(0);

		int VMID = vn.getOneID().intValue();

		cloud.vnetAction(CloudConstants.VNetAction.delete, VMID);				
		em.remove(vn);
	}

	private String getONEDir() throws InternalErrorException{
		ONEInfo oneinfo = getONEInfo();
		return oneinfo.getONEdir();
	}

	public synchronized String getInfo(String username, String VNname) throws UnknownVNException, InternalErrorException, UnknownUserException {
		if (!userOwnsVNet(username, VNname))
			throw new UnknownUserException("Permission denied.");

		return getInfo(VNname);
	}

	public synchronized String getInfo(String VNname) throws UnknownVNException, InternalErrorException {
		List<VirtualNet> ts = em.createQuery("select vn from VirtualNet vn where vn.id='" + VNname + "'").getResultList();
		if (ts == null || ts.size()==0)
			throw new UnknownVNException("VM "+VNname+" not found");

		VirtualNet vn = ts.get(0);

		int VNID = vn.getOneID().intValue();

		VNetInfo vnetinfo = cloud.getVNetInfo(VNID);
		return vnetinfo.toXML();
	}

	public synchronized List<String> getVNetList(String user) {
		List<String> vns = em.createQuery("select vn.id from VirtualNet vn where vn.user.id='"+user+"'").getResultList();
		return vns;
	}
	
	public synchronized List<String> getPublicVNetList() {
		List<String> vns = em.createQuery("select vn.id from VirtualNet vn where vn.user.id='public'").getResultList();
		return vns;
	}

	public void assignVNettoUser(String username, String VNname) throws UnknownUserException, UnknownVNException{
		User user = em.find(User.class, username);
		if (user == null)
			throw new UnknownUserException();

		List<VirtualNet> ts = em.createQuery("select vn from VirtualNet vn where vn.id='" + VNname + "'").getResultList();
		if (ts == null || ts.size()==0){
			throw new UnknownVNException("VM "+VNname+" not found");
		}
		VirtualNet vn = ts.get(0);
		vn.setUser(user);
	}

	public List<String> getVNetList() {
		List<String> ts = em.createQuery("select vn.id from VirtualNet vn").getResultList();
		if (ts == null)
			ts = new ArrayList<String>();
		return ts;
	}

	private ONEInfo getONEInfo() throws InternalErrorException {
		ConfigurationsManager configs = new ConfigurationsManager(em);
		ONEInfo oneinfo = configs.getONEInfo();
		if (oneinfo == null){
			oneinfo = new ONEInfo();
		}
		return oneinfo;
	}


}
