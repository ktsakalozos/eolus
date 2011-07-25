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

package org.uoa.nefeli;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.uoa.eolus.user.User;
import org.uoa.nefeli.parser.WorkloadDesc;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.uoa.nefeli.parser.Machine;
import org.uoa.nefeli.parser.WorkloadDesc;
import org.uoa.nefeli.parser.constraints.Constraint;
import org.uoa.nefeli.parser.Profile;
import org.uoa.nefeli.parser.constraints.EmptyHostNodeWish;
import org.uoa.nefeli.parser.constraints.EvenHostNodeWish;
import org.uoa.nefeli.parser.constraints.FavourVMWish;
import org.uoa.nefeli.parser.constraints.MinTraficWish;
import org.uoa.nefeli.parser.constraints.ParallelPCsWish;
import org.uoa.nefeli.parser.events.AndEvents;
import org.uoa.nefeli.parser.events.InvertEvent;
import org.uoa.nefeli.parser.events.MagicWordEvent;
import org.uoa.nefeli.parser.events.OrEvents;

@Entity
public class TaskflowEntry implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @OneToMany
    private Set<Signal> keyMap;

	private String status = "noStatus"; //Taskflow status "submitted" or "staging" or "running" or "stimulated" or "finalized" or "terminated"
    private String description = "";
    private Integer CurentProfile;


	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		WorkloadDesc w = BuildWorkloadObj();
		setCurentProfile(w.getStartingProfile());
	}

    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public WorkloadDesc BuildWorkloadObj(){
		XStream xs = new XStream(new DomDriver());
		xs.alias("Machine", Machine.class);
		xs.alias("WorkFlow", WorkloadDesc.class);
		xs.alias("Wish", Constraint.class);
		xs.alias("ParallelPCs", ParallelPCsWish.class);
		xs.alias("MinTrafic", MinTraficWish.class);
		xs.alias("FavourVM", FavourVMWish.class);
		xs.alias("EvenHostNode", EvenHostNodeWish.class);
		xs.alias("EmptyHostNode", EmptyHostNodeWish.class);
		xs.alias("State", Profile.class);
		xs.alias("AndEvents", AndEvents.class);
		xs.alias("OrEvents", OrEvents.class);
		xs.alias("InvertEvent", InvertEvent.class);
		xs.alias("MagicWordEvent", MagicWordEvent.class);
		WorkloadDesc workload = (WorkloadDesc) xs.fromXML(description);
		Iterator<Machine> vm = workload.getMachines().iterator();
		while (vm.hasNext()) {
			Machine machine = (Machine) vm.next();
			machine.setName(machine.getName());
		}
		if (user != null)
			workload.setUser(user.getId());
		
		return workload;
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
        if (!(object instanceof TaskflowEntry)) {
            return false;
        }
        TaskflowEntry other = (TaskflowEntry) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "nefeli.TasklowEntry[id=" + id + "]";
    }

	public boolean checkMagicWord(Integer receiver, String keyWord) {
//		if (!keysMap.containsKey(receiver))
//			return false;
//		if (keysMap.get(receiver).equalsIgnoreCase(keyWord))
//			return true;
		return false;
	}

	public void setCurentProfile(Integer curentProfile) {
		CurentProfile = curentProfile;
	}

	public Integer getCurentProfile() {
		return CurentProfile;
	}

	public void setKeyMap(Set<Signal> keyMap) {
		this.keyMap = keyMap;
	}

	public Set<Signal> getKeyMap() {
		return keyMap;
	}

}
