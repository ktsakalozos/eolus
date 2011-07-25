package eolus.client.Presenters;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;

import com.google.gwt.user.client.ui.VerticalPanel;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;

import com.google.gwt.user.client.ui.Label;

import com.google.gwt.user.client.ui.TextBox;


import eolus.client.FunctionObject;
import eolus.client.UI_Elements.HTMLwriter;
import eolus.client.UI_Elements.LightBox;
import eolus.client.UI_Elements.MyDropDown;
import eolus.client.UI_Elements.MyTable;



public class VMPresenter extends Presenter{

	String[] UserList;


	public void loadMyVms()
	{	
		loadpage("View My Virtual Machines");

		loadUser(user.getUsername());


		setRefreshTask(new FunctionObject() /* periodically refresh user data */
		{	
			public void function()
			{
				loadUser(user.getUsername());
			}
		});		

	}


	public void loadListVMs()
	{		
		loadpage("View Virtual Machines");
		final AsyncCallback<String[]> callback = new AsyncCallback<String[]>()
		{

			public void onFailure(Throwable caught) 
			{
				caught.getMessage();
				view.clear();
				HTML msg = HTMLwriter.getSystemMessage("red", "Error contacting server."/*+caught.getMessage()*/);
				view.add(msg);

			}

			public void onSuccess(final String[] users) 
			{

				UserList= users;
				loadpagination(users.length); 
				loadUserTables();

			}	

		};


		setRefreshTask(new FunctionObject()
		{

			public void function()
			{
				serverCall.getUsers(callback); 	
			}

		});

	}

	public void executeAction(String action,String vm)
	{

		final LightBox  glass = new LightBox();
		final AsyncCallback<String> callback = new AsyncCallback<String>()
		{

			public void onFailure(Throwable caught) 
			{
				caught.getMessage();
				HTML msg =HTMLwriter.getSystemMessage("red", "Error contacting server."+caught.getMessage());
				glass.add(msg);
				glass.show();

			}

			public void onSuccess(final String result) 
			{
				if(result.equalsIgnoreCase("ok"))
				{
					doRefresh(); /*silently refresh page to show changes */
				}
				else
				{
					HTML msg = HTMLwriter.getSystemMessage("red", "Action failed: "+result);
					glass.add(msg);
					glass.show();
				}

			}	

		};
		serverCall.actionOnVm(user.getUsername(), vm, action, callback);

	}


	public void loadUser(final String userdata) //load a user's vms 
	{

		final VerticalPanel content = (VerticalPanel) this.getContentWidgetNoClear(userdata,userdata+"'s Virtual Machines");		

		final MyTable table;

		if(content.getWidgetCount() ==0)
		{

			table= new MyTable();
			content.add(table);
		}
		else
		{

			table = (MyTable) content.getWidget(content.getWidgetCount()-1);

		}
		//ACTIONS: kill,pause, RESUME,INFO, CREATE TEMPLATE FROM VM, RE/ASSIGN VM TO USR, MIGRATE, POWEROF

		serverCall.getUserVms(userdata, new AsyncCallback<String[][]>() 
				{

			public void onFailure(Throwable caught) 
			{
				contactError(caught);
			}

			public void onSuccess(String[][] result) 
			{


				int i=1, size=result.length;

				while(i<size)
				{

					loadVM(userdata,table,result[i],i+1,result[0]);
					i++;
				}
				if(1==size)
				{

					table.getTable().setWidget(0,0,new Label("This user has no virtual machines."));
					while(table.getTable().getCellCount(0)>1)
					{
						table.getTable().removeCell(0, 1);
					}
					table.unsetHeaders();
				}
				else
				{
					table.setHeaders(result[0]);
				}
				table.removeExtra(size);
				MyTable.styleRows(1,result[0].length,table.getTable());
				//style rows
				//kill extra rows

			}
				});



	}

