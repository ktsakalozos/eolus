package eolus.client;



import java.util.ArrayList;
import java.util.HashMap;
import com.google.gwt.xml.client.XMLParser; 
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;

import com.google.gwt.user.client.ui.ListBox;


public class Parser { /* class that parses and validates*/


	public static HashMap<String,Object> parseNetworkInfo(String xml){
		HashMap<String,Object> map =  new HashMap<String,Object>();
		Document doc = XMLParser.parse(xml);
		String[] myips={};


		String name = doc.getElementsByTagName("NAME").item(0).getFirstChild().getNodeValue();
		String state = doc.getElementsByTagName("STATE").item(0).getFirstChild().getNodeValue();
		String id= doc.getElementsByTagName("ID").item(0).getFirstChild().getNodeValue();

		map.put("ID",id);
		map.put("Name",name);
		map.put("IsPublic",name.equals("Public") );
		map.put("State",state);


		NodeList ips= doc.getElementsByTagName("IP");



		int i=0,size= ips.getLength();
		while(i<size){

			myips[i]= ips.item(i).getFirstChild().getNodeValue();

			i++;	
		}
		map.put("IPS",myips);



		return map;

	}



	/**
	 * creates character separated string from elements in listbox.
	 *
	 * @Author Caroline
	 * @param lb the Listbox
	 * @param p2 thes separating character, for example, the comma
	 * @return the generated string
	 */




	public static String createSeparatedListFromListBox(ListBox lb,String separator)
	{
		String s="";
		int i=0, size = lb.getItemCount();
		while(i<size)
		{
			if(i!=0)
				s+=separator;
			s+= lb.getItemText(i);	
			i++;	
		}
		return s;
	}


	public static String[] createArrayFromListBox(ListBox lb)
	{
		ArrayList<String> s = new ArrayList<String>();
		int i=0, size = lb.getItemCount();
		while(i<size)
		{


			s.add(lb.getItemText(i));	
			i++;	
		}
		//return s;
		i=0;
		size=s.size();
		String[] result = new String[size];
		while(i<size)
		{
			result[i]=s.get(i);
			i++;
		}


		return result;
	}

	public static Boolean isvalidIPNumber(String n)
	{



		if(n.substring(n.length()-1, n.length()).equals("."))
			return false;
		int i=-1;
		try{
			i = Integer.parseInt( n );}

		catch (NumberFormatException e){

			return false;
		}

		if ( (i < 0) || (i > 254) )
		{
			return false;
		}
		return true;
	}	
	public static Boolean isvalidIP(String ipAddress){

		String[] parts = ipAddress.split("\\.");


		if ( parts.length != 4 )
		{
			return false;
		}

		for ( String s : parts )
		{ 
			int i=-1;
			try{ i = Integer.parseInt( s );}

			catch (NumberFormatException e){

				return false;
			}

			if ( (i < 0) || (i > 254) )
			{
				return false;
			}
		}

		return true;
	}


	public static Boolean isvalidName(String name){

		if (name.equals(""))
			return false;

		if(	name.contains(" "))
			return false;





		return true;	
	}


}
