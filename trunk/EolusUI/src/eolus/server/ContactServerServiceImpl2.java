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

package eolus.server;

//
//  To arxeio auto uparxei gia na antikatastisei to arxeio ContactServerServiceImpl.java
//  kai na prosferei statikes plirofories sto client kommati wste na mporei na dokimastei
//  i leitourgia tou.
//

import java.util.Vector;
import org.jboss.security.SecurityAssociation;
import org.uoa.eolus.InternalErrorException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import eolus.client.ContactServerService;


public class ContactServerServiceImpl2 extends RemoteServiceServlet implements ContactServerService{

	private static final long serialVersionUID = 1L; 

	public void workAround(String str)
	{

	}

	public InternalErrorException dummy(InternalErrorException e)
	{
		return null;
	}


	public String adminRemoveHostfromCluster(String hostname,String cluster)
	{
		return "ok";
	}
	public String adminAddHostToCluster(String hostname,String cluster)
	{
		return "ok";
	}
	public String adminRemoveCluster(String name)
	{
		return "ok";
	}

	public String userCreateScript(String name,String content,String description)
	{
		return "ok";
	}
	public String adminCreateScript(String user,String name,String content,String description)
	{
		return "ok";
	}

	public String[] adminGetClusterInfo(String site)
	{

		String[] s= {"true","true"};
		return s;


	}

	public String adminSyncAllTemplates()
	{
		return "ok";
	}
	public String adminSyncAllScripts()
	{
		return "ok";
	}

	public String[] adminGetAllVms()

	{
		return new String[] {"vm1","vm2","vm3","vm4","vm5","publicvm1"};

	}

	public String[] getUserVmList(String user)
	{
		return new String[] {"vm1","vm2","vm3","vm4"};

	}

	public String userRemoveScript(String script)
	{
		return "ok";   
	}


	public String adminRemoveScript(String user,String script)
	{
		return "ok";   
	}


	public String[] getHosts()
	{
		String[] s={"Host1","Host2","Host3","Host4"};
		return s;

	}

	public String[] adminGetHostsOfCluster(String cluster)
	{
		String[] s={"Host1","Host2","Host3","Host4"};
		return s;

	}
	public String adminNewCluster(String name,String[] hosts)
	{
		return "ok";
	}
	public String[] adminGetClusters()
	{
		String[] s ={"cluster1","cluster2"};
		return s;
	}


	public String[] adminGetAllNetworks()
	{   
		String[] s = {"net1","neat2","lalala_net","1","2","3","4","5","6","7","8","9"};
		return s;
	}
	public String getNetworkInfo(String network){

		if(network.contains("2")){

			return "<VNet><ID>2</ID><NAME>Public</NAME><IPs><IP>88.197.20.247</IP><IP>88.197.20.247</IP></IPs><STATE>READY</STATE></VNet>";


		}
		else 
			return "<VNet><ID>1</ID><NAME>Int-0</NAME><IPs><IP>192.168.0.0</IP><IP>192.168.0.1</IP><IP>192.168.0.2</IP><IP>192.168.0.3</IP><IP>192.168.0.4</IP><IP>192.168.0.5</IP><IP>192.168.0.6</IP><IP>192.168.0.7</IP><IP>192.168.0.8</IP><IP>192.168.0.9</IP><IP>192.168.0.10</IP><IP>192.168.0.11</IP><IP>192.168.0.12</IP><IP>192.168.0.13</IP><IP>192.168.0.14</IP><IP>192.168.0.15</IP><IP>192.168.0.16</IP><IP>192.168.0.17</IP><IP>192.168.0.18</IP><IP>192.168.0.19</IP><IP>192.168.0.20</IP><IP>192.168.0.21</IP><IP>192.168.0.22</IP><IP>192.168.0.23</IP><IP>192.168.0.24</IP><IP>192.168.0.25</IP><IP>192.168.0.26</IP><IP>192.168.0.27</IP><IP>192.168.0.28</IP><IP>192.168.0.29</IP><IP>192.168.0.30</IP><IP>192.168.0.31</IP><IP>192.168.0.32</IP><IP>192.168.0.33</IP><IP>192.168.0.34</IP><IP>192.168.0.35</IP><IP>192.168.0.36</IP><IP>192.168.0.37</IP><IP>192.168.0.38</IP><IP>192.168.0.39</IP><IP>192.168.0.40</IP><IP>192.168.0.41</IP><IP>192.168.0.42</IP><IP>192.168.0.43</IP><IP>192.168.0.44</IP><IP>192.168.0.45</IP><IP>192.168.0.46</IP><IP>192.168.0.47</IP><IP>192.168.0.48</IP><IP>192.168.0.49</IP><IP>192.168.0.50</IP><IP>192.168.0.51</IP><IP>192.168.0.52</IP></IPs><STATE>READY</STATE></VNet>";

	}

