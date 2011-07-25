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

package org.uoa.nefeli.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uoa.nefeli.parser.Machine;
import org.uoa.nefeli.parser.Profile;
import org.uoa.nefeli.parser.Transition;
import org.uoa.nefeli.parser.WorkloadDesc;
import org.uoa.nefeli.parser.constraints.Constraint;
import org.uoa.nefeli.parser.constraints.MinTraficWish;
import org.uoa.nefeli.parser.constraints.ParallelPCsWish;
import org.uoa.nefeli.parser.events.Event;
import org.uoa.nefeli.parser.events.MagicWordEvent;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TestXStream {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<Integer, List<Integer>> a = new HashMap<Integer, List<Integer>>();

		WorkloadDesc work = new WorkloadDesc();

		Machine m = new Machine();
		m.setName("vm1");
		m.setRAM(512);
		m.setCores(1);
		m.setTemplate("tc22pub");
		String[] nets = {"public"};
		m.setNetwork(nets);
		work.add(m);

		Machine m2 = new Machine();
		m2.setName("vm2");
		m2.setRAM(512);
		m2.setCores(1);
		m2.setTemplate("tc22pub");
		String[] nets2 = {"public"};
		m2.setNetwork(nets2);
		work.add(m2);

		Machine m3 = new Machine();
		m3.setName("vm3");
		m3.setRAM(512);
		m3.setCores(1);
		m3.setTemplate("tc22pub");
		String[] nets3 = {"public"};
		m3.setNetwork(nets3);
		work.add(m3);

		Machine m4 = new Machine();
		m4.setName("vm4");
		m4.setRAM(512);
		m4.setCores(1);
		m4.setTemplate("tc22pub");
		String[] nets4 = {"public"};
		m4.setNetwork(nets4);
		work.add(m4);

		
		ParallelPCsWish par = new ParallelPCsWish();
		par.setID(1);
		List<String> pl = new ArrayList<String>();
		pl.add("vm1");
		pl.add("vm2");
		par.setPCs(pl);


		ParallelPCsWish par2 = new ParallelPCsWish();
		par2.setID(2);
		List<String> pl2 = new ArrayList<String>();
		pl2.add("vm1");
		pl2.add("vm2");
		pl2.add("vm3");
		pl2.add("vm4");
		par2.setPCs(pl2);

		List<Constraint> ls = new ArrayList<Constraint>();
		ls.add(par);
		ls.add(par2);
		work.setConstraints(ls);
		
		Profile s1 = new Profile();
		s1.setID(0);
		Map<Integer, Double> prefs = new HashMap<Integer, Double>();
		prefs.put(1, 0.5);
		prefs.put(2, 0.0);
		s1.setPreferences(prefs);

		Profile s2 = new Profile();
		s2.setID(1);
		Map<Integer, Double> prefs2 = new HashMap<Integer, Double>();
		prefs2.put(1, 0.0);
		prefs2.put(2, 1.0);
		s2.setPreferences(prefs2);

		List<Profile> states = new ArrayList<Profile>();
		states.add(s1);
		states.add(s2);
		work.setProfiles(states);
		work.setStartingProfile(0);
		
		List<Event> events = new ArrayList<Event>();
		MagicWordEvent e = new MagicWordEvent();
		e.setID(0);
		e.setKeyWord("dvd");
		e.setPort(1);
		events.add(e);

		MagicWordEvent e2 = new MagicWordEvent();
		e2.setID(1);
		e2.setKeyWord("cd");
		e2.setPort(2);
		events.add(e2);
		work.setEvents(events);
		
		Transition t = new Transition();
		t.setFrom(0);
		t.setTo(1);
		List<Integer> el = new ArrayList<Integer>();
		el.add(0);
		t.setEvents(el);

		Transition t2 = new Transition();
		t2.setFrom(1);
		t2.setTo(0);
		List<Integer> el2 = new ArrayList<Integer>();
		el2.add(1);
		t2.setEvents(el2);

		List<Transition> tl = new ArrayList<Transition>();
		tl.add(t);
		tl.add(t2);

		work.setTransitions(tl);
				
		XStream xs = new XStream(new DomDriver());
		
		String xml = xs.toXML(work);
		System.out.println(xml);
		
	}

}
