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
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.annotation.security.RunAs;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.Pool;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.defaults.PoolDefaults;
import org.uoa.eolus.EolusLocal;
import org.uoa.nefeli.cloud.EolusConnector;
import org.uoa.nefeli.cloud.Infrastructure;
import org.uoa.nefeli.costfunctions.CostFunction;
import org.uoa.nefeli.costfunctions.EmptyHostNode;
import org.uoa.nefeli.costfunctions.EvenHostLoad;
import org.uoa.nefeli.costfunctions.FavourVM;
import org.uoa.nefeli.costfunctions.MinTrafic;
import org.uoa.nefeli.costfunctions.ParallelNodes;
import org.uoa.nefeli.costfunctions.PinVM;
import org.uoa.nefeli.parser.Machine;
import org.uoa.nefeli.parser.Profile;
import org.uoa.nefeli.parser.Transition;
import org.uoa.nefeli.parser.WorkloadDesc;
import org.uoa.nefeli.parser.constraints.Constraint;
import org.uoa.nefeli.parser.constraints.EmptyHostNodeWish;
import org.uoa.nefeli.parser.constraints.EvenHostNodeWish;
import org.uoa.nefeli.parser.constraints.FavourVMWish;
import org.uoa.nefeli.parser.constraints.MinTraficWish;
import org.uoa.nefeli.parser.constraints.ParallelPCsWish;
import org.uoa.nefeli.parser.constraints.PinVMWish;
import org.uoa.nefeli.parser.events.AndEvents;
import org.uoa.nefeli.parser.events.Event;
import org.uoa.nefeli.parser.events.InvertEvent;
import org.uoa.nefeli.parser.events.MagicWordEvent;
import org.uoa.nefeli.parser.events.OrEvents;
import org.uoa.nefeli.traps.AndTraps;
import org.uoa.nefeli.traps.InvertTrap;
import org.uoa.nefeli.traps.MagicWordTrap;
import org.uoa.nefeli.traps.OrTraps;
import org.uoa.nefeli.traps.Trap;
import org.uoa.nefeli.utils.HostInfo;
import org.uoa.nefeli.utils.VMTemplate;

/**
 * Message-Driven Bean implementation class for: Deployer
 *
 */
/*I need a singleton message queue. I hope this works*/
@SecurityDomain(value = "JBossWS")
@MessageDriven(
	activationConfig = { 
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName= "maxSession", propertyValue= "1")
})
@Pool(value=PoolDefaults.POOL_IMPLEMENTATION_STRICTMAX,maxSize=1,timeout=1800000) 
@RunAs("admin")
public class Deployer implements MessageListener {

	@PersistenceContext
	EntityManager em;

	@EJB
	private EolusLocal eolus;
	
	@Resource
	TimerService timerService;
	
	private List<WorkloadDesc> workloads = new ArrayList<WorkloadDesc>();
	private SAPlanner planner = null;
	private Infrastructure infra = null;
    /**
     * Default constructor. 
     */
    public Deployer() {
        // TODO Auto-generated constructor stub
    }
	
