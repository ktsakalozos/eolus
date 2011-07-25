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


import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;


import eolus.client.EntityManager;


public class MyHandlers {


	public static class UserActionHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			Label l = (Label)event.getSource();
			if( l.getTitle().startsWith("Modify") ) {
				EntityManager.persist("ModifyUser", l.getTitle().substring(7));
				History.newItem("ModifyUser");
			}
			else {
				EntityManager.persist("DeleteUser", l.getTitle().substring(7));
				History.newItem("DeleteUser");
			}
		}
	}



	public static class FocusAndBlurHandler implements FocusHandler, BlurHandler
	{
		private final Label l;

		public FocusAndBlurHandler(Label l) {
			this.l = l;
		}

		public void onFocus(FocusEvent event) {
			if( "DONE".equals(l.getText()) )
				l.setVisible(false);         
		}

		public void onBlur(BlurEvent event) {
			TextBox tb = (TextBox) event.getSource();
			if( (!l.isVisible() && !"".equals(tb.getText()) )|| "system positive".equals(l.getStyleName()) )
				setPositive(l,"DONE");
		}
	}

	public static class NewUserNameHandler implements KeyUpHandler {

		private final Label l;

		public NewUserNameHandler(Label l) {
			this.l = l;
		}

		public void onKeyUp(KeyUpEvent event) {
			TextBox tb = (TextBox) event.getSource();
			if( tb.getText().length()<4 )
				setNegative(l,"Username must be at least 4 characters long");
			else if( l.isVisible() && "system negative".equals(l.getStyleName()) )
			{
				setPositive(l,"OK !!");
				Timer t = new Timer() {
					@Override
					public void run() {
						if( "OK !!".equals(l.getText()))
							l.setVisible(false);
					}
				};
				t.schedule(2000);
			}
		}
	}

	public static class ConfirmPassHandler implements KeyUpHandler {

		private final PasswordTextBox pass;
		private final Label l;
		private final HTML w;

		public ConfirmPassHandler(Label l,PasswordTextBox pass, HTML w)
		{
			this.l = l;
			this.pass = pass;
			this.w = w;
		}

		public void onKeyUp(KeyUpEvent event) {
			String pass1 = pass.getText();
			String pass2 = ((PasswordTextBox)event.getSource()).getText();
			if( w != null && event.getNativeKeyCode() == KeyCodes.KEY_ENTER )
				DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), w);
			if( pass2.length() > pass1.length() )
				setNegative(l,"too long");
			else if( pass2.length() < pass1.length() )
				setNegative(l,"too short");
			else if( !pass2.equals(pass1) )
				setNegative(l,"NOT a match");
			else
				setPositive(l,"DONE");
		}
	}

	public static class NewPassHandler implements KeyUpHandler {

		private final Label l;
		private final Label l2;
		private final TextBox tb;

		public NewPassHandler(Label l, TextBox tb, Label l2) {
			this.l = l;
			this.l2 = l2;
			this.tb = tb;
		}

		public void onKeyUp(KeyUpEvent event) {
			String pass = ((PasswordTextBox) event.getSource()).getText();
			if( l2.isVisible() )
				l2.setVisible(false);
			if( pass.length() < 6 || pass.equals(tb.getText()) )
			{
				setNegative(l,"VERY weak");
			}
			else
			{ 
				int c[] = {0,0,0,0};
				for(int i=0 ; i<pass.length() ; i++)
				{
					if( Character.isDigit(pass.charAt(i)) )
						c[0] = 1;
					else if( Character.isLowerCase(pass.charAt(i)) )
						c[1] = 1;
					else if( Character.isUpperCase(pass.charAt(i)) )
						c[2] = 1;
					else
						c[3] = 1;
				}
				int strength = c[0]+c[1]+c[2]+c[3];
				switch(strength) {
				case 1: { setNegative(l,"VERY weak"); break; }
				case 2: { setNegative(l,"weak"); break;}
				case 3: { setPositive(l,"STRONG"); break;}
				case 4: { setPositive(l,"VERY STRONG"); break;}
				}
			}

			Timer t = new Timer() {
				@Override
				public void run() {
					if( "OK !!".equals(l.getText()))
						l.setVisible(false);
				}
			};
			t.schedule(2000);
		}
	}

	public static void setNegative(Label l, String text)
	{
		l.setStyleName("system negative");
		l.setText(text);
		l.setVisible(true);
	}

	public static void setPositive(Label l, String text)
	{
		l.setStyleName("system positive");
		l.setText(text);
		l.setVisible(true);


	}
	public static void autoHide(final Label l) /* hide label after 2 seconds --caroline */
	{
		Timer once = new Timer()
		{
			@Override
			public void run() 
			{
				l.setVisible(false);
				this.cancel();

			}
		};
		once.schedule(2000); 
	}
}