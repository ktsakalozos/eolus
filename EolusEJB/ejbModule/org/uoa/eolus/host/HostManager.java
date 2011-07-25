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

package org.uoa.eolus.host;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;

import org.opennebula.bridge.CloudClient;
import org.opennebula.bridge.CloudConstants;
import org.opennebula.bridge.HostInfo;
import org.uoa.eolus.InternalErrorException;
import org.uoa.eolus.config.ConfigurationsManager;

public class HostManager {

	CloudClient cloud = null;
	EntityManager em = null;
	
	public HostManager(EntityManager em) throws InternalErrorException {
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
		syncHosts();
	}

	
	private void syncHosts() throws InternalErrorException {

		List<Host> hosts = em.createQuery("select h from Host h ").getResultList();
		List<String> hostsFromONE = getHostList();
		
		//Cleanup local db
		for (Host h : hosts) {
			boolean found = false;

			Iterator<String> it = hostsFromONE.iterator();
			while (it.hasNext()) {
				String cname = it.next();
				if (h.getId().equals(cname)) {
					System.out.println("Host "+h.getId()+" is kept on local DB."+h.getSitename());
					found = true;
				}
			} 
			if (!found) {
				System.out.println("Host "+h.getId()+" is removed from local DB.");
				em.remove(h);
			}
		}

		//Update local DB with unregistered hosts. Assign them to the default site.
		Site defaultSite = em.find(Site.class, "default");
		if (defaultSite == null){
			defaultSite = new Site();
			defaultSite.setId("default");
			em.persist(defaultSite);
		}
		
		for (String onename : hostsFromONE) {
			boolean found = false;

			Iterator<Host> it = hosts.iterator();
			while (it.hasNext()) {
				Host h = it.next();
				if (h.getId().equals(onename)) {
					System.out.println("Host "+h.getId()+" found on the local DB."+h.getSitename());
					if (h.getSitename() == null){
						h.setSitename("default");
						if (!defaultSite.getHosts().contains(h.getId())){
							defaultSite.addHost(h.getId());
							em.persist(defaultSite);
						}
						em.persist(h);
					}else{
						Site s = em.find(Site.class, h.getSitename());
						if (s == null){
							h.setSitename("default");
							em.persist(h);
						}else{
							if (!s.getHosts().contains(h.getId())){
								s.addHost(h.getId());
								em.persist(s);
							}	
						}
					}
					found = true;
				}
			} 
			if (!found) {
				int hid = cloud.getHostIDfromHostName(onename);
				Host h = new Host();
				h.setId(onename);
				h.setSitename(defaultSite.getId());
				h.setCloudid(new Long(hid));
				System.out.println("Adding to default site host "+h.getId());
				defaultSite.addHost(h.getId());
				em.persist(defaultSite);
				em.persist(h);
			}
		}

	}


	
	public void Create(String hostName) throws IOException, InternalErrorException {
		Host ho = em.find(Host.class, hostName);
		if (ho != null)
			throw new InternalErrorException("Host with name "+hostName+" already exist");
		
		HostInfo hostinfo = cloud.hostAllocate(hostName, "im_xen", "vmm_xen", "tm_ssh");
		System.out.println("Building hist info.");
		Host h = new Host();
		Site defaultSite = em.find(Site.class, "default");
		h.setSitename(defaultSite.getId());
		h.setCloudid(Long.parseLong(hostinfo.id));
		h.setId(hostName);
		System.out.println("Update default site with host id" +h.getId()+ " (cloud id: "+ Long.parseLong(hostinfo.id) +").");
		defaultSite.addHost(h.getId());
		System.out.println("Persist default site.");
		em.persist(defaultSite);
		System.out.println("Persist host info.");
		em.persist(h);
		System.out.println("Done.");
	}

	public void Delete(String hostname) throws InternalErrorException {
		Host h = em.find(Host.class, hostname);
		if (h == null)
			throw new InternalErrorException("Host with name "+hostname+" does not exist");

		cloud.hostAction(CloudConstants.HostAction.delete,h.getCloudid().intValue());
		Site s = em.find(Site.class, h.getSitename());
		if (s == null){
			System.out.println("Site does not exist.");
		}else{
			s.removeHost(h.getSitename());
			em.persist(s);
		}
		
		em.remove(h);
	}

	public void CreateSite(String siteName) throws InternalErrorException {
		Site s = em.find(Site.class, siteName);
		if (s != null){
			throw new InternalErrorException("Site already exists.");
		}
		s = new Site();
		s.setId(siteName);
		em.persist(s);
	}

	public String ReadSite(String siteName, String property) throws IOException, InternalErrorException {
		Site s = em.find(Site.class, siteName);
		if (s == null){
			throw new InternalErrorException("Site does not exist.");
		}
		return s.readProperty(property);
	}
	
	public boolean UpdateSite(String siteName, String property, String value) throws IOException, InternalErrorException {
		Site s = em.find(Site.class, siteName);
		if (s == null){
			throw new InternalErrorException("Site does not exist.");
		}

		if (s.updateProperty(property, value)){
			em.persist(s);
			return true;
		}else{
			return false;
		}
	}

	public String getSiteInfo(String siteName) throws IOException, InternalErrorException {
		Site s = em.find(Site.class, siteName);
		if (s == null){
			throw new InternalErrorException("Site does not exist.");
		}
		return s.toXML();
	}