	public void update(TaskflowEntry task) {
		System.out.println("Event recieved");
		boolean redeploy = false;
		if (!getInfrastructure().Ready()) {
			System.out.println("Infra not ready.");
			timerService.createTimer(60*1000, task);
			return;
		}

		if (task.getStatus().equalsIgnoreCase("submitted")) {
			LoadWorkloads();
			redeploy = true;
		}

		if (task.getStatus().equalsIgnoreCase("finalize")) {
			UndeployVMs(task);
			//Wait for 5 minutes at most.
			waitForInfra(5);
			//prepare for re-deployment of the rest VMs
			LoadWorkloads();
			redeploy = true;
		}

		if (task.getStatus().equalsIgnoreCase("stimulated")){
			WorkloadDesc w = task.BuildWorkloadObj();
			if (Redeploy(w, task)){
				redeploy = true;
			}
			
//			Iterator<WorkloadDesc> w_iter = workloads.iterator();
//			while (w_iter.hasNext()) {
//				WorkloadDesc w = w_iter.next();
//			}
		}
		
		if (redeploy) {
			// Get VMs and constraints
			List<VMTemplate> vms = new ArrayList<VMTemplate>();
			List<String> vmIDs = new ArrayList<String>();
			List<CostFunction> cfs = new ArrayList<CostFunction>();

			Iterator<WorkloadDesc> workload_iter = workloads.iterator();
			while (workload_iter.hasNext()) {
				WorkloadDesc w = workload_iter.next();
				getVMInfo(vms, vmIDs, w);
				Map<Integer, CostFunction> cfsmap = GetConstraints(vms, w);
				GetWeights(cfs, task, cfsmap);
			}

			PrintInfo(vms, cfs);
			
			// Produce new Profile
			Plan p = null;
			try {
				getPlanner().Reset();
				getPlanner().AddCostFunctions(cfs);
				getPlanner().AddVMs(vms);
				p = getPlanner().PlanDeployment();
				Deploy(p);
			} catch (Exception e) {
				// TODO handle exceptions
				e.printStackTrace();
			}

			System.out.println("Produced Plan");
			p.Print();
			setCurrent_plan(p);

		}

		if (task.getStatus().equalsIgnoreCase("submitted")){
			task.setStatus("running");
		}
		if (task.getStatus().equalsIgnoreCase("finalize")) {
			task.setStatus("terminated");
			Iterator<Signal> it = task.getKeyMap().iterator();
			while (it.hasNext()) {
				Signal signal = (Signal) it.next();
				em.remove(signal);
			}
			task.getKeyMap().clear();
		}
		if (task.getStatus().equalsIgnoreCase("stimulated")){
			task.setStatus("running");
		}
		em.persist(task);

	}

	private void waitForInfra(int minutes) {
		boolean ready = false;
		int lookups = 0;
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ready = getInfrastructure().Ready();
		while (!ready){
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Infra not ready.");
			ready = getInfrastructure().Ready();
			lookups++;
			if (lookups > minutes){
				break;
			}
		}
	}


