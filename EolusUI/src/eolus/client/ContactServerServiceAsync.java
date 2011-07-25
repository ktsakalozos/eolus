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

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ContactServerServiceAsync {

	//public void dummy(InternalErrorException e,AsyncCallback<InternalErrorException> callback);
	//giati ehoume to ContactServerServiceAsync ola void?? waiii?

	void foo(AsyncCallback<String> callback);
	public void getUserVmList(String user,AsyncCallback<String[]> callback);
	public void adminGetAllVms(AsyncCallback<String[]> callback);
	//scripts
	public void userRemoveScript(String script,AsyncCallback<String> callback);
	public void adminRemoveScript(String user, String script,AsyncCallback<String> callback);
	public void adminSyncAllScripts(AsyncCallback<String> callback);
	public void adminSyncAllTemplates(AsyncCallback<String> callback);
	public void userCreateScript(String name,String content,String description,AsyncCallback<String> callback);
	public void adminCreateScript(String user,String name,String content,String description,AsyncCallback<String> callback);


	public void adminGetClusterInfo(String site,AsyncCallback<String[]> callback);
	public void adminRemoveCluster(String name,AsyncCallback<String> callback);
	public void adminRemoveHostfromCluster(String hostname,String cluster,AsyncCallback<String> callback);
	public void adminAddHostToCluster(String hostname,String cluster,AsyncCallback<String> callback);



	//hosts//
	public void getHosts(AsyncCallback<String[]> callback);
	public void adminGetHostsOfCluster(String cluster,AsyncCallback<String[]> callback);
	public void adminGetClusters(AsyncCallback<String[]> callback);
	public void adminNewCluster(String name,String[] hosts,AsyncCallback<String> callback);
	//networks//

	void removeNetwork(String network,AsyncCallback<String> callback);
	void removeNetworkUser(String network,AsyncCallback<String> callback);
	void assignNetwork(String network,String user,AsyncCallback<String> callback);
	void getNetworkInfo(String network,AsyncCallback<String> callback);
	void getNetworksbyUser(String user,AsyncCallback<String[]> callback);
	void getStrayNetworks(AsyncCallback<String[]> callback);
	void AdminCreateNetwork(String name,String type,String info,AsyncCallback<String> callback);
	void UserCreateNetwork(String name,String type,String info,AsyncCallback<String> callback);
	void adminGetAllNetworks(AsyncCallback<String[]> callback);
	public void getNetworks(AsyncCallback<String[]> callback); //user function
	//
	void getUsers(AsyncCallback<String[]> callback);
	void getUsername(AsyncCallback<String> callback); //why void?!?
	void getRemoteIp(AsyncCallback<String> callback);
	void debug(String mes, AsyncCallback<String> callback);
	void addUser(String data, AsyncCallback<String> callback);
	void getUsersList(AsyncCallback<String[][]> callback);
	void getUserInfo(String username, AsyncCallback<String[]> callback);
	void getUsernamesList(AsyncCallback<String[]> callback);

	void deleteUser(String name, AsyncCallback<String> callback);
	void logout(AsyncCallback<Boolean> callback);
	void getUserTemplates(String user, boolean sync ,AsyncCallback<String[][]> callback);
	void deleteTemplate(String user, String template, AsyncCallback<String> callback);
	void moveTemplate(String user, String tempName, String newTempName, AsyncCallback<String> callback);
	void copyTemplate(String user, String tempName, String newTempName, AsyncCallback<String> callback);
	void makeTemplatePublic(String user, String tempName, AsyncCallback<String> callback);
	void getUserVms(String user, AsyncCallback<String[][]> callback);
	void actionOnVm(String user, String vm, String action, AsyncCallback<String> callback);
	void createVm(String user, String temp, String VmName, String net,
			int mem, int cores, AsyncCallback<String> callback);
	void createTemplFromVm(String user, String Vm, AsyncCallback<String> callback);
	void getUserScripts(String user, boolean sync, AsyncCallback<String[][]> callback);
	void getScriptDescription(String user, String script, AsyncCallback<String> callback);
	void runScript(String user, String script, String vm, AsyncCallback<String> callback);
	void runCommand(String user, String vm, String command, AsyncCallback<String[]> callback);
	void syncTemplates(String user, AsyncCallback<String> callback);
	void syncScripts(String user, AsyncCallback<String> callback);

	void assignVm(String user, String vm_name, AsyncCallback<String> callback);
	void createHost(String hostName, AsyncCallback<String> callback);
	void deleteHost(String hostID, AsyncCallback<String> callback);
	void getHostsList(AsyncCallback<String[][]> callback);
	void enableHost(String hostID, boolean enable, AsyncCallback<String> callback);
	void workAround(String str, AsyncCallback callback); 


	void isAdmin(AsyncCallback<Boolean> callback);
	void getUserTemplateStatus(String user, String template, boolean sync,
			AsyncCallback<String> callback);
	void getHostNames(AsyncCallback<String[]> asyncCallback);
	void migrateVM(String vm, String migrate_to, AsyncCallback<String> asyncCallback);
}
