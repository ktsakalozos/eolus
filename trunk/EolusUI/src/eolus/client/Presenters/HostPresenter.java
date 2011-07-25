package eolus.client.Presenters;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;

import eolus.client.FunctionObject;
import eolus.client.UI_Elements.HTMLwriter;
import eolus.client.UI_Elements.LightBox;
import eolus.client.UI_Elements.MyTable;

public class HostPresenter extends Presenter
{
	String[][] HostList;
	final String ACTIVE="";
	final String INACTIVE="DISABLED";

	public void loadListHosts()
	{  
		loadpage("Hosts");

		final AsyncCallback<String[][]> callback = new AsyncCallback<String[][]>()
		{

			public void onFailure(Throwable caught) 
			{
				caught.getMessage();
				// panel.clear();
				view.clear();
				HTML msg = HTMLwriter.getSystemMessage("red", "Error contacting server."/*+caught.getMessage()*/);
				view.add(msg);

			}

			public void onSuccess(final String[][] hosts) 
			{

				HostList= hosts;
				loadpagination(hosts.length - 1); 
				loadHostTables();

			}	

		};

		setRefreshTask(new FunctionObject(){

			public void function()
			{

				serverCall.getHostsList(callback); 

			}

		});


	}

	public void reload()  //to be used for pagination
	{
		loadpagination(HostList.length-1);
		hideContent();
		loadHostTables();


	}
	public void loadHost(final String[] data,final MyTable table, final int row)
	{
		table.getTable().getRowFormatter().setVisible(row,true);
		int i=0,size=data.length;
		while(i<size)
		{


			table.getTable().setWidget(row,i,new HTML(data[i]));
			i++;
		}
		//kai koumpia

		Button activate = new Button("&nbsp;&nbsp;&nbsp;Activate");
		activate.setStyleName("btn activate");
		Button deactivate = new Button("&nbsp;&nbsp;&nbsp;Deactivate");
		deactivate.setStyleName("btn deactivate");
		Button delete = new Button("&nbsp;&nbsp;&nbsp;Delete");
		delete.setStyleName("btn remove");
		if(!data[6].equalsIgnoreCase(INACTIVE))
		{
			activate.setEnabled(false);
			deactivate.setEnabled(true);
		}

		if(data[6].equalsIgnoreCase(INACTIVE))
		{
			activate.setEnabled(true);
			deactivate.setEnabled(false);
		}
		if(data[4].equalsIgnoreCase("0 MB"))
		{
			activate.setEnabled(false);
			deactivate.setEnabled(false);

		}
		FlowPanel actions =  new FlowPanel();
		table.getTable().setWidget(row,size++,actions);
		actions.add(activate);
		actions.add(deactivate);
		actions.add(delete);

		delete.addClickHandler(new ClickHandler() /* actions on hosts :) */
		{

			public void onClick(ClickEvent event)
			{	serverCall.deleteHost(data[0], new AsyncCallback<String>()
					{

				public void onFailure(Throwable caught)
				{
					LightBox l = new LightBox();
					l.add(HTMLwriter.getSystemMessage("red", "Could not delete host: "+caught.getMessage()));
					l.show();

				}
				public void onSuccess(String result)
				{
					if(result.equalsIgnoreCase("ok"))
					{
						//table.getTable().getRowFormatter().setVisible(row,false);
						doRefresh();
					}
				}
					});

			}
		});


		activate.addClickHandler(new ClickHandler() /* actions on hosts :) */
		{

			public void onClick(ClickEvent event)
			{	serverCall.enableHost(data[0],true, new AsyncCallback<String>()
					{

				public void onFailure(Throwable caught)
				{
					LightBox l = new LightBox();
					l.add(HTMLwriter.getSystemMessage("red", "Could not activate host: "+caught.getMessage()));
					l.show();

				}
				public void onSuccess(String result)
				{
					if(result.equalsIgnoreCase("ok"))
					{
						doRefresh();
					}
				}
					});

			}
		});

		deactivate.addClickHandler(new ClickHandler() /* actions on hosts :) */
		{

			public void onClick(ClickEvent event)
			{	serverCall.enableHost(data[0],false, new AsyncCallback<String>()
					{

				public void onFailure(Throwable caught)
				{
					LightBox l = new LightBox();
					l.add(HTMLwriter.getSystemMessage("red", "Could not deactivate host: "+caught.getMessage()));
					l.show();
				}
				public void onSuccess(String result)
				{
					if(result.equalsIgnoreCase("ok"))
					{
						doRefresh();
					}
				}
					});

			}
		});


	}

	public void loadHostTables()
	{

		int i=(current_page-1) * page_size +1 ,size=HostList.length;
		int itemsadded=0;

		final VerticalPanel content = (VerticalPanel) this.getContentWidgetNoClear("hosts","Hosts List");		
		final String [] headers = HostList[0];
		final MyTable table;

		if(content.getWidgetCount() ==0)
		{

			table= new MyTable(headers);
			content.add(table);
		}
		else
		{

			table = (MyTable) content.getWidget(content.getWidgetCount()-1);


		}

		while(i<size)
		{

			if(HostList[i]==null)
				break;


			loadHost(HostList[i],table,itemsadded+1);
			i++;
			itemsadded++;
			if(itemsadded==page_size)
				break;
		}
		table.removeExtra(itemsadded);
		MyTable.styleRows(1,HostList.length,table.getTable());


	}



	public void loadCreateHost()
	{
		loadpage("Create New Host");

		/* Kristian */

		final FlexTable ft = new FlexTable();
		ft.setCellPadding(5);

		final VerticalPanel root = view;
		root.add(ft);
		root.add(formerrors);

		Label title1 = new Label("Select Name for new Host:");
		title1.addStyleName("form_subtitle");
		ft.setWidget(ft.getRowCount(), 0, title1);

		final TextBox host_name = new TextBox();
		ft.setWidget(ft.getRowCount(), 0, host_name);
		host_name.setWidth("250px");

		final HTML send = new HTML();
		send.addStyleName("button send_form_btn");
		send.setHTML("<span><span>Create Host</span></span>");

		int row = ft.getRowCount();
		ft.setWidget(ft.getRowCount(), 0, send);
		ft.getCellFormatter().setHeight(row, 0, "50px");
		ft.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);


		send.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				formerrors.clear();
				if( host_name.getText().length() < 1 ) {

					formerrors.add(HTMLwriter.getSystemMessage("red","Please select a name first"));

				}
				else {
					serverCall.createHost(host_name.getText(), new AsyncCallback<String>() {

						public void onFailure(Throwable caught) {

							formerrors.add(HTMLwriter.getSystemMessage("red","Asyncronous call to the server failed"));

						}

						public void onSuccess(String result) {

							if( !"ok".equals(result) )
								formerrors.add(HTMLwriter.getSystemMessage("red",result));
							else
								formerrors.add(HTMLwriter.getSystemMessage("green","Host \""+host_name.getText()+"\" was succesfully created"));

							host_name.setText("");
							host_name.setFocus(true);
						}

					});
				}
			}

		});



	}

	public void init()
	{
		//page_size=15;// 
		String[] s1={"ListHosts" ,"List Hosts"};
		String[] s2=  {"CreateHost","Create Host"};
		admin_sidebar.add(s1);
		admin_sidebar.add(s2);
		user_sidebar.clear();
		user_icons.clear();
		String[] i1={"ListHosts","d22_1" ,"List Hosts"};
		String[] i2=  {"CreateHost","d22_2" ,"Create Host"};    
		admin_icons.add(i1);
		admin_icons.add(i2);
		category ="Hosts";
	}




}
