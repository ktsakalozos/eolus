package eolus.client.Presenters;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import eolus.client.FunctionObject;
import eolus.client.UI_Elements.HTMLwriter;
import eolus.client.UI_Elements.LightBox;
import eolus.client.UI_Elements.MyDropDown;
import eolus.client.UI_Elements.MyHandlers;
import eolus.client.UI_Elements.MyTable;

public class ScriptPresenter extends Presenter
{
	String[] UserList;
	boolean sync =false;
	Label syncStatus = new Label();

	public void loadRunScript()
	{
		loadpage("Run a Script");

	}

	public void loadRunCommand()
	{
		loadpage("Run a Command");
		view.add(new Label("Select VM:"));
		final ListBox vms = new ListBox();
		view.add(vms);
		final HTML error_vm = HTMLwriter.getSystemMessage("red", "Please select a virtual machine.");
		error_vm.setVisible(false);
		view.add(error_vm);
		view.add(new Label("Type command:"));
		final TextArea ta = new TextArea();
		ta.setCharacterWidth(80);
		ta.setVisibleLines(8);
		view.add(ta);
		final HTML error_script = HTMLwriter.getSystemMessage("red", "Please type a command.");
		error_script.setVisible(false);
		view.add(error_script);

		Button submit = new Button("Execute Command");
		view.add(submit);

		view.add(new Label("Command Output:"));
		final TextArea outp = new TextArea();
		outp.setCharacterWidth(80);
		outp.setVisibleLines(4);
		view.add(outp);

		submit.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				outp.setText(""); 
				error_script.setVisible(false);
				error_vm.setVisible(false);
				/*validate */
				if(ta.getText().equalsIgnoreCase(""))
				{
					error_script.setVisible(true);
					return;
				}
				if(vms.getItemCount()==0 || vms.getSelectedIndex()==-1)
				{
					error_vm.setVisible(true);
					return;
				}
				/* handle command execution */
				AsyncCallback<String[]> callback = new AsyncCallback<String[]>()
				{

					public void onFailure(Throwable caught) 
					{
						caught.getMessage();

						HTML msg = HTMLwriter.getSystemMessage("red", "Error contacting server."/*+caught.getMessage()*/);
						view.add(msg);


					}

					public void onSuccess(String[] result)
					{
						//the command went through
						if( result == null )
						{


							HTML msg = HTMLwriter.getSystemMessage("red", errorRequest/*+caught.getMessage()*/);
							view.add(msg);

						}
						else
						{

							String s = "Command executed.\nstdout: "+result[0]+"\nstderr: "+result[1];
							outp.setText(s);


						}

					}

				};//end server call definition
				serverCall.runCommand(user.getUsername(), vms.getItemText(vms.getSelectedIndex()),ta.getText(), callback);
				outp.setText("Executing command..");
			}


		});//end clickhandler


		/* asynchronously fetch vms from the server */ 
		AsyncCallback<String[]> callback = new AsyncCallback<String[]>()
		{

			public void onFailure(Throwable caught) 
			{
				caught.getMessage();
				view.clear();
				HTML msg = HTMLwriter.getSystemMessage("red", "Error contacting server."/*+caught.getMessage()*/);
				view.add(msg);


			}

			public void onSuccess(String[] result) {

				if( result == null )
				{

					view.clear();
					HTML msg = HTMLwriter.getSystemMessage("red", errorRequest/*+caught.getMessage()*/);
					view.add(msg);

				}
				else
				{

					for(String name : result )
						vms.addItem(name);
					vms.setSelectedIndex(0);
				}

			}

		};

		if(user.isAdmin())
		{
			serverCall.adminGetAllVms(callback);
		}
		else
		{
			serverCall.getUserVmList(user.getUsername(), callback);
		}



	}

	public void loadUploadScript()
	{	
		loadpage("Upload a Script");

		final VerticalPanel panel = new VerticalPanel();

		view.add(panel);		
		Label label1 = new Label("Script Name:");
		panel.add(label1);
		final HTML error_name= HTMLwriter.getSystemMessage("red","Name cannot be blank  or contain spaces.");
		error_name.setVisible(false);
		final TextBox name = new TextBox();
		name.setName("nvnet_name");
		panel.add(name);
		panel.add(error_name);
		panel.add(new Label("Script Description:"));
		final TextArea descr = new TextArea();
		descr.setCharacterWidth(80);
		descr.setVisibleLines(7);
		panel.add(descr);
		final TextArea ta = new TextArea();
		ta.setCharacterWidth(80);
		ta.setVisibleLines(20);
		panel.add(new Label("Script:"));
		panel.add(ta);
		final HTML error_script= HTMLwriter.getSystemMessage("red","Please enter a script.");
		error_script.setVisible(false);
		panel.add(error_script);

		Button submit = new Button("Upload Script");
		panel.add(submit);

		final AsyncCallback<String> callback = new AsyncCallback<String>()
		{

			public void onFailure(Throwable caught) 
			{
				contactError(caught);

			}

			public void onSuccess(String b) 
			{
				HTML msg = new HTML();
				if(b.equals("ok"))
				{

					msg= HTMLwriter.getSystemMessage("green", "Script uploaded successfully." +
					" You can now view it.");
				}
				else
				{  
					msg= HTMLwriter.getSystemMessage("red", "There was an error uploading the script. "+b); 
				}

				panel.clear(); 
				panel.add(msg);

			}
		};

		submit.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{ 
				error_name.setVisible(false);
				error_script.setVisible(false);
				if(ta.getText().equals(""))
				{
					error_script.setVisible(true);
					return;
				}
				if(name.getText().equals(""))
				{
					error_name.setVisible(true);
					return;
				}

				//Window.alert("sumbitiing");
				String script_name = name.getText();
				String description = descr.getText();
				String script = ta.getText();
				if(user.isAdmin())
					serverCall.adminCreateScript("public", script_name,script , description, callback);
				else
					serverCall.userCreateScript(script_name,script , description, callback);

			}

		}); //end of clickhandler




	}

	public void loadMyScripts()
	{	
		loadpage("View My Scripts");
		Button syn = new Button("&nbsp;&nbsp;&nbsp;Sync Scripts");
		syn.setStyleName("btn sync");
		FlexTable f = new FlexTable();
		view.add(f);
		f.setWidget(0, 0, syn);
		f.setWidget(0, 1, syncStatus);




		setRefreshTask(new FunctionObject() /* periodically refresh user data */
		{	
			public void function()
			{
				loadUser(user.getUsername());
				loadUser("public");
				sync=false;

			}
		});



		syn.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{


				AsyncCallback<String> callback = new AsyncCallback<String>() 
				{

					public void onFailure(Throwable caught)
					{
						MyHandlers.setNegative(syncStatus, "SYNC FAILED");
					}
					public void onSuccess(String result)
					{

						if(result.equalsIgnoreCase("ok"))
						{
							MyHandlers.setPositive(syncStatus, "SYNCED");
							MyHandlers.autoHide(syncStatus);
							doRefresh();
						}
						else
						{
							MyHandlers.setNegative(syncStatus, "SYNC FAILED");
						}
					}
				};

				serverCall.syncScripts(user.getUsername(), callback);
				MyHandlers.setPositive(syncStatus, "SYNCING...");
			}


		});


	}

	public void loadUser(final String userdata) //load a user's vms :)
	{

		final VerticalPanel content = (VerticalPanel) this.getContentWidgetNoClear(userdata,userdata+"'s Scripts");		

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
		final FlexTable ft = table.getTable();
		AsyncCallback<String[][]> callback = new AsyncCallback<String[][]>() 
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

					loadScript(userdata,ft,result[i],i+1);
					i++;
				}
				if(1==size)
				{


					table.getTable().setWidget(0,0,new Label("This user has no scripts"));
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
		};	

		serverCall.getUserScripts(userdata,sync,callback);


	}

	public void runScript(final String username,final String scriptname,final String[] vms)
	{

		final LightBox  glass = new LightBox(); //the lightbox

		final VerticalPanel v = new VerticalPanel();

		final MyDropDown menu= new MyDropDown(vms,"Select VM to run the script "+scriptname,"Run Script");

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
				final String vm = lb.getItemText(lb.getSelectedIndex());
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
							msg = HTMLwriter.getSystemMessage("green", "Script successfully applied on VM "+vm);
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

				serverCall.runScript(username, scriptname, vm, callback);
			}
		});
		glass.show();
	}

	public void loadRunScript(final String username,final String scriptname)
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
					if(result.length==0) /* the selected user has no vms */
					{
						msg = HTMLwriter.getSystemMessage("yellow", "The selected user has no virtual machines.");
						glass.add(msg);
						glass.show();
					}
					else	
						runScript(username,scriptname,result);
				}
				else
				{
					msg = HTMLwriter.getSystemMessage("red", "Problem perfoming this action "+result);
					glass.add(msg);
					glass.show();
				}


			}

		};
		//if admin, load all vms?
		//serverCall.adminGetAllVms(callback);
		serverCall.getUserVmList(username, callback);

	}

	public void loadScript(final String username,final FlexTable ft,final String[] scriptdata,final int row)
	{

		ft.getRowFormatter().setVisible(row,true);
		int i=0,size=scriptdata.length;
		while(i<size)
		{

			ft.setWidget(row,i,new Label(scriptdata[i]));
			i++;
		}
		FlowPanel actions =  new FlowPanel();
		ft.setWidget(row,size++,actions);
		Button exec = new Button("&nbsp;&nbsp;&nbsp;Run Script on VM");
		exec.setStyleName("btn run");
		//ft.setWidget(row,size++,exec);
		actions.add(exec);

		exec.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				loadRunScript(username,scriptdata[0]);	
			}

		});


		Button del = new Button("&nbsp;&nbsp;&nbsp;Remove Script");
		del.setStyleName("btn remove");
		if(user.isAdmin() || !scriptdata[0].equals("public"))
			actions.add(del);

		del.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{

				dodelete(username,scriptdata[0]);
			}

		});	
	}


	public void dodelete(String username, String scriptname)
	{
		AsyncCallback<String> callback = new AsyncCallback<String>() 
		{

			public void onFailure(Throwable caught)
			{
				LightBox l = new LightBox();
				l.add(HTMLwriter.getSystemMessage("red", errorServer +" "+caught.getMessage()));
				l.show();
			}
			public void onSuccess(String result)
			{

				if(result.equalsIgnoreCase("ok"))
				{

					doRefresh();
				}
				else
				{
					LightBox l = new LightBox();
					l.add(HTMLwriter.getSystemMessage("red", errorRequest +" "+result)); //errorRequest +" "+result
					l.show();

				}
			}
		};

		if(user.isAdmin())
		{
			serverCall.adminRemoveScript(username, scriptname, callback);
		}
		else
		{
			serverCall.userRemoveScript(scriptname, callback);
		}


	}


	public void loadListScripts()
	{		
		loadpage("View Scripts");
		Button syn = new Button("&nbsp;&nbsp;&nbsp;Sync Scripts");
		syn.setStyleName("btn sync");
		FlexTable f = new FlexTable();
		view.add(f);
		f.setWidget(0, 0, syn);
		f.setWidget(0, 1, syncStatus);
		final AsyncCallback<String[]> callback = new AsyncCallback<String[]>()
		{

			public void onFailure(Throwable caught) 
			{
				contactError(caught);
			}

			public void onSuccess(final String[] users) 
			{
				//Window.alert("we haz the info: "+clusters.length);
				UserList= users;
				loadpagination(users.length); 
				loadUserTables();

			}	

		};

		setRefreshTask(new FunctionObject(){

			public void function()
			{
				serverCall.getUsers(callback);
			}

		});

		syn.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				AsyncCallback<String> callback = new AsyncCallback<String>() 
				{

					public void onFailure(Throwable caught)
					{
						MyHandlers.setNegative(syncStatus, "SYNC FAILED");
					}
					public void onSuccess(String result)
					{

						if(result.equalsIgnoreCase("ok"))
						{
							MyHandlers.setPositive(syncStatus, "SYNCED");
							MyHandlers.autoHide(syncStatus);
							doRefresh();
						}
						else
						{
							MyHandlers.setNegative(syncStatus, "SYNC FAILED");
						}
					}
				};

				serverCall.adminSyncAllScripts(callback);
				MyHandlers.setPositive(syncStatus, "SYNCING...");
			}


		});



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
			{

				return;
			}

		}

		sync=false;	
	}


	public void init()
	{
		String[] s1=	{"ListScripts", "List Scripts"};
		String[] s2=	  {"RunCommand", "Run Command on Vm"};
		String[] s3=   	{"UploadScript", "Upload a Script"};
		String[] us1=	  {"MyScripts", "My Scripts"};
		admin_sidebar.add(s1);
		admin_sidebar.add(s2);
		admin_sidebar.add(s3);
		admin_sidebar.add(us1);
		user_sidebar.add(us1);
		user_sidebar.add(s2);
		user_sidebar.add(s3);
		String[] i1={"ListScripts","d1_1", "List Scripts"};
		String[] i2=   {"RunCommand","d1_1", "Run Command on Vm"};
		String[] i3=   {"UploadScript","d1_1", "Upload a Script"};
		String[] ui1=	  {"MyScripts","d1_1", "My Scripts"};
		admin_icons.add(i1);
		admin_icons.add(i2);
		admin_icons.add(i3);
		admin_icons.add(ui1);
		user_icons.add(ui1);
		user_icons.add(i2);
		user_icons.add(i3);
		category ="Scripts";

	}


}
