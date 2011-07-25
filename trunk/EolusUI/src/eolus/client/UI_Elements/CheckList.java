package eolus.client.UI_Elements;
import java.util.ArrayList;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.CheckBox;
public class CheckList
{
	private VerticalPanel p = new VerticalPanel();	
	private VerticalPanel widget = new VerticalPanel();	
	private ScrollPanel s;
	protected ArrayList<CheckBox> items = new ArrayList<CheckBox>();

	public VerticalPanel getCheckList()
	{
		return widget;
	}

	public String[] getChecked()
	{
		String[] s={};
		java.util.Iterator<CheckBox> it = items.iterator();
		while(it.hasNext())
		{
			CheckBox cb = it.next();
			if(cb.getValue())
			{
				s[s.length]=cb.getText();
			}
		}



		return s;
	}
	public CheckList(String[] elements,String title)
	{

		s = new ScrollPanel(p);
		p.add(new Label(title));
		int i=0,size =elements.length;
		while(i<size)
		{
			CheckBox cb = new CheckBox(elements[i]);
			p.add(cb);
			items.add(cb);
			i++;
		}
		widget.add(s);

	}




}
