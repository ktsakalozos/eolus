package eolus.client.UI_Elements;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTML;



public class MyTable extends Composite{ //vmview = table thing

	//generic ext-Table thing

	private FlexTable widget= new FlexTable();
	private VerticalPanel panel = new VerticalPanel();
	private VerticalPanel content = new VerticalPanel();

	public void removeExtra(int goodsize)
	{
		while(widget.getRowCount() > goodsize+1)
			widget.removeRow(goodsize+1);

	}
	public FlexTable getTable(){return widget;}

	public void removeAll(){

		while( (widget.getRowCount()) > 1 ) //erase and refresh? :S
			widget.removeRow(1); 


	}
	public void styleRow(int row,int size){
		int j=0;
		while(j<size){
			if(row % 2 ==0)
				widget.getCellFormatter().addStyleName(row,j,"tablecell cell_first");
			else
				widget.getCellFormatter().addStyleName(row,j,"tablecell cell_second");
			j++;
		}



	}

	public static void styleRows(int start,int size,FlexTable ft)
	{

		int tsize= ft.getRowCount();
		int i=start;

		while(i<tsize)
		{
			int j=0;
			while(j<size){
				if(i % 2 ==0)
					ft.getCellFormatter().addStyleName(i,j,"tablecell cell_first");
				else
					ft.getCellFormatter().addStyleName(i,j,"tablecell cell_second");
				j++;
			}

			i++;
		}


	}
	//table - containerr
	public MyTable(String[] titles)
	{

		int i=0;

		while(i<titles.length)
		{

			widget.setWidget(0,i, new Label(titles[i]));
			i++;

		}
		for( i=0 ; i<titles.length ; i++)
			widget.getCellFormatter().addStyleName(0,i,"tablecell cell_head");


		initWidget(widget); 
	}

	public MyTable()
	{
		initWidget(widget); 
	}

	public void unsetHeaders()
	{
		int i;
		for( i=0 ; i<widget.getCellCount(0) ; i++)
			widget.getCellFormatter().removeStyleName(0,i,"tablecell cell_head");

	}
	public void setHeaders(String[] titles)
	{

		int i=0;

		while(i<titles.length)
		{

			widget.setWidget(0,i, new Label(titles[i]));
			i++;

		}
		for( i=0 ; i<titles.length ; i++)
			widget.getCellFormatter().addStyleName(0,i,"tablecell cell_head");
	}


	public VerticalPanel getContent()
	{

		content.addStyleName("contentTable");
		return content; 

	}

	public MyTable(String title)
	{

		panel.add(new HTML(HTMLwriter.getInnerTableBegining(title)));
		//RootPanel.get(title).add(content);
		panel.add(content);
		panel.add(new HTML(HTMLwriter.getInnerTableEnding()));
		initWidget(panel); 


	}

	public void removeRow(int number)
	{
		widget.getRowFormatter().setVisible(number,false);	
		//widget.removeRow(number);

	}


}

//////////////////