	public String[] getStrayNetworks(){

		String[] s={"stray1","stray2","strayyy3"};
		return s;


	}

	public String[] getNetworksbyUser(String user){

		String[] s ={"net1_"+user, "net2_"+user,"net3_"+user,"net4_"+user};
		return s;
	}
	public String assignNetwork(String network,String user){

		return "ok";

	}

	public String removeNetwork(String network){

		return "ok";
	}

	public String removeNetworkUser(String network){

		return "ok";
	}

	public String AdminCreateNetwork(String name,String type,String info){

		return "ok";

	}
	public String UserCreateNetwork(String name,String type,String info){

		return "ok";

	}

	public String foo(){

		try{
			// int a =5/0; //trollcode
			return "ok";
		}
		catch(Exception e)
		{ 	return e.toString(); 
		}
	}

	public boolean logout() {

		this.getThreadLocalRequest().getSession().invalidate();
		//      this.getThreadLocalRequest().getSession().setMaxInactiveInterval(0);
		return true;
	}

	public String getUsername() {
		try{
			return SecurityAssociation.getCallerPrincipal().getName();
		}
		catch(Exception e)
		{ return null; }
	}

	public String getRemoteIp() {
		return getThreadLocalRequest().getRemoteHost();
	}

	public String debug(String mes) {
		System.out.println(mes);
		return null;
	}

	public String addUser(String data) {
		Vector<String> v = new Vector<String>();
		v.add("fridian");
		v.add("jackal");
		v.add("public");
		if( v.contains(data) )
			return "Username \""+data+"\" already exists";
		return "ok";
	}

	public String[] getNetworks()
	{
		return new String[]{"mynet1","mynet2"};


	}

	public String[] getUsers(){

		String[] data = {"user1","user2","user3","user4","user5","user6","user7"};
		return data;


	}

	public String[][] getUsersList() {
		String[][] data = {
				{"Username","Real Name","Date Registered","Last IP","Role"},
				{"fridian","Kristian","6/3/2010","77.48.52.224","user"},
				{"jackal","Kwstas","27/2/2010","148.16.98.126","admin"},
				{"public","Nephele","01/2/2010","125.59.87.56","admin"} };
		return data;
	}

	public String[] getUserInfo(String username) {
		if( "fridian".equals(username) )
			return new String[] {"fridian","Kristian","6/3/2010","77.48.52.224","user"};
		else if( "jackal".equals(username) )
			return new String[] {"jackal","Kwstas","27/2/2010","148.16.98.126","admin"};
		else if( "public".equals(username) )
			return new String[] {"public","Nephele","01/2/2010","125.59.87.56","admin"};
		return null;
	}

	public String[] getUsernamesList() {
		return new String[] {"fridian","jackal","public"};
	}

	public String modifyUser(String user, String[] data) {   /////////////////////////////
		Vector<String> v = new Vector<String>();
		v.add("fridian");
		v.add("jackal");
		v.add("public");
		if( !data[0].equals(user) && v.contains(data[0]) )
			return "Username \""+data[0]+"\" already exists"; //????
		return "ok";
	}

	public String deleteUser(String name) {
		Vector<String> v = new Vector<String>();
		v.add("fridian");
		v.add("jackal");
		v.add("public");
		if( !v.contains(name) )
			return "No user exists with username \""+name+"\"";
		return "ok";
	}

	public String[][] getUserTemplates(String user, boolean sync) //boolean sync? :/
	{

		return new String[][] {{"temp1","active"},{"temp2","inactive"},{"temp3","paused"}};

	}

