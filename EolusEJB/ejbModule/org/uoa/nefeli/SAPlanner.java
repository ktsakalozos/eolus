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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.uoa.nefeli.cloud.Infrastructure;
import org.uoa.nefeli.costfunctions.CostFunction;
import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * A planner that uses simulated anealing to do the planning
 * @author Konstantinos Tsakalozos
 *
 */
public class SAPlanner extends Planner {

	boolean ready;
	Infrastructure infra;

	Plan current_plan = null;
	Plan min_plan = null;
	Plan tmp_plan = null;

	Random rand;
	double temperature;

	List<HostInfo> hosts;
	List<HostInfo> original_hosts;

	List<VMTemplate> vms = new ArrayList<VMTemplate>();
	int sameCount;
	int tries;

	public SAPlanner(Infrastructure infra, double temper, int same, int retries) {
		this.infra = infra;
		rand = new Random();
		current_plan = new Plan();
		tmp_plan = new Plan();
		min_plan = new Plan();
		ready = false;
		sameCount = same;
		tries = retries;
		temperature = temper;
		functions = new ArrayList<CostFunction>();

	}

	public void Reset(){
		vms.clear();
		functions.clear();
	}
	
	public void AddVMs(List<VMTemplate> vm) {
		vms = vm;
	}

	public void AddCostFunction(CostFunction f) {
		functions.add(f);
	}

	public void AddCostFunctions(List<CostFunction> cfs) {
		Iterator<CostFunction> fi = cfs.iterator();
		while(fi.hasNext()){
			functions.add(fi.next());
		}
	}

	private boolean anneal(double d, double temp) {
		if (temperature < 1.0E-4) {
			if (d > 0.0)
				return true;
			else
				return false;
		}
		if (Math.random() < Math.exp(d / temp))
			return true;
		else
			return false;
	}

	public boolean Schedule() throws Exception {
		hosts = infra.GatherDetails();
		CopyHostInfo();

		List<Plan> pop = new ArrayList<Plan>();
		for (int i = 0; i < tries; i++) {
			ProducePlan();
			Plan p = new Plan();
			p.ClonePlan(min_plan);
			pop.add(p);
		}

		for (int i = 0; i < pop.size(); i++)
			if (pop.get(i).rank < min_plan.rank)
				min_plan.ClonePlan(pop.get(i));

		ready = true;
		return true;
	}

	private void CopyHostInfo() {
		original_hosts = new ArrayList<HostInfo>();
		for (HostInfo hi : hosts){
			original_hosts.add(new HostInfo(hi));
		}
		
	}

	private void ProducePlan() throws Exception {
		int same = 0;
		int cycle = 0;
//		PrintHosts();
		current_plan = StartingGridyPlan(vms);
		min_plan.ClonePlan(current_plan);
		double temp = temperature;
		while (same < sameCount) {
//			System.out.println("Cycle=" + cycle + ",Length=" + min_plan.rank
//					+ ",Temp=" + temp);

			try {
				Plan p = Mutate(current_plan);
				if (anneal(p.rank, temp)) {
					current_plan.ClonePlan(tmp_plan);
				}

				if (current_plan.rank < min_plan.rank) {
					min_plan.ClonePlan(current_plan);
					same = 0;
				} else
					same++;
				temp = 0.99 * temp;
				cycle = 0;
			} catch (Exception x) {
				if (cycle > 100)
					same++;
				
//				x.printStackTrace();
			}
			cycle++;
		}
	}

	private void PrintHosts() {
		System.out.println("Hosts ");
		for (HostInfo hi : hosts){
			System.out.println("Host " +hi.name+ " Free mem "+hi.Mem_free+
					" Free CPU "+ hi.CPU_free );
		}		
	}

	private void PrintOriginalHosts() {
		System.out.println("Original Hosts ");
		for (HostInfo hi : hosts){
			System.out.println("Host " +hi.name+ " Free mem "+hi.Mem_free +
					" Free CPU "+ hi.CPU_free );
		}		
	}

	private HostInfo getHostByID(String ID) {
		Iterator<HostInfo> it = hosts.iterator();
		while (it.hasNext()) {
			HostInfo h = it.next();
			if (h.name.equalsIgnoreCase(ID)) {
				return h;
			}
		}
		return null;
	}

	private Plan Mutate(Plan p) throws Exception {

//		System.out.println("Mutate called");
		tmp_plan.Clean();

		for (int i = 0; i < 10; i++) {
//			System.out.println("Mutate attempt "+i);
			ResetHosts();
			Iterator<Entry<VMTemplate, HostInfo>> it = p.match.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<VMTemplate, HostInfo> e = it.next();
				VMTemplate key = e.getKey();
				if (Math.abs(rand.nextInt() % 100) > 80) {
					int hostID = Math.abs(rand.nextInt() % hosts.size());
					tmp_plan.match.put(key, getHostByID(hosts.get(hostID).name));
				} else {
					tmp_plan.match.put(key, getHostByID(e.getValue().name));
				}
			}
			if (ValidPlan(tmp_plan)) {
				tmp_plan.rank = Evaluate(tmp_plan);
				tmp_plan.Ready();
				return tmp_plan;
			}
		}
		