	private void UndeployVMs(TaskflowEntry task) {
		WorkloadDesc w = task.BuildWorkloadObj();
		Iterator<Machine> it = w.getMachines().iterator();
		while (it.hasNext()) {
			Machine m = it.next();
			VMTemplate vm = new VMTemplate(w.getUser(),
					m.getName(), m.getCores(), 
					m.getRAM(), m.getTemplate(), m.getNetwork());
			System.out.println("Killing vm: " +vm.Name+" of user "+vm.User);
			try {
				if (!getInfrastructure().Kill(vm))
					System.out.println("VM termination failed: " +vm.Name);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private List<Trap> LoadTrapsWorkload(TaskflowEntry task) {
		WorkloadDesc workload = task.BuildWorkloadObj();
		Iterator<Event> eit = workload.getEvents().iterator();
		List<Trap> traps = new ArrayList<Trap>();
		while (eit.hasNext()) {
			Event e = eit.next();
			if (e instanceof MagicWordEvent) {
				MagicWordEvent me = (MagicWordEvent) e;
				MagicWordTrap mwt = new MagicWordTrap(me.getPort(), me.getKeyWord(),task);
				mwt.ID = me.getID();
				traps.add(mwt);
			}
			if (e instanceof InvertEvent) {
				InvertEvent ie = (InvertEvent) e;
				Trap tr = traps.get(ie.getEventToInvert());
				InvertTrap invt = new InvertTrap(tr);
				invt.ID = ie.getID();
				traps.add(invt);
			}
			if (e instanceof OrEvents) {
				OrEvents oe = (OrEvents) e;
				List<Trap> ortraps = new ArrayList<Trap>();
				for (int i = 0; i < oe.getEvents().size(); i++) {
					boolean found = false;
					for (int j = 0; j < traps.size(); j++){
						if (traps.get(j).ID == oe.getEvents().get(i)){
							ortraps.add(traps.get(j));
							found = true;
							break;
						}
					}
					if (!found)
						System.out.println("Trap with id not found "+traps.get(i).ID);
				}
				OrTraps ot = new OrTraps(ortraps);
				ot.ID = oe.getID();
				traps.add(ot);
			}
			if (e instanceof AndEvents) {
				AndEvents ae = (AndEvents) e;
				List<Trap> andtraps = new ArrayList<Trap>();
				for (int i = 0; i < ae.getEvents().size(); i++) {
					boolean found = false;
					for (int j = 0; j < traps.size(); j++){
						if (traps.get(j).ID == ae.getEvents().get(i)){
							andtraps.add(traps.get(j));
							found = true;
							break;
						}
					}
					if (!found)
						System.out.println("Trap with id not found "+traps.get(i).ID);
				}
				AndTraps at = new AndTraps(andtraps);
				at.ID = ae.getID();
				traps.add(at);
			}
		}
		return traps;
	}

	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {

    	ObjectMessage msg = (ObjectMessage)message;
    	Long ID = null;
    	try {
			ID = (Long)msg.getObject();
			Thread.sleep(3000);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TaskflowEntry task = em.find(TaskflowEntry.class, ID);
		System.out.println("Taskflow "+task.getId()+ " has status "+task.getStatus());
		
		if (task == null){
			System.out.println("Could not get the task entry.");
			return;
		}
		
		update(task);
		
    }

	private void Submit(TaskflowEntry task) throws Exception{
		Context initial = new InitialContext();
		ConnectionFactory cf = (ConnectionFactory)initial.lookup("ConnectionFactory");
		Destination notify = (Destination)initial.lookup("queue/NefeliQueue");
		Connection connection = cf.createConnection();
		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(notify);
		ObjectMessage message = session.createObjectMessage();
		message.setObject(task.getId());
		producer.send(message);	
	}

	public Plan getCurrent_plan() {
		List<CurrentPlan> plans_list = em.createQuery(
			"select p from CurrentPlan p").getResultList();
		if (plans_list.size() <= 0)
			return null;
		else if (plans_list.size() > 1){
			System.out.println("We have two current plans!");
		}
		return plans_list.get(0).BuildWorkloadObj();
	}

	private void setCurrent_plan(Plan p) {
		List<CurrentPlan> plans_list = em.createQuery(
		"select p from CurrentPlan p").getResultList();
		if (plans_list.size() <= 0){
			CurrentPlan cp = new CurrentPlan();
			cp.LoadPlan(p);
			em.persist(cp);
			return;
		}else if (plans_list.size() > 1){
			System.out.println("We have two current plans!");
		}
		CurrentPlan cp = plans_list.get(0);
		cp.LoadPlan(p);
		em.persist(cp);
	}

	
	private Planner getPlanner(){
		if (planner == null){
			Infrastructure infra = getInfrastructure();
			planner = new SAPlanner(infra, 1000, 300, 30);
		}
		return planner;
	}

	private Infrastructure getInfrastructure() {
		if (infra == null){
			infra = new EolusConnector(eolus, this);
		}
		return infra ;
	}
	
	@Timeout
	public void DelayTask(Timer timer) {
	    TaskflowEntry task = (TaskflowEntry)timer.getInfo();
	    try {
			Submit(task);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	private void LoadWorkloads() {
		workloads.clear();
		List<TaskflowEntry> alltasks = em.createQuery(
			"select ts from TaskflowEntry ts where ts.status<>'terminated'").getResultList();
		Iterator<TaskflowEntry> tasks_it = alltasks.iterator();
		while (tasks_it.hasNext()) {
			TaskflowEntry task = (TaskflowEntry) tasks_it.next();
			if (!task.getStatus().equalsIgnoreCase("finalize")){
				workloads.add(task.BuildWorkloadObj());
			}
		}
	}

	private void getVMInfo(List<VMTemplate> vms, List<String> vmIDs,
			WorkloadDesc w) {
		Iterator<Machine> it = w.getMachines().iterator();
		while (it.hasNext()) {
			Machine m = it.next();
			if (!vmIDs.contains(m.getName())) {
				VMTemplate vm = new VMTemplate(w.getUser(),
						m.getName(), m.getCores(), 
						m.getRAM(), m.getTemplate(), m.getNetwork());
				System.out.println("User "+vm.User+ " Name "+ vm.Name);
				vms.add(vm);
				vmIDs.add(m.getName());
			}
		}
	}

	private Map<Integer, CostFunction> GetConstraints(List<VMTemplate> vms,
			WorkloadDesc w) {
		Map<Integer, CostFunction> cfsmap = new HashMap<Integer, CostFunction>();
		cfsmap.clear();
		Iterator<Constraint> wit = w.getConstraints().iterator();
		while (wit.hasNext()) {
			Constraint wish = wit.next();
			CostFunction c = null;
			if (wish instanceof ParallelPCsWish) {
				ParallelPCsWish p = (ParallelPCsWish) wish;
				List<VMTemplate> pvms = new ArrayList<VMTemplate>();
				Iterator<String> pvmids = p.getPCs().iterator();
				while (pvmids.hasNext()) {
					String id = pvmids.next();
					VMTemplate t = getVMfromID(vms, id);
					pvms.add(t);
				}
				c = new ParallelNodes(pvms);
			}
			if (wish instanceof MinTraficWish) {
				MinTraficWish m = (MinTraficWish) wish;
				Map<String, List<String>> edges = m.getEdges();
				c = new MinTrafic(edges);
			}
			if (wish instanceof FavourVMWish) {
				FavourVMWish f = (FavourVMWish) wish;
				c = new FavourVM(f.getPC());
			}
			if (wish instanceof PinVMWish) {
				PinVMWish f = (PinVMWish) wish;
				c = new PinVM(f.getPC(),this);
			}
			if (wish instanceof EvenHostNodeWish) {
				c = new EvenHostLoad();
			}
			if (wish instanceof EmptyHostNodeWish) {
				EmptyHostNodeWish e = (EmptyHostNodeWish) wish;
				c = new EmptyHostNode(e.getHost());
			}
			cfsmap.put(wish.getID(), c);
		}
		return cfsmap;
	}

	
	private VMTemplate getVMfromID(List<VMTemplate> vms, String vmID){
		for (VMTemplate t : vms){
			if (t.Name.equalsIgnoreCase(vmID))
				return t;
		}
		return null;
	}

	private void GetWeights(List<CostFunction> cfs, TaskflowEntry task,
			Map<Integer, CostFunction> cfsmap) {
		WorkloadDesc w = task.BuildWorkloadObj();
		Integer profileID = task.getCurentProfile();
		Profile profile = null;
		Iterator<Profile> p_int = w.getProfiles().iterator();
		while(p_int.hasNext()){
			Profile prof = p_int.next();
			if (prof.getID().equals(profileID)){
				profile = prof;
			}
		}
		Map<Integer, Double> m = profile.getPreferences();
		Iterator<Entry<Integer, Double>> cfsit = m.entrySet()
				.iterator();
		while (cfsit.hasNext()) {
			Entry<Integer, Double> e = cfsit.next();
			cfsmap.get(e.getKey()).weight = e.getValue();
		}

		Iterator<Integer> cm = profile.getConstrains().iterator();
		while (cm.hasNext()) {
			cfsmap.get(cm.next()).weight = 100.0;
		}

		cfs.addAll(cfsmap.values());
	}

	private void PrintInfo(List<VMTemplate> vms, List<CostFunction> cfs) {
		System.out.println("VMs: ");
		Iterator<VMTemplate> vm_it = vms.iterator();
		while (vm_it.hasNext()) {
			System.out.println(vm_it.next().Name);
		}
		System.out.println("Constraints: ");
		Iterator<CostFunction> cfs_it = cfs.iterator();
		while (cfs_it.hasNext()) {
			CostFunction c = cfs_it.next();
			System.out.println(c.weight);
		}
	}
	
	/**
	 * Deploy a specific plan to the infrastructure
	 * @param p
	 * @throws Exception
	 */
	private void Deploy(Plan p) throws Exception{
		Plan current_plan = getCurrent_plan();
		System.out.println("Deploy Plan ranked: "+p.rank);
		p.Print();
		Infrastructure infra = getInfrastructure(); 
		Iterator<Entry<VMTemplate, HostInfo>> it = p.match.entrySet().iterator();
		while(it.hasNext()){
			Entry<VMTemplate, HostInfo> element = it.next();
			HostInfo oldhost = null;
			if (current_plan != null){
				Iterator<Entry<VMTemplate, HostInfo>> i = current_plan.match.entrySet().iterator();
				while (i.hasNext()){
					Entry<VMTemplate, HostInfo> e = i.next();
					if(e.getKey().Name.equalsIgnoreCase(element.getKey().Name))
						oldhost = e.getValue();
				}
			}
			if (oldhost == null){
				infra.Spawn(element.getKey(), element.getValue());
			}else{
				infra.Migrate(element.getKey(), oldhost, element.getValue());				
			}
		}
		
		if (current_plan == null) return;
		Plan ToBeKilled = new Plan();
		ToBeKilled.ClonePlan(current_plan);
		it = p.match.entrySet().iterator();
		while(it.hasNext()){
			Entry<VMTemplate, HostInfo> element = it.next();
			Iterator<Entry<VMTemplate, HostInfo>> i = ToBeKilled.match.entrySet().iterator();
			while (i.hasNext()){
				Entry<VMTemplate, HostInfo> e = i.next();
				if(e.getKey().Name.equalsIgnoreCase(element.getKey().Name)){
					ToBeKilled.match.remove(e.getKey());
					break;
				}
			}
		}
		Iterator<Entry<VMTemplate, HostInfo>> i = ToBeKilled.match.entrySet().iterator();
		while (i.hasNext()){
			Entry<VMTemplate, HostInfo> e = i.next();
			System.out.println("VM "+e.getKey()+" must already be dead");
			//infra.Kill(e.getKey());
		}
	}

	/**
	 * Check the status of the traps and accordingly decide
	 * on the current profile
	 * @param task 
	 * @param traps The mapping of all traps along with their ID
	 * @param Transitions 
	 * @return true icase the current profile changes 
	 */
	public boolean Redeploy(WorkloadDesc w, TaskflowEntry task) {
		List<Transition> Transitions =  w.getTransitions();
		Integer CurentProfile = task.getCurentProfile();
		List<Trap> traps = LoadTrapsWorkload(task);
		//TODO: this is wrong. It does an AND over ALL traps
		if (Transitions == null)
			return false;
		for (int t = 0; t < Transitions.size(); t++) {
			if (Transitions.get(t).getFrom().equals(CurentProfile)) {
				List<Integer> events = Transitions.get(t).getEvents();
				boolean do_jump = true;
				for (int e = 0; e < events.size(); e++) {
					for (int j = 0; j < traps.size(); j++){
						if (events.get(e).equals(traps.get(j).ID)){
							Trap tr = traps.get(j);
							if (!tr.Check()){
									do_jump = false;
									continue;
							}
						}
					}
				}
				if (do_jump){
					CurentProfile = Transitions.get(t).getTo();
					System.out.println("Current profile becomes: "+CurentProfile);
					task.setCurentProfile(CurentProfile);
					return true;
				}
			}
		}
		return false;
	}

	
}
