package eolus.client;



public class UserInfo
{

	private static UserInfo ref;
	private String username;
	private Boolean isAdmin;
	private UserInfo() //private constructor for the implementation of the singleton pattern
	{
		// no code needed
	}

	public static UserInfo getUserInfo()
	{
		if (ref == null){
			// it's ok, we can call this constructor
			ref = new UserInfo();
		}
		return ref;
	}


	public void setUsername(String a){username=a;}
	public void setIsAdmin(Boolean a){isAdmin=a;}

	public String getUsername(){return username;}
	public Boolean isAdmin(){return isAdmin;  }


}