	public String deleteTemplate(String user, String template) {
		System.out.println("deleteTemplate   user=\""+user+"\"     template=\""+template+"\"");    ////////////////------
		return "ok";
	}

	public String moveTemplate(String user, String tempName, String newTempName) {
		System.out.println("moveTemplate   user=\""+user+"\"    tempName=\""+tempName+"\"     newTempName=\""+newTempName+"\"");    ////////////////------
		return "ok";
	}

	public String copyTemplate(String user, String tempName, String newTempName) {
		System.out.println("copyTemplate   user=\""+user+"\"    tempName=\""+tempName+"\"     newTempName=\""+newTempName+"\"");    ////////////////------
		return "ok";
	}

	public String makeTemplatePublic(String user, String tempName) {
		//      return "error minima_lathous";
		System.out.println("makeTemplatePublic   user=\""+user+"\"     tempName=\""+tempName+"\"");    ////////////////------
		return "ok onoma_tou_template";
	}

	public String[][] getUserVms(String user) {

		return new String[][] 
		                    { {"Name","Status","Memory","CPU","IP","Hostname","Actions"},
				getVMinfo("a"),
				getVMinfo("a"),
				getVMinfo("a"),
				getVMinfo("a"),
				getVMinfo("a") };
		//return null;
	}

	public String actionOnVm(String user, String vm, String action) {
		System.out.println("actionOnVm   user=\""+user+"\"    vm=\""+vm+"\"     action=\""+action+"\"");    ////////////////------
		return "ok";
	}

	public String createVm(String user, String template, String VmName, String net, int mem, int cores) {
		System.out.println("createVm   user=\""+user+"\"    template=\""+template+"\"    VmName=\""+VmName+"\"" +
				"    network=\""+net+"\"    mem=\""+mem+"\"    cores=\""+cores+"\"");    ////////////////------
		return "ok";
	}

	public String createTemplFromVm(String user, String Vm) {
		System.out.println("createTemplFromVm   user=\""+user+"\"    Vm=\""+Vm+"\"");    ////////////////------
		return "ok";
	}

	public String[][] getUserScripts(String user, boolean sync) {

		String[][] s ={{"Name","Description","Actions"},
				{"scirpt1","description 1"},
				{"scirpt2","description 2" }
		};

		return s;
	}

	public String runScript(String user, String script, String vm) {
		System.out.println("runScript   user=\""+user+"\"    script=\""+script+"\"     vm=\""+vm+"\"");    ////////////////------
		return "ok";
	}

	public String[] runCommand(String user, String vm, String command) {
		System.out.println("runCommand    user=\""+user+"\"    vm=\""+vm+"\"     command=\""+command+"\"");    ////////////////------
		String[] reply = {"bla bla kai ena list kai kai kaeotkijei tjis fashfou ahsfoi hasfoij sfijisajfos auisah ufisaefahoufvsui divcn udv usdv udsvuohs uodvhnsdivhsdivsd vsadivhisah visdhv is",
				"total 2154\n" +
				"drwx------  3 std05242 undergr    1024 Feb 17 18:28 Desktop/\n" +
				"drwxr-xr-x  2 std05242 undergr     512 May 30  2007 Documents/\n" +
				"drwx------  2 std05242 undergr     512 Mar 28  2006 Mail/\n" +
				"-rw-------  1 std05242   62011     600 Mar 31  2006 PUTTY.RND\n" +
				"drwxr-xr-x  2 std05242 undergr     512 Feb 28  2008 Templates/\n" +
				"drwx------  3 std05242   62011     512 Apr  3  2006 WINDOWS/\n" +
				"drwx------  2 std05242 undergr     512 Apr 20  2008 apestalmena/\n"+
				"-rw-r--r--  1 std05242 undergr 2170880 Feb  4  2009 christian@christian-desktop\n" +
				"drwx------ 13 std05242 undergr     512 May 18  2009 ergasies/\n" +
				"-rw-r--r--  1 std05242 undergr       0 Apr 15  2009 finger\n" +
				"drwx------  2 std05242 undergr     512 Apr 14  2006 hw3/\n" +
				"drwx------  2 std05242 undergr     512 Jul 14  2009 mail/\n" +
				"-rw-------  1 std05242 undergr    9249 Apr  3  2006 mbox\n" +
				"-rw-r--r--  1 std05242 undergr      75 Nov  7  2008 ntoles\n" +
				"drwx-----x 10 std05242 undergr    2560 Apr 12 18:46 public_html/\n" +
				"drwxr-xr-x  3 std05242 undergr     512 Apr  7  2008 workspace/\n" +
		"-rw-r--r--  1 std05242 undergr     296 May 24  2009 xrwmata.c\n"};
		return reply;
	}

