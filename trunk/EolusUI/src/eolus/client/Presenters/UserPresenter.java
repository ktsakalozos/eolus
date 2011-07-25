package eolus.client.Presenters;

import java.util.Vector;


import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import eolus.client.FunctionObject;
import eolus.client.UI_Elements.HTMLwriter;
import eolus.client.UI_Elements.LightBox;


public class UserPresenter extends Presenter{

	String[][] UserList;


	public void loadDeleteUser(String s) //Kristians' function
	{
		loadpage("Delete a User");		
		final FlexTable ft = new FlexTable();
		ft.setCellPadding(5);
		ft.addStyleName("center_table");
		final VerticalPanel root = view;
		root.add(ft);
		//The field title
		final Label title = new Label("Select user to be Deleted");
		title.addStyleName("form_subtitle");
		//The button to delete the user
		final HTML delete = new HTML();
		delete.addStyleName("button red_btn"); //:)
		delete.setHTML("<span><span>Delete User</span></span>");
		delete.setVisible(false);
		//The inactive delete button
		final HTML delete_inact = new HTML();
		delete_inact.addStyleName("button grey_btn");// :) neat idea
		delete_inact.setHTML("<span><span>Delete User</span></span>");

		//add both buttons in a panel
		final HorizontalPanel btns = new HorizontalPanel();
		btns.add(delete_inact);
		btns.add(delete);
		btns.addStyleName("float_right");

		//The suggest box with the usernames
		final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		final SuggestBox box = new SuggestBox(oracle);
		final Vector<String> names = new Vector<String>();
		box.setWidth("200px");
		box.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if( names.contains(box.getText())) {
					delete_inact.setVisible(false);
					delete.setVisible(true);
				}
				else {
					delete.setVisible(false);
					delete_inact.setVisible(true);
				}
			}   
		});
		box.addSelectionHandler(new SelectionHandler<Suggestion>() {
			public void onSelection(SelectionEvent<Suggestion> event) {
				delete_inact.setVisible(false);
				delete.setVisible(true);
			}
		});

		final VerticalPanel errors =  new VerticalPanel();
		root.add(errors);
		delete.addClickHandler(new ClickHandler() {   
			public void onClick(ClickEvent event) {
				errors.clear();
				if( Window.confirm("Are you sure you want to delete the user \""+box.getText()+
				"\"?\nThis action cannot be undone.") )
					serverCall.deleteUser(box.getText(), new AsyncCallback<String>() {

						public void onFailure(Throwable caught)
						{

							root.add(HTMLwriter.getSystemMessage("red", "Asyncronous call to the server failed"));

						}

						public void onSuccess(String result)
						{

							if( "ok".equals(result) ) {
								errors.add(HTMLwriter.getSystemMessage("green", "The user \""+box.getText()+"\" was succesfully deleted"));
								box.setText("");
								delete.setVisible(false);
								delete_inact.setVisible(true);
							}
							else {
								errors.add(HTMLwriter.getSystemMessage("red", result));
							}

						}

					});
			}
		});

		ft.setWidget(0, 0, title);
		ft.setWidget(1, 0, box);
		ft.setWidget(2, 0, btns);

		serverCall.getUsernamesList(new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				root.add(HTMLwriter.getSystemMessage("red","Failed to get usernames from server"));
			}
			public void onSuccess(String[] result) {
				for(String name:result) {
					names.add(name);
					oracle.add(name);
				}
			}
		});	
	}




	public void loadListUsers()
	{		
		loadpage("View Users List");
		//view.add(new Label("Viewing users")); //temp
		final AsyncCallback<String[][]> callback = new AsyncCallback<String[][]>()
		{

			public void onFailure(Throwable caught) 
			{
				caught.getMessage();
				// panel.clear();
				HTML msg = HTMLwriter.getSystemMessage("red", "Error contacting server."+caught.getMessage());
				view.add(msg);

			}

			public void onSuccess(final String[][] users) 
			{

				UserList= users;
				loadpagination(users.length-1); 
				loadUserTables();

			}	

		};

		setRefreshTask(new FunctionObject(){

			public void function()
			{
				serverCall.getUsersList(callback); 			
			}

		});


	}


	public void loadUser(final String[] userdata)
	{

		VerticalPanel content = (VerticalPanel) this.getContentWidget(userdata[0],"User: "+userdata[0]);
		Button l2 = new Button("&nbsp;&nbsp;&nbsp;Delete "+userdata[0]);
		l2.setStyleName("btn remove");
		FlowPanel f =  new FlowPanel();
		content.add(f);
		//f.add(l1);
		f.add(l2);



		l2.addClickHandler(new ClickHandler() //user deletion
		{
			public void onClick(ClickEvent event)
			{			
				final LightBox l = new LightBox();
				l.add(new Label("Are you sure you want to delete user "+userdata[0]+"? This action cannot be undone."));
				Button yes = new Button("Yes, delete");
				Button cancel = new Button("Cancel");
				FlowPanel f = new FlowPanel();
				f.add(yes);
				f.add(cancel);
				l.add(f);
				l.show();
				yes.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{ //do delete

						final AsyncCallback<String> callback = new AsyncCallback<String>()
						{

							public void onFailure(Throwable caught) 
							{
								caught.getMessage();
								HTML msg = HTMLwriter.getSystemMessage("red", "Error contacting server."+caught.getMessage());
								l.clear();
								l.add(msg);

							}

							public void onSuccess(final String reply) 
							{
								tables.get(userdata[0]).setVisible(false);
								l.close();
							}	

						};	
						serverCall.deleteUser(userdata[0], callback);	
					}
				});
				cancel.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{ //do nothing
						l.close(); 

					}
				});				
			}			
		});//end of user delete clickhandler

	}


	public void reload()  //to be used for pagination
	{
		loadpagination(UserList.length);
		hideContent();
		loadUserTables();

	}

	public void loadUserTables()
	{
		int i=(current_page-1) * page_size +1 ,size=UserList.length;
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

	public void loadCreateUser() //Kristian's form
	{
		loadpage("Create New User");
		getAddUserForm(); 

	}

	public void init()
	{

		String[] s1={"ListUsers","List Users"};
		String[] s2= {"AddUser","Add User"};
		String[] s4 = {"DeleteUser","Delete User"};
		admin_sidebar.add(s1);
		admin_sidebar.add(s2);
		admin_sidebar.add(s4);
		user_sidebar.clear();
		user_icons.clear();
		String[] i1={"ListUsers","d1_1" ,"List Users"};
		String[] i2= {"AddUser","d1_2" ,"Add User"};
		String[] i4 = {"DeleteUser","d1_4" ,"Delete User"};
		admin_icons.add(i1);
		admin_icons.add(i2);
		admin_icons.add(i4);
		category ="Users";
	}

	public void getAddUserForm() 
	{

		final FlexTable ft = new FlexTable();
		ft.setCellPadding(5);

		final VerticalPanel root = view;
		root.add(ft);
		root.add(formerrors);

		Label title1 = new Label("Username:");
		title1.addStyleName("form_subtitle");
		ft.setWidget(ft.getRowCount(), 0, title1);

		final TextBox user_name = new TextBox();
		ft.setWidget(ft.getRowCount(), 0, user_name);
		user_name.setWidth("250px");

		final HTML send = new HTML();
		send.addStyleName("button send_form_btn");
		send.setHTML("<span><span>Add user</span></span>");

		int row = ft.getRowCount();
		ft.setWidget(ft.getRowCount(), 0, send);
		ft.getCellFormatter().setHeight(row, 0, "50px");
		ft.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);


		send.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				formerrors.clear();
				if( user_name.getText().length() < 1 ) {

					formerrors.add(HTMLwriter.getSystemMessage("red","Please enter a username."));

				}
				else {
					serverCall.addUser(user_name.getText(), new AsyncCallback<String>() {

						public void onFailure(Throwable caught) {

							formerrors.add(HTMLwriter.getSystemMessage("red","Asyncronous call to the server failed"));

						}

						public void onSuccess(String result) {

							if( !"ok".equals(result) )
								formerrors.add(HTMLwriter.getSystemMessage("red",result));
							else
								formerrors.add(HTMLwriter.getSystemMessage("green","User \""+user_name.getText()+"\" was succesfully added to Eolus."));

							user_name.setText("");
							user_name.setFocus(true);
						}

					});
				}
			}

		});



	}




}
