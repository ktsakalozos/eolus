package eolus.client;

import java.sql.Timestamp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;


public class MyTimer { /*singleton*/

	private static Timer t;


	public static void setTimer(Timer a)
	{
		if(t!=null)
			t.cancel();
		t=a;

	}

	public static Timer getTimer(){return t;}


	public static Label stamp()
	{ 


		java.util.Date date= new java.util.Date();

		return new Label("Last updated on "+ new Timestamp(date.getTime()));

	}

}

