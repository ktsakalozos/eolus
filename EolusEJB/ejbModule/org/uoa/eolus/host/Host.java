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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CollectionOfElements;


@Entity
public class Host implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6175710615994099756L;
	@Id
    private String id = "noHost";// hostname;
    private Long cloudid;// hostname;
    private String sitename;
    
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
        if (!(object instanceof Host)) {
            return false;
        }
        Host other = (Host) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Eolus.Host[id=" + id + "]";
    }

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setCloudid(Long cloudid) {
		this.cloudid = cloudid;
	}

	public Long getCloudid() {
		return cloudid;
	}

	public void setSitename(String sitename) {
		this.sitename = sitename;
	}

	public String getSitename() {
		return sitename;
	}

}