		throw new Exception("Invalid Plan");
	}

	private boolean ValidPlan(Plan p) {
//		ResetHosts();
		Iterator<Entry<VMTemplate, HostInfo>> it = p.match.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<VMTemplate, HostInfo> e = it.next();
			VMTemplate key = e.getKey();
			if (!CheckDeploy(e.getValue(), key)) {
				ResetHosts();
//				System.out.print(".");
//				System.out.println("Mutation failed");
				return false;
			}
		}
		if (!CheckConstraints(p)) {
			ResetHosts();
			return false;
		}
		p.Ready();
		ResetHosts();
		return true;
	}

	private Plan StartingPlan(List<VMTemplate> vm) throws Exception {
		int fails = 0;
		List<VMTemplate> VM4dep = new ArrayList<VMTemplate>();
		VM4dep.addAll(vm);
		fails = hosts.size() * 4;
		Plan p = new Plan();
		while (VM4dep.size() > 0 && fails > 0) {
			int hostID = Math.abs(rand.nextInt() % hosts.size());
			int VMID = Math.abs(rand.nextInt() % VM4dep.size());
			if (CheckDeploy(hosts.get(hostID), VM4dep.get(VMID))) {
				p.Deploy(VM4dep.get(VMID), hosts.get(hostID));
				VM4dep.remove(VMID);
				fails = hosts.size() * 4;
			} else {
				fails--;
			}
		}
		p.rank = Evaluate(p);
		ResetHosts();
		if (fails > 0)
			return p;
		else
			throw new Exception("No new plan could be created");
	}

	private Plan StartingGridyPlan(List<VMTemplate> vm) throws Exception {
		List<VMTemplate> VM4dep = new ArrayList<VMTemplate>();
		VM4dep.addAll(vm);
		int DeploiedVMs = 0;
		Plan p = new Plan();
		for (VMTemplate depVM : VM4dep ){
			for (HostInfo hi : hosts){
				if (hi.CPU_free < depVM.CPU) {
					continue;
				}
				if (hi.Mem_free < depVM.Mem) {
					continue;
				}

				if (CheckDeploy(hi, depVM)) {
					p.Deploy(depVM, hi);
					DeploiedVMs++;
					break;
				}
			}
		}
		if (VM4dep.size() > DeploiedVMs){
			ResetHosts();
			throw new Exception("No new plan could be created");
		}else{
			p.rank = Evaluate(p);
			ResetHosts();
			return p;
		}
	}

	
	private double Evaluate(Plan p) {
		//System.out.println("Evaluate plan:");
		//p.Print();
		Map<CostFunction, Double> m = ComputePlanCosts(p);
		Iterator<Double> it = m.values().iterator();
		double r = 0.0;
		while (it.hasNext()) {
			r += it.next().doubleValue();
		}
		//System.out.println("Score: "+r);
		return r;
	}

	private void ResetHosts() {
		for (int i = 0; i < hosts.size(); i++) {
			HostInfo original_h = GetOriginalHostInfo(hosts.get(i).name);
			HostInfo h = hosts.get(i);
			h.CPU_free = original_h.CPU_free;
			h.Mem_free = original_h.Mem_free;
		}
	}

	private HostInfo GetOriginalHostInfo(String id) {
		if (original_hosts == null)
			System.out.println("Original host info array is null!!!!");
		for (HostInfo hi: original_hosts){
			if (hi.name.equalsIgnoreCase(id))
				return hi;
		}
		return null;
	}

	private boolean CheckDeploy(HostInfo host, VMTemplate vm) {
//		System.out.println("Host: "+host.ID+"   free CPU: "+host.CPU_free+
//				"   free Mem: "+host.Mem_free+"   total Mem: "+host.Mem_total);
//		System.out.println("VM: "+vm.ID+"   CPU: "+vm.CPU+ 
//				"   Mem: "+vm.Mem	);
		if (host.CPU_free < vm.CPU) {
			return false;
		}
		if (host.Mem_free < vm.Mem) {
			return false;
		}

		host.CPU_free -= vm.CPU;
		host.Mem_free -= vm.Mem;

		return true;
	}

	private Map<CostFunction, Double> ComputePlanCosts(Plan p) {
		Map<CostFunction, Double> m = new HashMap<CostFunction, Double>();
	//	System.out.println("Number of cost functions used: "+functions.size());
		for (int i = 0; i < functions.size(); i++) {
			//System.out.println("Function evaluation: "+functions.get(i).Evaluate(p));
			m.put(functions.get(i), functions.get(i).Evaluate(p));
		}
		return m;
	}

	/**
	 * Get the nearest plan to the provided one
	 * @param pivot The pivotal plan
	 * @param j How many plans should be produced
	 * @return The nearest to pivot plan
	 * @throws Exception
	 */
	public Plan NearestPlan(Plan pivot, int j) throws Exception {
		List<Plan> pop = new ArrayList<Plan>();
		for (int i = 0; i < j; i++) {
			ProducePlan();
			Plan p = new Plan();
			p.ClonePlan(min_plan);
			pop.add(p);
		}

		long mindistance = 0;
		int nearest = 0;
		for (int n = 0; n < pop.size(); n++) {
			long curdist = distance(pivot, pop.get(n));
			if (n == 0 || mindistance > curdist) {
				nearest = n;
				mindistance = curdist;
			}
		}

		min_plan.ClonePlan(pop.get(nearest));

		return min_plan;
	}

	
	private long distance(Plan a, Plan b) {

		long distance = 0;
		for (Iterator<Entry<VMTemplate, HostInfo>> it = a.match.entrySet()
				.iterator(); it.hasNext();) {
			Entry<VMTemplate, HostInfo> ele = it.next();
			VMTemplate vm = ele.getKey();
			HostInfo bhost = b.match.get(vm);
			if (!bhost.name.equalsIgnoreCase(ele.getValue().name)) {
				distance += vm.Mem + vm.HD;
			}
		}
		return distance;
	}

	public Plan PlanDeployment() throws Exception {
		//if (!ready) {
			if (!Schedule())
				return null;
		//}
		return min_plan;
	}
}
