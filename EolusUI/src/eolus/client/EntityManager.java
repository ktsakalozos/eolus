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

package eolus.client;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kristian 
 */

/**
 * 
 * This class simulates an entity manager on the client side of a GWT project by 
 * writting data (with the persist() method) in a hidden div tag on the html page.
 * The data can later be retrieved with the get method. The object types that can
 * be stored are: String
 */
public class EntityManager {

	private static FlexTable em = null;

	public static void init() {
		RootPanel root = RootPanel.get("EntityManager");
		root.setVisible(false);
		if( "".equals(root.getElement().getInnerHTML()) ) {
			em = new FlexTable();
			root.add(em);
		}
	}

	public static void persist(String name, Object o) {
		/*
      //protupo gia tin dimiourgia kwdika pou na apothikeuei antikeimena tupou "Antikeimeno".
      //analogi diadikasia prepei na ulopoithei kai stin sinartisi get


      if(o instanceof Antikeimeno) {
         Antikeimeno antik = (Antikeimeno) o; 
         int rows = em.getRowCount();
         int i=0;
         for(i=0 ; i<rows ; i++)
            if( em.getText(i, 0).equals(name) )
               break;
         em.setText(i, 0, name);
         em.setWidget(i, 1, antik);
      }
		 */

		if(o instanceof String) {
			Label l = new Label((String)o);
			int rows = em.getRowCount();
			int i=0;
			for(i=0 ; i<rows ; i++)
				if( em.getText(i, 0).equals(name) )
					break;
			em.setText(i, 0, name);
			em.setWidget(i, 1, l);
		}
	}

	public static Object get(String name) {
		int rows = em.getRowCount();
		int i=0;
		for( ; i<rows ; i++)
			if( em.getText(i, 0).equals(name) )
				break;
		if( i==rows )
			return null;
		Widget w = em.getWidget(i, 1);
		if(w instanceof Label)
		{
			Label l = (Label)w;
			return l.getText();
		}
		return null;
	}

	public static void remove(String name) {
		int rows = em.getRowCount();
		int i=0;
		for( ; i<rows ; i++)
			if( em.getText(i, 0).equals(name) )
				break;
		if( i!=rows )
			em.removeRow(i);
	}

	public static void clear() {
		em.removeAllRows();
	}
}