	public String getScriptDescription(String user, String script) {
		return "blahblah";
		//      return "minima lathous";
	}

	public String syncTemplates(String user) {
		System.out.println("syncTemplates   user=\""+user+"\"");    ////////////////------
		return "ok";
	}

	public String syncScripts(String user) {
		System.out.println("syncScripts   user=\""+user+"\"");    ////////////////------
		return "ok";
	}



	public String assignVm(String user, String vmName) {
		System.out.println("assignVm   user=\""+user+"\"   vmName=\""+vmName+"\"");    ////////////////------
		return "ok";
	}

	public String createHost(String hostName) {
		System.out.println("createHost   hostName=\""+hostName+"\"");    ////////////////------
		return "ok";
	}

	public String deleteHost(String hostID) {
		System.out.println("deleteHost   hostID=\""+hostID+"\"");    ////////////////------
		return "ok";
	}

	public String[] getHostInfo(String host)
	{
		String xml= "<HOST><ID>6</ID><NAME>n30.di.uoa.gr</NAME><TOTALCPU>1600" +
		"</TOTALCPU><FREE_CPU>1599</FREE_CPU><TOTALMEMORY>50321408</TOTALMEMORY>" +
		"<FREE_MEM>6361088</FREE_MEM><RUNNING_VMS>0</RUNNING_VMS><STATE>MONITORED</STATE></HOST>";

		try
		{

			String[] s=  Parser.parseHostInfo(xml);
			// System.out.println("Naem:"+s[0]);
			return s;
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null;

		}


	}

	public String[][] getVmInfo(String host) //deprecated

	{
		return null;
	}

	public String[] getVMinfo(String host)
	{
		String xml=" <VM><ID>32</ID><NAME>renaGW</NAME><IPs>"+
		"<IP>88.197.20.247</IP></IPs><MEMORY>524140</MEMORY><CPU>0</CPU><HOSTNAME>n33.di.uoa.gr</HOSTNAME>"+
		"<STATE>RUNNING</STATE></VM>";

		try
		{

			String[] s=  Parser.parseVMInfo(xml);
			// System.out.println("Naem:"+s[0]);
			return s;
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null;

		}


	}

	public String[][] getHostsList() {
		System.out.println("getHostsList"); 
		String[][] s ={{"Name","Running VMs","Total CPU","Free CPU","Total Memory","Free Memory","Status"},

				getHostInfo("a") ,
				getHostInfo("b"),
				getHostInfo("a") 



		};

		return s;
	}



	public String enableHost(String hostID, boolean enable) {
		System.out.println("enableHost   hostID=\""+hostID+"\"   enable=\""+enable+"\"");    ////////////////------

		if(Math.random()>0.5)
			return "ok";
		else
			return "blabla error";
	}

	public String[][] getAllPendingJobs() {
		System.out.println("getAllPendingJobs");    ////////////////------
		String[][] data = 
		{ {"Create VM","admin", "simera"},
				{"Transfer Template","fridian", "xtes"},
				{"Some action","fridian", "simera"},
				{"Create VM","jackal", "twra"},
				{"Some Loooong action","admin", "aurio"} };

		return data;
		//      return null;
	}

	public String createPendingJob(String Description) {
		System.out.println("createPendingJob   Description=\""+Description+"\"");    ////////////////------
		return "ok";
	}

	public boolean isAdmin() {
		// TODO Auto-generated method stub
		return true;
	}

	public String getUserTemplateStatus(String user, String template, boolean sync) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getHostNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String migrateVM(String vm, String migrate_to) {
		// TODO Auto-generated method stub
		return null;
	}

}
