package eolus.client.Presenters;


public class IndexPresenter extends Presenter
{

	public void init()
	{
		String[] s1={"UsersManagment","User Management Tools"};
		String[] s2=  {"NetManagement","Manage Networks"};  
		String[] s3= {"VmsManagment","Manage Virtual Machines"};
		String[] s4=  {"TemplatesManagment","Manage Templates"};  
		String[] s5=   {"ScriptManagment","Manage Scripts"};
		String[] s6=  {"HostsManagment","Manage Hosts"}; 
		String[] s7= {"Clusters","Manage Clusters"};


		admin_sidebar.add(s1);
		admin_sidebar.add(s2); 
		admin_sidebar.add(s3);
		admin_sidebar.add(s4);
		admin_sidebar.add(s5);
		admin_sidebar.add(s6);
		admin_sidebar.add(s7);

		user_sidebar.add(s2);
		user_sidebar.add(s3);
		user_sidebar.add(s4);
		user_sidebar.add(s5);


		String[] i1={"UsersManagment","d1" ,"Manage Users"};
		String[] i2=  {"NetManagement","d23" ,"Manage Networks"};  
		String[] i3= {"VmsManagment","d4" ,"Manage Virtual Machines"};
		String[] i4=  {"TemplatesManagment","d20" ,"Manage Templates"};  
		String[] i5=   {"ScriptManagment","d21" ,"Manage Scripts"};
		String[] i6=  {"HostsManagment","d22" ,"Manage Hosts"}; 
		String[] i7= {"Clusters","d23" ,"Manage Clusters"};


		admin_icons.add(i1);
		admin_icons.add(i2);
		admin_icons.add(i3);
		admin_icons.add(i4);
		admin_icons.add(i5);
		admin_icons.add(i6);
		admin_icons.add(i7);


		user_icons.add(i2);
		user_icons.add(i3);
		user_icons.add(i4);
		user_icons.add(i5);



		if(user.isAdmin())
			category ="Eolus Adminstration Panel";
		else
			category ="Eolus User Panel";
	}

	public void reload()
	{

	}

}
