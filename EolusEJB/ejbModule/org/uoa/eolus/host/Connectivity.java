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

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Connectivity implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6175710615994099756L;
	@Id
    private String id = "SiteA-SiteB";// hostnames;
    private String rank;// "no" or "poor" or "fair" "good" or "excellent"
    
    private Site SiteA = null;
    
    public String getId() {
        return id;
    }

    private Boolean involvesSite(String site){
    	if (id.startsWith(site) || id.endsWith(site))
    		return true;
    	return false;
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
        if (!(object instanceof Connectivity)) {
            return false;
        }
        Connectivity other = (Connectivity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Eolus.User[id=" + id + "]";
    }

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getRank() {
		return rank;
	}

}
