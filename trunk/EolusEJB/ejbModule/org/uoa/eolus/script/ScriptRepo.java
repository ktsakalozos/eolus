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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uoa.eolus.script;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author jackal
 */
@Entity
public class ScriptRepo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    
	private String scriptsRepo = "/fn/a/ONE-repository/.gcube-scripts";

    
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ScriptRepo)) {
            return false;
        }
        ScriptRepo other = (ScriptRepo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eolus.ScriptRepo[id=" + id + "]";
    }

	public void cloneFrom(ScriptRepo cur) {
		setScriptsRepo(cur.getScriptsRepo());
	}

	public void setScriptsRepo(String scriptsRepo) {
		this.scriptsRepo = scriptsRepo;
	}

	public String getScriptsRepo() {
		return scriptsRepo;
	}

}
