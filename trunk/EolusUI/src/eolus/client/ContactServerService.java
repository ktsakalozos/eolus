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

package eolus.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("ContactServer") //the servlet!
public interface ContactServerService extends RemoteService{
	String getUsername() throws Exception;
	public String[] getUserVmList(String user)  throws Exception;
	public String[] adminGetAllVms() throws Exception;

	//public InternalErrorException dummy(InternalErrorException e);

	//scripts//
	public String userRemoveScript(String script) throws Exception;
	public String adminRemoveScript(String user, String script) throws Exception;
	public String adminSyncAllScripts() throws Exception;
	public String adminSyncAllTemplates() throws Exception;
	public String userCreateScript(String name,String content,String description) throws Exception;
	public String adminCreateScript(String user,String name,String content,String description) throws Exception;

	//networks
	public String removeNetwork(String network) throws Exception;
	public String removeNetworkUser(String network) throws Exception;
	public String[] getNetworks() throws Exception;
	public String assignNetwork(String network,String user) throws Exception;

	public String[] adminGetClusters() throws Exception;
	public String getNetworkInfo(String network) throws Exception;
	public String[] getNetworksbyUser(String user) throws Exception;
	public String[] getStrayNetworks() throws Exception;
	public String AdminCreateNetwork(String name,String type,String info) throws Exception;
	public String UserCreateNetwork(String name,String type,String info) throws Exception;
	public String[] adminGetAllNetworks() throws Exception;
	//

	//clusters//
	public String[] adminGetClusterInfo(String site) throws Exception;
	public String[] adminGetHostsOfCluster(String cluster) throws Exception;
	public String adminNewCluster(String name,String[] hosts) throws Exception;
	public String adminRemoveHostfromCluster(String hostname,String cluster) throws Exception;
	public String adminAddHostToCluster(String hostname,String cluster) throws Exception;
	public String[] getHosts() throws Exception;
	public String adminRemoveCluster(String name) throws Exception;
	//
	boolean isAdmin() throws Exception;
	String[] getUsers() throws Exception;
	String getRemoteIp() throws Exception;
	String debug(String mes) throws Exception;
	String addUser(String data) throws Exception;
	String[][] getUsersList() throws Exception;
	String[] getUserInfo(String username) throws Exception;
	String[] getUsernamesList() throws Exception;

	String deleteUser(String name) throws Exception;
	String foo(); //:):):)
	boolean logout() throws Exception;
	String[][] getUserTemplates(String user, boolean sync) throws Exception;
	String deleteTemplate(String user, String template) throws Exception;
	String moveTemplate(String user, String tempName, String newTempName) throws Exception;
	String copyTemplate(String user, String tempName, String newTempName) throws Exception;
	String makeTemplatePublic(String user, String tempName) throws Exception;
	String[][] getUserVms(String user) throws Exception;
	String actionOnVm(String user, String vm, String action) throws Exception;
	String createVm(String user, String temp, String VmName, String net, int mem, int cores) throws Exception;
	String createTemplFromVm(String user, String Vm) throws Exception;
	String[][] getUserScripts(String user, boolean sync) throws Exception;
	String getScriptDescription(String user, String script) throws Exception;
	String runScript(String user, String script, String vm ) throws Exception;
	String[] runCommand(String user, String vm, String command) throws Exception;
	String syncTemplates(String user) throws Exception;
	String syncScripts(String user) throws Exception;
	void workAround(String str); 


	String assignVm(String user, String vm_name) throws Exception;
	String createHost(String hostName) throws Exception;
	String deleteHost(String hostID) throws Exception;
	String[][] getHostsList() throws Exception;
	String enableHost(String hostID, boolean enable)  throws Exception;

	String getUserTemplateStatus(String user, String template, boolean sync) throws Exception;
	String[] getHostNames() throws Exception;
	String migrateVM(String vm, String migrate_to) throws Exception;
}
