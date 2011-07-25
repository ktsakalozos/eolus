package eolus.client.UI_Elements;


import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;



public class MyDropDown extends Composite
{

	private ListBox list = new ListBox();
	private VerticalPanel widget = new VerticalPanel();
	private Button submit;


	public VerticalPanel getWidget(){return widget;}

	public MyDropDown(String[] options,String label){

		list.addItem("Select a user");
		int i=0,size=options.length;
		while(i<size)  {

			if(options[i].equals("public")){
				i++;
				continue;
			}

			list.addItem(options[i]);
			i++;
		}
		widget.add(new Label(label));
		widget.add(list);

		initWidget(widget); 

	}

	public MyDropDown(String[] options,String label,String title){

		submit = new Button(title);
		int i=0,size=options.length;
		while(i<size)
		{
			list.addItem(options[i]);
			i++;
		}

		widget.add(new Label(label));
		widget.add(list);
		widget.add(submit);
		initWidget(widget); 

	}

	public Button getButton(){ return submit;}
	public ListBox getList(){return list;}

	public String getSelected()
	{
		if(list.getSelectedIndex()<0)
			return null;

		return list.getValue(list.getSelectedIndex());
	}



}
