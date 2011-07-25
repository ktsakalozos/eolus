package eolus.client.Presenters;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;

import java.util.HashMap;

import com.google.gwt.user.client.Timer;

import eolus.client.ContactServerService;
import eolus.client.ContactServerServiceAsync;
import eolus.client.FunctionObject;
import eolus.client.MyTimer;
import eolus.client.UserInfo;
import eolus.client.UI_Elements.HTMLwriter;
import eolus.client.UI_Elements.MyTable;

public abstract class Presenter {

	protected ContactServerServiceAsync serverCall = GWT.create(ContactServerService.class);
	protected UserInfo user = UserInfo.getUserInfo();
	protected HTMLwriter html = new HTMLwriter();
	protected int refreshTime=4000;
	protected final int DEFAULT_PAGE_SIZE =5;
	protected ArrayList<String[]> user_sidebar = new ArrayList<String[]>();
	protected ArrayList<String[]> admin_sidebar= new ArrayList<String[]>();
	protected ArrayList<String[]> user_icons= new ArrayList<String[]>();
	protected ArrayList<String[]> admin_icons= new ArrayList<String[]>();
	protected String category;
	protected int page_size = DEFAULT_PAGE_SIZE;
	protected int current_page=1;
	protected String[] createddivs={};
	protected HashMap<String,MyTable> tables= new HashMap<String,MyTable>();
	protected String content = "Content";
	protected String content_table="ContentTable";
	protected VerticalPanel view = new VerticalPanel();
	protected Presenter ref;
	protected VerticalPanel formerrors = new VerticalPanel();
	protected FunctionObject f;
	protected String errorRequest ="There was a problem with your request.";
	protected String errorServer ="There was a problem contacting the server.";


	public boolean divExists(String divid)
	{

		int i=0,size=createddivs.length;
		while(i<size)
		{
			if(createddivs[i].equals(divid))
				return true;
			i++;
		}
		return false;
	}

	public void contactError(Throwable caught)

	{
		caught.getMessage();
		view.clear();
		HTML msg = HTMLwriter.getSystemMessage("red", "Error contacting server."/*+caught.getMessage()*/);
		view.add(msg);


	}
	public void error(String msg)
	{

	}

	public void stopRefreshTask()
	{
		if(MyTimer.getTimer()!=null)
			MyTimer.getTimer().cancel();
	}

	public void doRefresh()
	{
		f.function();
	}

	public void setRefreshTask(final FunctionObject o)
	{

		f=o;

		Timer refresh = new Timer()
		{
			@Override
			public void run() 
			{
				o.function();

			}
		};
		MyTimer.setTimer(refresh);
		refresh.scheduleRepeating(refreshTime);
		o.function();

	}

	public void loadpage(String page_title)
	{

		stopRefreshTask();
		loadsidebar();
		Element l;	
		l = RootPanel.get(content).getElement();
		l.setInnerHTML("");
		HTMLwriter.makeheader(page_title,content,content_table);
		RootPanel.get(content).setVisible(true);
		RootPanel.get(content_table).add( (Widget) view);


	}



	public abstract void reload(); //override



	public void loadpagination(int elements){


		if( RootPanel.get("paging")!=null)
		{
			//Window.alert("paging exists");
			RootPanel.get("paging").clear();
		}
		int numPages = (int) Math.ceil((double)elements/(double)page_size); //correct!
		//Window.alert("elemets:"+elements+" and num pages:"+numPages);
		if(numPages<2)
			return;

		FlowPanel f = new FlowPanel();
		int i=1;

		Button prev = new Button("Prev");
		if(current_page == 1)
			prev.setEnabled(false);
		f.add(prev);
		prev.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {

				current_page = current_page -1;

				reload();
			}				
		});
		while(i<=numPages){
			Button button = new Button(""+i);
			if(i==current_page)
				button.setEnabled(false);
			final int page = i;
			button.addClickHandler(new ClickHandler()
			{				
				public void onClick(ClickEvent event) 
				{

					current_page =page; 

					reload();			 
				}						
			});						
			f.add(button);						
			i++;
		}

		Button next = new Button("Next");
		f.add(next);
		if(current_page == numPages)
			next.setEnabled(false);		
		next.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event) 
			{

				current_page = current_page +1;

				reload();

			}			
		});

		RootPanel.get("paging").add(f);
		RootPanel.get("paging").setVisible(true); //just in case
	}


	public Presenter()
	{
		init();
		ref=this;
	}

	public void hideContent()
	{
		int i=0,size= createddivs.length;
		while(i<size)
		{
			//if(i < page_size * (current_page -1) || i>= page_size * current_page) //hide the ones not on this page
			tables.get(createddivs[i]).setVisible(false);
			i++;
		}
	}
	public Widget getContentWidget(String name,String title) //name=id of sorts
	{
		MyTable table;
		if((table=(MyTable)(tables.get(name)))==null)
		{
			//create a new table
			table = new MyTable(title);
			tables.put(name,table);
			view.add(table);
			createddivs[createddivs.length] = name;
		}
		else
		{ //replace the data
			table.setVisible(true);
			table.getContent().clear();
		}

		return table.getContent();


	}

	public Widget getContentNoClear(String name,String title) //name=id of sorts
	{
		MyTable table;
		if((table=(MyTable)(tables.get(name)))==null)
		{
			//create a new table
			table = new MyTable(title);
			tables.put(name,table);
			view.add(table);
			createddivs[createddivs.length] = name;
		}
		else
		{ 
			table.setVisible(true);

		}

		return table;


	}

	public Widget getContentWidgetNoClear(String name,String title) //name=id of sorts
	{
		MyTable table;
		if((table=(MyTable)(tables.get(name)))==null)
		{
			//create a new table
			table = new MyTable(title);
			tables.put(name,table);
			view.add(table);
			createddivs[createddivs.length] = name;
		}
		else
		{ //replace the data
			table.setVisible(true);
			//table.getContent().clear();
		}

		return table.getContent();


	}
	/* @override this */
	public abstract void init();

	public void loadindex()
	{

		stopRefreshTask();
		loadsidebar();
		Element l;	
		l = RootPanel.get(content).getElement();
		l.setInnerHTML("");
		if(user.isAdmin())
		{

			l.setInnerHTML(HTMLwriter.getDashboard(category+" Dashboard",admin_icons));

		}

		else
		{/* plain user functions */
			l.setInnerHTML(HTMLwriter.getDashboard(category+" Dashboard",user_icons));
		}
		RootPanel.get(content).setVisible(true);

	}



	public void loadsidebar()
	{
		if(user.isAdmin())
		{ 

			HTMLwriter.makesidebar(admin_sidebar);
		}
		else
		{

			HTMLwriter.makesidebar(user_sidebar);

		}

	}


}
