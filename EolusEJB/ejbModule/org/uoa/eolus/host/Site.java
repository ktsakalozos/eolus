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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CollectionOfElements;
import org.uoa.eolus.InternalErrorException;

import sun.security.action.GetLongAction;


@Entity
public class Site implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6175710615994099756L;
	@Id
    private String id = "noSite";// hostname;
	private boolean isLiveMigrationCapable = false;
	private boolean isMigrationCapable = true;

	@CollectionOfElements
    private List<String> hosts = new ArrayList<String>();

	@CollectionOfElements
    private List<String> properties = new ArrayList<String>();
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Site)) {
            return false;
        }
        Site other = (Site) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Eolus.User[id=" + id + "]";
    }

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setLiveMigrationCapable(boolean isLiveMigrationCapable) {
		this.isLiveMigrationCapable = isLiveMigrationCapable;
	}

	public boolean isLiveMigrationCapable() {
		return isLiveMigrationCapable;
	}

	public void setMigrationCapable(boolean isMigrationCapable) {
		this.isMigrationCapable = isMigrationCapable;
	}

	public boolean isMigrationCapable() {
		return isMigrationCapable;
	}

	public boolean updateProperty(String property, String value){
		if (property.equalsIgnoreCase("LiveMigrate"))
			setLiveMigrationCapable(Boolean.parseBoolean(value));
		if (property.equalsIgnoreCase("Migrate"))
			setMigrationCapable(Boolean.parseBoolean(value));
		return true;
	}

	public String readProperty(String property) throws InternalErrorException {
		if (property.equalsIgnoreCase("LiveMigrate"))
			return Boolean.toString(isLiveMigrationCapable());
		if (property.equalsIgnoreCase("Migrate"))
			return Boolean.toString(isMigrationCapable());
		throw new InternalErrorException("Unknown property: "+ property);
	}

	
	public String toXML() {
		String xml = "";
		xml += "<SITE>";
		xml += "<HOSTS>";
		for (String h : hosts){
			xml += "<HOST>"+h+"</HOST>";
		}
		xml += "</HOSTS>";
		xml += "<PROPERTIES>";
		xml += "<LIVEMIGRATE>"+Boolean.toString(isLiveMigrationCapable())+"</LIVEMIGRATE>";
		xml += "<MIGRATE>"+Boolean.toString(isMigrationCapable())+"</MIGRATE>";
		xml += "</PROPERTIES>";
		xml += "</SITE>";
		return xml;
	}

	public void addHost(String host){
		hosts.add(host);
	}

	public void removeHost(String host){
		hosts.remove(host);
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	public List<String> getHosts() {
		return hosts;
	}
	
}