	public void loadVM(final String username,MyTable table,final String[] vmdata,final int row,String[] headers)
	{
		table.setHeaders(headers);
		final FlexTable ft = table.getTable();
		ft.getRowFormatter().setVisible(row,true);
		int i=0,size=vmdata.length;
		while(i<size)
		{

			ft.setWidget(row,i,new Label(vmdata[i]));
			i++;
		}
		FlowPanel actions =  new FlowPanel();
		table.getTable().setWidget(row,size++,actions);

		Button template = new Button("&nbsp;&nbsp;&nbsp;Create Template from VM");
		template.setStyleName("btn template");
		actions.add(template);
		template.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				createTemplate(username,vmdata[0]);
			}


		});

		//migrate
		if(user.isAdmin())
		{
			Button migrate = new Button("&nbsp;&nbsp;&nbsp;Migrate VM");
			migrate.setStyleName("btn migrate");
			actions.add(migrate);
			migrate.addClickHandler(new ClickHandler()
			{

				public void onClick(ClickEvent event)
				{
					//String user=vmdata
					//ft.removeRow(row);
					loadmigrate(vmdata[0]);
				}


			});

		}
		// pause//



		Button pause = new Button("&nbsp;&nbsp;&nbsp;Pause");
		pause.setStyleName("btn pause");
		actions.add(pause);
		pause.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				String action="pause";
				//ft.removeRow(row);
				executeAction(action, vmdata[0]);
			}


		});


		//resume//
		Button resume = new Button("&nbsp;&nbsp;&nbsp;Resume");
		resume.setStyleName("btn run");
		actions.add(resume);
		resume.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				String action="resume";
				executeAction(action, vmdata[0]);
				ft.getRowFormatter().setVisible(row, false);
			}


		});

		//poweroff//
		Button off = new Button("&nbsp;&nbsp;&nbsp;Power Off");
		actions.add(off);
		off.setStyleName("btn off");
		resume.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				String action="poweroff";
				executeAction(action, vmdata[0]);
				ft.getRowFormatter().setVisible(row, false);
			}


		});

		//kill//

		Button del = new Button("&nbsp;&nbsp;&nbsp;Kill");
		del.setStyleName("btn remove");
		actions.add(del);
		del.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				String action="kill";
				executeAction(action, vmdata[0]);

			}


		});

		//assign to user//
		if(user.isAdmin())
		{
			final Button assign = new Button("&nbsp;&nbsp;&nbsp;Assign to User");
			actions.add(assign);
			assign.setStyleName("btn assign");
			assign.addClickHandler(new ClickHandler()
			{

				public void onClick(ClickEvent event)
				{


					assign(vmdata[0]);
				}


			});

		}
	}





	public void assign(final String vmname)
	{

		final LightBox  glass = new LightBox(); 

		final VerticalPanel v = new VerticalPanel();

		final MyDropDown menu= new MyDropDown(UserList,"Select user to assign Virtual Machine "+vmname,"Assign");

		v.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		v.add(menu);		   
		glass.add(v);
		Button submit = menu.getButton();


		submit.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{

				ListBox lb = menu.getList();
				if(lb.getSelectedIndex() ==-1)
					return;

				final String usr = lb.getItemText(lb.getSelectedIndex());
				AsyncCallback<String> callback = new AsyncCallback<String>() 
				{
					public void onFailure(Throwable caught) 
					{
						v.clear();
						HTML msg = HTMLwriter.getSystemMessage("red", "Problem performing this action: "+caught.getMessage());
						v.add(msg);

					}

					public void onSuccess(String result) 
					{
						v.clear();
						HTML msg;
						if(result.equalsIgnoreCase("ok"))
						{
							msg = HTMLwriter.getSystemMessage("green", "Virtual machine successfully assigned");
							glass.autoclose(); 

							doRefresh();
						}
						else
						{
							msg = HTMLwriter.getSystemMessage("red", "Problem perfoming this action "+result);
						}
						v.add(msg);


					}

				};

				serverCall.assignVm(usr, vmname, callback);
			}
		});
		glass.show();
	}

	public void migrate(final String vmname, String[] hosts)
	{

		final LightBox  glass = new LightBox(); 

		final VerticalPanel v = new VerticalPanel();

		final MyDropDown menu= new MyDropDown(hosts,"Select host to migrate Virtual Machine to: "+vmname,"Migrate");

		v.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		v.add(menu);		   
		glass.add(v);
		Button submit = menu.getButton();

		submit.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{


				ListBox lb = menu.getList();
				if(lb.getSelectedIndex() ==-1)
					return;

				final String host = lb.getItemText(lb.getSelectedIndex());
				AsyncCallback<String> callback = new AsyncCallback<String>() 
				{
					public void onFailure(Throwable caught) 
					{
						v.clear();
						HTML msg = HTMLwriter.getSystemMessage("red", "Problem performing this action: "+caught.getMessage());
						v.add(msg);

					}

					public void onSuccess(String result) 
					{
						v.clear();
						HTML msg;
						if(result.equalsIgnoreCase("ok"))
						{
							msg = HTMLwriter.getSystemMessage("green", "Virtual machine successfully migrated");
							glass.autoclose(); 
							doRefresh();
						}
						else
						{
							msg = HTMLwriter.getSystemMessage("red", "Problem perfoming this action "+result);
						}
						v.add(msg);


					}

				};

				serverCall.migrateVM(vmname,host, callback);
			}
		});


		glass.show(); 
	}


	public void createTemplate(String usr, String vm)
	{
		final LightBox  glass = new LightBox();
		AsyncCallback<String> callback = new AsyncCallback<String>() 
		{
			public void onFailure(Throwable caught) 
			{

				HTML msg = HTMLwriter.getSystemMessage("red", "Problem performing this action: "+caught.getMessage());
				glass.add(msg);
				glass.show();

			}

			public void onSuccess(String result) 
			{

				HTML msg;
				if(result.equalsIgnoreCase("ok"))
				{

					msg = HTMLwriter.getSystemMessage("green", "Template from virtual machine successfully created.");
					glass.add(msg);
					glass.show();
					glass.autoclose(); 
					doRefresh();
				}
				else
				{
					msg = HTMLwriter.getSystemMessage("red", "Problem perfoming this action "+result);
					glass.add(msg);
					glass.show();
				}


			}

		};

		serverCall.createTemplFromVm(usr, vm, callback);



	}


	public void loadmigrate(final String vmname)
	{
		final LightBox  glass = new LightBox();
		AsyncCallback<String[]> callback = new AsyncCallback<String[]>() 
		{
			public void onFailure(Throwable caught) 
			{

				HTML msg = HTMLwriter.getSystemMessage("red", "Problem performing this action: "+caught.getMessage());
				glass.add(msg);
				glass.show();

			}

			public void onSuccess(String[] result) 
			{

				HTML msg;
				if(result!=null)
				{
					migrate(vmname,result);
				}
				else
				{
					msg = HTMLwriter.getSystemMessage("red", "Problem perfoming this action "+result);
					glass.add(msg);
					glass.show();
				}


			}

		};

		serverCall.getHosts(callback);



	}
	public void reload()  //to be used for pagination
	{
		loadpagination(UserList.length);
		hideContent();
		loadUserTables();


	}

	public void loadUserTables()
	{
		int i=(current_page-1) * page_size /*+1*/ ,size=UserList.length;
		int itemsadded=0;

		while(i<size)
		{

			loadUser(UserList[i]);
			i++;
			itemsadded++;
			if(itemsadded==page_size)
				return;
		}


	}

	public void init()
	{
		// page_size=15; //change page size from deafault 5
		String[] s1={"ListVms","List Virtual Machines"};
		String[] s2= {"CreateVm","Create Vm from Template"};
		String[] s3= {"MyVms","My Virtual Machines"};
		admin_sidebar.add(s1);
		admin_sidebar.add(s2);		
		admin_sidebar.add(s3);	
		user_sidebar.add(s3);
		user_sidebar.add(s2);


		String[] i1={"ListVms","d4_1" ,"List Virtual Machines"};
		String[] i2=  {"CreateVm","d4_2" ,"Create Vm from Template"};
		String[] i3= {"MyVms","d4_2","My Virtual Machines"};
		admin_icons.add(i1);
		admin_icons.add(i2);
		admin_icons.add(i3);
		user_icons.add(i3);
		user_icons.add(i2);
		category ="Virtual Machines";
	}

	public void loadCreateVM()
	{

		loadpage("Create New Virtual Machine");
		final VerticalPanel root = view; // get the panel associated with the element :)
		final VerticalPanel errors = new VerticalPanel();

		final FlexTable ft = new FlexTable();
		ft.setCellPadding(5);
		root.add(ft);
		final ListBox users = new ListBox();
		Label title1;

		title1 = new Label("Select User Who Owns the Template:");

		title1.addStyleName("form_subtitle");
		ft.setWidget(ft.getRowCount(), 0, title1);

		users.addItem("-- Select a user --");
		ft.setWidget(ft.getRowCount(), 0, users);

		Label title2 = new Label("Select Template:");
		ft.setWidget(ft.getRowCount(), 0, title2);
		title2.addStyleName("form_subtitle");

		final ListBox templs = new ListBox();
		ft.setWidget(ft.getRowCount(), 0, templs);

		templs.addItem("-- Select a user first --");


		Label title3 = new Label("Select Name for the Vm:");
		title3.addStyleName("form_subtitle");
		ft.setWidget(ft.getRowCount(), 0, title3);

		final TextBox vm_name = new TextBox();
		ft.setWidget(ft.getRowCount(), 0, vm_name);
		vm_name.setWidth("250px");

		Label title4 = new Label("Select Network:");
		title4.addStyleName("form_subtitle");
		final ListBox vnets = new ListBox();
		vnets.addItem("-- Select a Network --");

		ft.setWidget(ft.getRowCount(), 0, title4);
		ft.setWidget(ft.getRowCount(), 0, vnets);

		/*final TextBox net = new TextBox();
			ft.setWidget(ft.getRowCount(), 0, net);
			net.setWidth("250px");*/

		Label title5 = new Label("Select Memory Size:");
		title5.addStyleName("form_subtitle");
		ft.setWidget(ft.getRowCount(), 0, title5);

		final ListBox memory = new ListBox();
		ft.setWidget(ft.getRowCount(), 0, memory);
		for(int i=1 ; i<=32 ; i++)
			memory.addItem((i*512)+" MB");
		memory.setSelectedIndex(0);
		memory.setVisibleItemCount(5);
		memory.setWidth("180px");

		Label title6 = new Label("Select CPU Cores:");
		title6.addStyleName("form_subtitle");
		ft.setWidget(ft.getRowCount(), 0, title6);

		final ListBox cores = new ListBox();
		ft.setWidget(ft.getRowCount(), 0, cores);
		for(int i=1 ; i<=8 ; i++)
			cores.addItem(i+"");
		cores.setSelectedIndex(0);
		cores.setVisibleItemCount(4);
		cores.setWidth("100px");


		final HTML send = new HTML();
		send.addStyleName("button send_form_btn");
		send.setHTML("<span><span>Create VM</span></span>");
		send.setVisible(false);

		final HTML send_inact = new HTML();
		send_inact.addStyleName("button grey_btn");
		send_inact.setHTML("<span><span>Create VM</span></span>");

		//add both buttons in a panel
		final HorizontalPanel btns = new HorizontalPanel();
		btns.add(send);
		btns.add(send_inact);

		int row = ft.getRowCount();
		ft.setWidget(row, 0, btns);
		ft.getCellFormatter().setHeight(row, 0, "50px");
		ft.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		view.add(errors);
		send.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				errors.clear();
				if( "".equals(vm_name.getText()) ) {

					errors.add(HTMLwriter.getSystemMessage("red","Please select a name for the VM"));

					return;
				}
				else if( vnets.getSelectedIndex()<1 ) {

					errors.add(HTMLwriter.getSystemMessage("red","Please select a network for the VM"));

					return;
				}

				String user1;

				if(user.isAdmin())
					user1=users.getItemText(users.getSelectedIndex());
				else
					user1=user.getUsername();

				String template = templs.getItemText(templs.getSelectedIndex());
				String mem = memory.getItemText(memory.getSelectedIndex());
				String net = vnets.getItemText(vnets.getSelectedIndex());
				mem = mem.substring(0, mem.length()-3);
				int mnimi = Integer.parseInt(mem);
				int pirines = Integer.parseInt(cores.getItemText(cores.getSelectedIndex()));
				serverCall.createVm(user1, template, vm_name.getText(), net, mnimi, pirines, new AsyncCallback<String>() {

					public void onFailure(Throwable caught)
					{

						errors.add(HTMLwriter.getSystemMessage("red","Asyncronous call to the server failed"));

					}

					public void onSuccess(String result) 
					{
						errors.clear();
						if( "ok".equals(result) ) {

							errors.add(HTMLwriter.getSystemMessage("green","Virtual Machine was succesfully created"));
							send.setVisible(false);
							send_inact.setVisible(true);
							users.setSelectedIndex(0);
							templs.clear();

							templs.addItem("-- Select a user first --");
							templs.setSelectedIndex(0);

							vm_name.setText("");
							vnets.setSelectedIndex(0); // :)
							memory.setSelectedIndex(0);
							cores.setSelectedIndex(0);
						}
						else
							errors.add(HTMLwriter.getSystemMessage("red",result));

					}
				});
			}
		});

		//NETWORKS CALLBACK

		AsyncCallback<String[]> callback2 = new AsyncCallback<String[]>()
		{
			public void onFailure(Throwable caught) {
				errors.clear();
				errors.add(HTMLwriter.getSystemMessage("red","Asyncronous call to the server failed"));

			}

			public void onSuccess(String[] result) {
				errors.clear();
				if( result == null )
					errors.add(HTMLwriter.getSystemMessage("red","Asyncronous call to the server failed"));
				else {
					//users.addItem("-- Select one --");
					for(String name : result )
						vnets.addItem(name);
					vnets.setSelectedIndex(0);
				}

			}

		};
		if(user.isAdmin())
		{
			serverCall.adminGetAllNetworks(callback2);
		}
		else
		{
			serverCall.getNetworksbyUser(user.getUsername(),callback2);
			serverCall.getNetworksbyUser("public",callback2);
		}



		AsyncCallback<String[]> callback = new AsyncCallback<String[]>()
		{
			public void onFailure(Throwable caught) {
				errors.clear();
				errors.add(HTMLwriter.getSystemMessage("red","Asyncronous call to the server failed"));

			}

			public void onSuccess(String[] result) {
				errors.clear();
				if( result == null )
					errors.add(HTMLwriter.getSystemMessage("red","Asyncronous call to the server failed"));
				else {
					//users.addItem("-- Select one --");
					for(String name : result )
						users.addItem(name);
					users.setSelectedIndex(0);
				}

			}

		};
		if(user.isAdmin())
		{
			serverCall.getUsernamesList(callback);
		}
		else
		{
			users.addItem(user.getUsername());
			users.addItem("public");
		}

		users.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				ListBox users = (ListBox) event.getSource();
				if( "-- Select one --".equals(users.getValue(users.getSelectedIndex() )) ) {
					templs.clear();
					templs.addItem("-- Select a user first --");
					send.setVisible(false);
					send_inact.setVisible(true);
				}
				else {
					templs.clear();
					String user1;

					user1 = users.getValue(users.getSelectedIndex());

					serverCall.getUserTemplates(user1, false, new AsyncCallback<String[][]>() {

						public void onFailure(Throwable caught) {

							root.add(HTMLwriter.getSystemMessage("red","Asyncronous call to the server failed"));

						}

						public void onSuccess(String[][] result) {

							if( result == null )
								root.add(HTMLwriter.getSystemMessage("red","Asyncronous call to the server failed"));
							else {
								int i=1,size=result.length;


								while(i<size)
								{
									String vm = result[i][0];
									templs.addItem(vm);
									i++;
								}
								if( result.length == 1 )
								{  templs.addItem("-- This user has no templates --");
								send_inact.setVisible(true);
								send.setVisible(false);
								}
								else {
									send_inact.setVisible(false);
									send.setVisible(true);
								}
							}

						}

					});
				}
			}

		});

	}

}


