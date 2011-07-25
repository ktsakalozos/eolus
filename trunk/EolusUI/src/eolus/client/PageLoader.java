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


import com.google.gwt.user.client.ui.RootPanel;

import eolus.client.Presenters.ClusterPresenter;
import eolus.client.Presenters.HostPresenter;
import eolus.client.Presenters.IndexPresenter;
import eolus.client.Presenters.NetPresenter;
import eolus.client.Presenters.ScriptPresenter;
import eolus.client.Presenters.TemplatePresenter;
import eolus.client.Presenters.UserPresenter;
import eolus.client.Presenters.VMPresenter;


public class PageLoader {


	private static int TAB_NUM =7; //ta tabs tou adminstrator

	public void Load(String page) {
		UnloadAll();

		if( "home".equals(page) || "".equals(page) )
		{

			IndexPresenter p = new IndexPresenter();
			p.loadindex();
		}
		else if( "VmsManagment".equals(page) )
		{
			RootPanel.get("tab2").addStyleName("selected");
			VMPresenter p = new VMPresenter();
			p.loadindex();
		}

		//* Users
		else if( "UsersManagment".equals(page) )
		{

			RootPanel.get("tab1").addStyleName("selected");
			UserPresenter p = new UserPresenter();
			p.loadindex();
		}

		else if( "ListUsers".equals(page) )
		{

			RootPanel.get("tab1").addStyleName("selected");
			UserPresenter p = new UserPresenter();
			p.loadListUsers();

		}

		else if( "AddUser".equals(page) )
		{   	  
			RootPanel.get("tab1").addStyleName("selected");
			UserPresenter p = new UserPresenter();
			p.loadCreateUser();
		}
		/*
      else if( "ModifyUser".equals(page) )
      {   	  
    	  RootPanel.get("tab1").addStyleName("selected");
          UserPresenter p = new UserPresenter();
          p.loadModifyUser(null);
      }*/
		else if( "DeleteUser".equals(page) )
		{   	  
			RootPanel.get("tab1").addStyleName("selected");
			UserPresenter p = new UserPresenter();
			p.loadDeleteUser("");
		}

		//*templates*//
		else if( "TemplatesManagment".equals(page) )
		{   	  
			RootPanel.get("tab3").addStyleName("selected");
			TemplatePresenter p = new TemplatePresenter();
			p.loadindex();
		}
		else if( "ListTemplates".equals(page) )
		{
			RootPanel.get("tab3").addStyleName("selected");
			TemplatePresenter p = new TemplatePresenter();
			p.loadListTemplates();
		}
		else if( "MyTemplates".equals(page) )
		{
			RootPanel.get("tab3").addStyleName("selected");
			TemplatePresenter p = new TemplatePresenter();
			p.loadMyTemplates();
		}
		/*
      else if( "CreateTemplate".equals(page) )
      {
    	  RootPanel.get("tab3").addStyleName("selected");
    	  TemplatePresenter p = new TemplatePresenter();
    	  p.loadCreateTemplate();
      }
		 */
		//


		else if( "VmsManagment".equals(page) )
		{
			RootPanel.get("tab2").addStyleName("selected");
			VMPresenter p = new VMPresenter();
			p.loadindex();
		}

		else if( "ListVms".equals(page) )
		{
			RootPanel.get("tab2").addStyleName("selected");
			VMPresenter p = new VMPresenter();
			p.loadListVMs();
		}
		else if( "MyVms".equals(page) )
		{
			RootPanel.get("tab2").addStyleName("selected");
			VMPresenter p = new VMPresenter();
			p.loadMyVms();
		}


		else if( "CreateVm".equals(page) )
		{
			RootPanel.get("tab2").addStyleName("selected");
			VMPresenter p = new VMPresenter();
			p.loadCreateVM();
		}

		else if( "ScriptManagment".equals(page) )
		{
			RootPanel.get("tab4").addStyleName("selected");
			ScriptPresenter p = new ScriptPresenter();
			p.loadindex();
		}
		else if( "ListScripts".equals(page) )
		{
			RootPanel.get("tab4").addStyleName("selected");
			ScriptPresenter p = new ScriptPresenter();
			p.loadListScripts();
		}
		else if( "MyScripts".equals(page) )
		{
			RootPanel.get("tab4").addStyleName("selected");
			ScriptPresenter p = new ScriptPresenter();
			p.loadMyScripts();
		}
		else if( "RunCommand".equals(page) )
		{
			RootPanel.get("tab4").addStyleName("selected");
			ScriptPresenter p = new ScriptPresenter();
			p.loadRunCommand();
		}
		else if( "UploadScript".equals(page) )
		{
			RootPanel.get("tab4").addStyleName("selected");
			ScriptPresenter p = new ScriptPresenter();
			p.loadUploadScript();
		}
		else if( "HostsManagment".equals(page) )
		{
			RootPanel.get("tab5").addStyleName("selected");
			HostPresenter p = new HostPresenter();
			p.loadindex();
		}
		else if( "CreateHost".equals(page) )
		{

			RootPanel.get("tab5").addStyleName("selected");
			HostPresenter p = new HostPresenter();
			p.loadCreateHost();

		}
		else if( "ListHosts".equals(page) )
		{
			//Window.alert("loadin!!!!");
			RootPanel.get("tab5").addStyleName("selected");
			HostPresenter p = new HostPresenter();
			p.loadListHosts();

		}
		/* car's cluster management pages */

		else if("Clusters".equals(page) )
		{
			RootPanel.get("tab6").addStyleName("selected");
			ClusterPresenter p = new ClusterPresenter();
			p.loadindex();
		}

		else if("NewCluster".equals(page) )
		{
			RootPanel.get("tab6").addStyleName("selected");
			ClusterPresenter p = new ClusterPresenter();
			p.loadNewCluster();
		}

		else if("ViewCluster".equals(page) )
		{
			RootPanel.get("tab6").addStyleName("selected");
			ClusterPresenter p = new ClusterPresenter();
			p.loadViewCluster();
		}


		/* car's network management pages */

		else if("NetManagement".equals(page) ){
			RootPanel.get("tab7").addStyleName("selected");
			NetPresenter p = new NetPresenter();
			p.loadindex();
		}

		else if("MyNetworks".equals(page) ){
			RootPanel.get("tab7").addStyleName("selected");
			NetPresenter p = new NetPresenter();
			p.LoadUserNetworks();
		}
		else if("AdminNetworks".equals(page) ){
			RootPanel.get("tab7").addStyleName("selected");
			NetPresenter p = new NetPresenter();
			p.LoadAdminNetworks();
		}
		else if("CreateNet".equals(page) ){
			RootPanel.get("tab7").addStyleName("selected");
			NetPresenter p = new NetPresenter();
			p.LoadCreateNetwork();
		}

		else if("AdminNetworksByUser".equals(page)){

			RootPanel.get("tab7").addStyleName("selected");
			NetPresenter p = new NetPresenter();
			p.LoadAdminNetworksUser("");



		}

	}

	public static void UnloadAll() {



		if(MyTimer.getTimer()!=null)
			MyTimer.getTimer().cancel(); //stop loading stuff!!!


		for(int i=1 ; i <= TAB_NUM ; i++) //SIMANTIKO: to noumero auto na allaksei an allaksei o arithmos twn tab
			RootPanel.get("tab"+i).removeStyleName("selected");



		RootPanel.get("Content").clear();
		RootPanel.get("Dashboard").clear();
		RootPanel.get("IndexSidebar").clear();







	}






}
