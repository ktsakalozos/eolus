package eolus.client.Presenters;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import eolus.client.FunctionObject;
import eolus.client.UI_Elements.HTMLwriter;
import eolus.client.UI_Elements.LightBox;
import eolus.client.UI_Elements.MyHandlers;
import eolus.client.UI_Elements.MyTable;

public class TemplatePresenter extends Presenter{
	private String[] UserList;
	private boolean sync=false;
	Label syncStatus = new Label();

	public void loadMyTemplates()
	{	
		loadpage("View My Templates");
		Button syn = new Button("&nbsp;&nbsp;&nbsp;Sync Templates");
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
				//Window.alert("refreshing!");
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

				serverCall.syncTemplates(user.getUsername(), callback);
				//sync public templates? :)
				MyHandlers.setPositive(syncStatus, "SYNCING...");
			}


		});


	}

	public void loadListTemplates()
	{  //enable/disable/delete
		//Window.alert("load list hosts");
		loadpage("Templates");
		Button syn = new Button("&nbsp;&nbsp;&nbsp;Sync Templates");
		syn.setStyleName("btn sync");
		FlexTable f = new FlexTable();
		view.add(f);
		f.setWidget(0, 0, syn);
		f.setWidget(0, 1, syncStatus);

		final AsyncCallback<String[]> callback = new AsyncCallback<String[]>()
		{

			public void onFailure(Throwable caught) 
			{
				caught.getMessage();
				view.clear();
				HTML msg = HTMLwriter.getSystemMessage("red", "Error contacting server.");
				view.add(msg);

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
				//Window.alert("refresh test");
				serverCall.getUsers(callback); //TODO: Possibly call it on each successful action

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

				serverCall.adminSyncAllTemplates(callback);
				MyHandlers.setPositive(syncStatus, "SYNCING...");
			}


		});





	}

	public void loadUser(final String userdata)
	{
		final VerticalPanel content = (VerticalPanel) this.getContentWidgetNoClear(userdata,userdata+"'s Templates");		
		//final String [] headers = {"Template name","Status","Actions"};
		final MyTable table;
		//tabular data ftw
		if(content.getWidgetCount() ==0)
		{
			//Window.alert("no children");
			table= new MyTable();
			content.add(table);
		}
		else
		{
			//Window.alert("some childrens "+content.getWidgetCount());
			table = (MyTable) content.getWidget(content.getWidgetCount()-1);
			//table.removeAll();

		}

		serverCall.getUserTemplates(userdata,sync, new AsyncCallback<String[][]>() 
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
					loadTemplate(userdata,table,result[i],i+1);
					i++;
				}
				/*format */
				if(1==size)
				{

					table.getTable().setWidget(0,0,new Label("This user has no templates."));
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

	public void loadTemplate(final String username,MyTable table, final String[] templatedata,final int row)
	{
		final FlexTable ft = table.getTable();
		ft.getRowFormatter().setVisible(row,true);
		int i=0,size=templatedata.length;
		while(i<size)
		{

			ft.setWidget(row,i,new HTML(templatedata[i]));
			i++;
		}
		FlowPanel actions =  new FlowPanel();
		table.getTable().setWidget(row,size++,actions);




		if(!username.equals("public") && user.isAdmin())
		{  
			Button mkpublic = new Button("&nbsp;&nbsp;&nbsp;Make Public");
			actions.add(mkpublic);
			mkpublic.setStyleName("btn mkpublic");
			mkpublic.addClickHandler(new ClickHandler()
			{

				public void onClick(ClickEvent event)
				{

					serverCall.makeTemplatePublic(username, templatedata[0],new AsyncCallback<String>() 
							{

						public void onFailure(Throwable caught) 
						{
							LightBox l = new LightBox();
							l.add(HTMLwriter.getSystemMessage("red", caught.getMessage()));
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
								l.add(HTMLwriter.getSystemMessage("red", result));
								l.show();
							}


						}
							}); //end server call

				}


			}); //end clickhandler
		}

		Button copy = new Button("&nbsp;&nbsp;&nbsp;Copy");
		copy.setStyleName("btn copy_action");
		Button move = new Button("&nbsp;&nbsp;&nbsp;Move");
		move.setStyleName("btn migrate");
		Button del = new Button("&nbsp;&nbsp;&nbsp;Delete");
		del.setStyleName("btn remove");
		actions.add(copy);
		actions.add(move);
		if(user.isAdmin())
			actions.add(del);
		//HANDLE DELETE//
		del.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{

				serverCall.deleteTemplate(username, templatedata[0],new AsyncCallback<String>() 
						{

					public void onFailure(Throwable caught) 
					{
						LightBox l = new LightBox();
						l.add(HTMLwriter.getSystemMessage("red", caught.getMessage()));
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
							l.add(HTMLwriter.getSystemMessage("red", result));
							l.show();
						}


					}
						}); //end server call

			}


		}); //end clickhandler

		//HANDLE COPY//

		copy.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{

				selectUser(username,templatedata[0],false);
			}

		}); //end clickhandler


		//HANDLE MOVE//
		move.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{

				selectUser(username,templatedata[0],true);
			}

		}); //end clickhandler
	}


	public void selectUser(final String username,final String template,final Boolean isMove)
	{

		final LightBox  glass = new LightBox(); //the lightbox

		final VerticalPanel v = new VerticalPanel();
		String action = "copy";
		String actionCaps = "Copy";
		if(isMove)
		{
			action="move";
			actionCaps="Move";
		}



		final TextBox  tname = new TextBox();
		v.add(new Label("Name to "+action+" template to:"));
		v.add(tname);
		glass.add(v);

		Button submit = new Button(actionCaps);
		v.add(submit);

		submit.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(tname.getText().equals(""))
					return;


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

							msg = HTMLwriter.getSystemMessage("green", "Template successfully copied");
							if(isMove)
							{
								msg = HTMLwriter.getSystemMessage("green", "Template successfully moved");
							}

							glass.autoclose(); 
							doRefresh();
						}
						else
						{
							msg = HTMLwriter.getSystemMessage("red", "Problem perfoming this action: "+result);
						}
						v.add(msg);	

					}

				};
				if(isMove)
				{
					serverCall.moveTemplate(username, template, tname.getText(), callback);
				}
				else
				{
					serverCall.copyTemplate(username, template, tname.getText(), callback);

				}

			}
		});
		glass.show();


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
		String[] s1={"ListTemplates" ,"List Templates"};
		String[] s3={"MyTemplates" ,"My Templates"};
		admin_sidebar.add(s1);
		admin_sidebar.add(s3);
		user_sidebar.add(s3);
		user_icons.clear();
		String[] i1={"ListTemplates","d22_1" ,"List Templates"};
		String[] i3={"MyTemplates","d22_1" ,"My Templates"};
		admin_icons.add(i1);
		admin_icons.add(i3);
		user_icons.add(i3);
		category ="Templates";
	}

}
