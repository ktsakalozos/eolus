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

package org.uoa.eolus.config;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author jackal
 */ 
@Entity
public class ONEInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id; // name
	private String ONEdir = "/home/oneadmin/ONE-1.4";
	private String netBridge = "xenbr0";
	
	public void cloneFrom(ONEInfo oneconf){
		setONEdir(oneconf.getONEdir());
		setNetBridge(oneconf.getNetBridge());
	}
	
	private static String convertToHex(byte[] data) { 
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) { 
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do { 
				if ((0 <= halfbyte) && (halfbyte <= 9)) 
					buf.append((char) ('0' + halfbyte));
				else 
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while(two_halfs++ < 1);
		} 
		return buf.toString();
	} 

	public static String SHA1(String text) 
	throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		sha1hash = md.digest();
		return convertToHex(sha1hash);
	}


	public static String buildSession(String username, String password) 
		throws NoSuchAlgorithmException, UnsupportedEncodingException {
			return username+":"+SHA1(password);
	}
	
    public String getONEdir() {
        return ONEdir;
    }

    public void setONEdir(String ONEdir) {
        this.ONEdir = ONEdir;
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
        if (!(object instanceof ONEInfo)) {
            return false;
        }
        ONEInfo other = (ONEInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eolus.ONEInfo[id=" + id + "]";
    }

	public void setNetBridge(String netBridge) {
		this.netBridge = netBridge;
	}

	public String getNetBridge() {
		return netBridge;
	}

}