	public List<String> getSiteList() {
		List<Site> sites = em.createQuery("select s from Site s ").getResultList();
		List<String> ans = new ArrayList<String>();
		Iterator<Site> it = sites.iterator();
		while (it.hasNext()) {
			Site site = (Site) it.next();
			ans.add(site.getId());
		}
		return ans;
	}

	public List<String> getSiteHosts(String sitename) throws InternalErrorException{
		Site s = em.find(Site.class, sitename);
		if (s == null){
			throw new InternalErrorException("Site does not exist.");
		}
		return s.getHosts();
	}

	public void DeleteSite(String siteName) throws InternalErrorException {
		Site s = em.find(Site.class, siteName);
		if (s == null){
			throw new InternalErrorException("Site does not exist.");
		}
		Site defaultSite = em.find(Site.class, "default");
		if (defaultSite == null){
			defaultSite = new Site();
			defaultSite.setId("default");
			em.persist(defaultSite);
		}
		
		for(String hostname : s.getHosts()){
			Host h = em.find(Host.class, hostname);
			if (h == null)
				continue;
			h.setSitename(defaultSite.getId());
			defaultSite.addHost(h.getId());
			em.persist(h);
		}
		em.remove(s);
		em.persist(defaultSite);
	}

	public void AddHostToSite(String hostname, String siteName) throws InternalErrorException {
		Site s = em.find(Site.class, siteName);
		if (s == null){
			throw new InternalErrorException("Site does not exist.");
		}
		Host h = em.find(Host.class, hostname);
		if (h == null){
			throw new InternalErrorException("Host does not exist.");
		}
		Site oldsite = getSite(h.getSitename());
		oldsite.removeHost(hostname);
		em.persist(oldsite);

		h.setSitename(siteName);
		em.persist(h);

		s.addHost(hostname);
		em.persist(s);
	}
	
	public void RemoveHostFromSite(String hostname) throws InternalErrorException {
		Host h = em.find(Host.class, hostname);
		if (h == null){
			throw new InternalErrorException("Host does not exist.");
		}
		
		Site s = getSite(h.getSitename());
		Site defaultSite = em.find(Site.class, "default");
		s.removeHost(hostname);
		h.setSitename(defaultSite.getId());
		defaultSite.addHost(h.getId());
		em.persist(defaultSite);
		em.persist(s);
		em.persist(h);
	}
	
	public void SetSiteConnectivity(String siteA, String siteB, String rank) throws InternalErrorException {
		if (siteA.compareToIgnoreCase(siteB) > 0){
			String tmp = siteA;
			siteA = siteB;
			siteB = tmp;
		}
		String key = siteA+"-"+siteB;
		Connectivity connect = em.find(Connectivity.class, key);
		if (connect == null){
			connect = new Connectivity();
			connect.setId(key);
		}
		connect.setRank(rank);
		em.persist(rank);
	}

	public String getSiteConnectivity(String siteA, String siteB) throws InternalErrorException {
		if (siteA.compareToIgnoreCase(siteB) > 0){
			String tmp = siteA;
			siteA = siteB;
			siteB = tmp;
		}
		String key = siteA+"-"+siteB;
		Connectivity connect = em.find(Connectivity.class, key);
		if (connect == null){
			return "no";
		}
		return connect.getRank();
	}
	
	
	
	public void Enable(String hostname, boolean enable) throws InternalErrorException {
		Host h = em.find(Host.class, hostname);
		if (h == null)
			throw new InternalErrorException("Host with name "+hostname+" does not exist");

		if (enable)
			cloud.hostAction(CloudConstants.HostAction.enable,h.getCloudid().intValue());
		else
			cloud.hostAction(CloudConstants.HostAction.disable,h.getCloudid().intValue());

	}

	
	public String getHostInfo(String hostname) throws InternalErrorException{
		Host h = em.find(Host.class, hostname);
		if (h == null)
			throw new InternalErrorException("Host with name "+hostname+" does not exist");

		System.out.println("Host info : "+cloud.getHostInfo(h.getCloudid().intValue()).toXML());
		return cloud.getHostInfo(h.getCloudid().intValue()).toXML();
	}
	
	public Host getHostStats(String hostname) throws InternalErrorException{
		Host h = em.find(Host.class, hostname);
		if (h == null){
			throw new InternalErrorException("Host does not exist.");
		}
		return h;
	}

	public List<String> getHostList() throws InternalErrorException {
		List<String> names = new ArrayList<String>();
		List<HostInfo> hosts = cloud.getAllHosts();
		
		Iterator<HostInfo> hi = hosts.iterator();
		while (hi.hasNext()) {
			HostInfo hostInfo = (HostInfo) hi.next();
			names.add(hostInfo.name);
		}
		return names;
	}

	public Integer getHostIDfromHostName(String hostname) throws InternalErrorException {
		Host h = em.find(Host.class, hostname);
		if (h == null){
			throw new InternalErrorException("Host does not exist.");
		}
		return h.getCloudid().intValue();
	}

	public Site getSite(String site) throws InternalErrorException {
		Site s = em.find(Site.class, site);
		if (s == null){
			throw new InternalErrorException("Site does not exist.");
		}
		return s;
	}

}
