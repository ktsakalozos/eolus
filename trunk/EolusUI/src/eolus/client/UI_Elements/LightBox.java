package eolus.client.UI_Elements;



import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LightBox extends Composite {
	private PopupPanel glass = new PopupPanel(false);
	private VerticalPanel v = new VerticalPanel();
	private VerticalPanel content = new VerticalPanel();

	public PopupPanel getPopup(){return glass;}
	public VerticalPanel getPanel(){return content;}
	public void clear()
	{

		content.clear();
	}
	public void show()
	{

		show(Window.getClientWidth()/2, Window.getScrollTop() +200);	
	}

	public void autoclose()
	{

		Timer once = new Timer()
		{
			@Override
			public void run() 
			{
				glass.hide();
				this.cancel();

			}
		};
		once.schedule(1000);
	}


	public void close()
	{
		glass.hide();
	}

	public void show(int x,int y) /*show at a specific offset from the top left of the page */
	{

		glass.setPopupPosition(x, y);	
		glass.show();

	}

	public void add(Widget w)
	{

		content.add(w); 

	}
	public LightBox(){

		glass.setGlassEnabled(true);
		glass.setModal(true);	
		//Button button = new Button();	
		HTML button = new HTML("<span class=x-button></span>");
		v.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		v.add(button);
		//button.setStyleName("x-button");   
		glass.add(v); 
		v.add(content);


		button.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{

				glass.hide();

			}
		});



	}

}
