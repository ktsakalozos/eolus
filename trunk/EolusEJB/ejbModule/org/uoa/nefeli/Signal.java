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
public class Signal implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private TaskflowEntry task;

    private Integer reciever;

    private String word;
    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        if (!(object instanceof Signal)) {
            return false;
        }
        Signal other = (Signal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "nefeli.TasklowEntry[id=" + id + "]";
    }

	public void setTask(TaskflowEntry task) {
		this.task = task;
	}

	public TaskflowEntry getTask() {
		return task;
	}

	public void setReciever(Integer reciever) {
		this.reciever = reciever;
	}

	public Integer getReciever() {
		return reciever;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

}
