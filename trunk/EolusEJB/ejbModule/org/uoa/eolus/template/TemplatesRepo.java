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

package org.uoa.eolus.template;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author jackal
 */
@Entity
public class TemplatesRepo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
//    @GeneratedValue(generator="system-uuid")
//    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id; //Repository path

    private String TemplatesRepo = "/fn/a/ONE-repository/gcube-domains";
    private String Template2VMConfDir = "/var/tmp";
    private String kernel = "vmlinuz-2.6.26-1-xen-amd64";
    private String initrd = "initrd.img-2.6.26-1-xen-amd64";

    public String getTemplate2VMConfDir() {
        return Template2VMConfDir;
    }

    public void setTemplate2VMConfDir(String Template2VMConfDir) {
        this.Template2VMConfDir = Template2VMConfDir;
    }

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
        if (!(object instanceof TemplatesRepo)) {
            return false;
        }
        TemplatesRepo other = (TemplatesRepo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eolus.TemplatesRepo[id=" + id + "]";
    }

	public void cloneFrom(TemplatesRepo cur) {
		setTemplate2VMConfDir(cur.getTemplate2VMConfDir());
		setInitrd(cur.getInitrd());
		setKernel(cur.getKernel());
		setTemplatesRepo(cur.getTemplatesRepo());
	}

	public void setTemplatesRepo(String templatesRepo) {
		TemplatesRepo = templatesRepo;
	}

	public String getTemplatesRepo() {
		return TemplatesRepo;
	}

	public void setKernel(String kernel) {
		this.kernel = kernel;
	}

	public String getKernel() {
		return kernel;
	}

	public void setInitrd(String initrd) {
		this.initrd = initrd;
	}

	public String getInitrd() {
		return initrd;
	}

}
