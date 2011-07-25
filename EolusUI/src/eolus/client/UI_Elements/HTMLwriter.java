/*
 * Copyright 2010 Konstantinos Tsakalozos
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the EUPL (the Licence);
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in  writing, software
 * distributed under the Licence is distributed on an AS IS basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eolus.client.UI_Elements;


import java.util.ArrayList;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;


public class HTMLwriter
{   


	public static void makeheader(String title, String container, String content)
	{

		HTML h = new HTML();
		h.setHTML(getTableBegining(title)+
				"<div id=\"paging\" style=\"left_margin_40\"></div>\n"+
				"<div id="+content+"></div>"+getTableEnding());

		RootPanel.get(container).clear();
		RootPanel.get(container).add(h);


	}

	public static void makesidebar(ArrayList<String[]> data)
	{

		Element l = RootPanel.get("IndexSidebar").getElement();
		l.setInnerHTML(HTMLwriter.getSidebar(data));      
		RootPanel.get("IndexSidebar").setVisible(true);
	}


	public static Label createLabel(String text, String title)
	{
		Label l = new Label(text);
		l.setTitle(title);
		return l;
	}

	public static HTML getSystemMessage(String color, String message)
	{
		HTML temp = new HTML();
		temp.setStyleName("system_messages "+color);
		temp.setHTML("<span class=\"ico\"></span><strong class=\"system_title\">"+message+"</strong>");
		return temp;
	}

	public static void clearLabel(Label l)
	{
		l.setText("");
		l.setVisible(false);
		l.setStyleName("system");
	}


	public static String getSidebar(ArrayList<String[]> data)
	{
		StringBuilder sidebar = new StringBuilder();

		sidebar.append(
				getTableBegining("Sidebar Menu") +
		"<ul class=\"sidebar_menu\">\n");


		for(int i=0 ; i< data.size() ; i++ )
			if( i != data.size()-1 )
				sidebar.append("  <li><a href=\"#"+data.get(i)[0]+"\"><strong>"+data.get(i)[1]+"</strong></a></li>\n");
			else
				sidebar.append("  <li class=\"last\"><a href=\"#"+data.get(i)[0]+"\"><strong>"+data.get(i)[1]+"</strong></a></li>\n");
		sidebar.append("</ul>\n" +
				getTableEnding());

		return sidebar.toString();
	}


	public static String getDashboard(String title,ArrayList<String[]> data)
	{
		StringBuilder dashboard = new StringBuilder();
		dashboard.append(
				getTableBegining(title) +
				"<div class=\"dashboard_menu_wrapper\">\n" +
		" <ul class=\"dashboard_menu\">\n");

		for(String[] row : data )
			dashboard.append("  <li><a href=\"#"+row[0]+"\" class=\""+row[1]+"\"><span>"+row[2]+"</span></a></li>\n");

		dashboard.append(" </ul>\n" + 
				"</div>\n" + 
				getTableEnding());

		return dashboard.toString();
	}

	public static String getTableBegining(String title)
	{
		return
		"<div class=\"title_wrapper\">\n" +
		" <h2>"+title+"</h2>\n" +
		" <span class=\"title_wrapper_left\"></span>\n" +
		" <span class=\"title_wrapper_right\"></span>\n" +
		"</div>\n" +
		"<div class=\"section_content\">\n" +
		" <div class=\"sct\">\n" +
		"  <div class=\"sct_left\">\n" +
		"   <div class=\"sct_right\">\n" +
		"    <div class=\"sct_left\">\n" +
		"     <div class=\"sct_right\">\n";
	}

	public static String getTableEnding()
	{
		return
		"     </div>\n" +
		"    </div>\n" +
		"   </div>\n" +
		"  </div>\n" +
		" </div>\n" +
		" <span class=\"scb\"><span class=\"scb_left\"></span><span class=\"scb_right\"></span></span>\n" +
		"</div>\n";
	}

	public static String getInnerTableBegining(String title)
	{
		return
		"<div class=\"title_wrapper2\">\n" +
		" <h2>"+title+"</h2>\n" +
		" <span class=\"title_wrapper_left2\"></span>\n" +
		" <span class=\"title_wrapper_right2\"></span>\n" +
		"</div>\n" ;
		//+
		//		"<div class=\"section_content\">\n" +
		//		" <div class=\"sct\">\n" +
		//		"  <div class=\"sct_left2\">\n" +
		//		"   <div class=\"sct_right2\">\n" +
		//		"    <div class=\"sct_left2\">\n" +
		//		"     <div class=\"sct_right2\">\n";
	}

	public static String getInnerTableEnding()
	{
		return 
		//		"     </div>\n" +
		//		"    </div>\n" +
		//		"   </div>\n" +
		//		"  </div>\n" +
		//		" </div>\n" +
		" <span class=\"scb\"><span class=\"scb_left\"></span><span class=\"scb_right\"></span></span>\n" ;
		//		+
		//		"</div>\n";
	}

}
