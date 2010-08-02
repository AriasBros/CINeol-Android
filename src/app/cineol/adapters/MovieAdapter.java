package app.cineol.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class MovieAdapter extends BaseAdapter {
	
	ArrayAdapter<String> titlesHeader;
	ArrayList<Adapter> sections = new ArrayList<Adapter>();

	public MovieAdapter (Context context, int headerLayout) {
		this.titlesHeader = new ArrayAdapter<String>(context, headerLayout);
	}
	
	public void addSection(String title, Adapter section) {
		if (title != null)
			titlesHeader.add(title);
		
		sections.add(section);
	}
	
	public int getCount() {
		return 2 + 2 + 1 + sections.get(2).getCount();
	}

	public Object getItem(int position) {
		Object item = null;
		
		if (position == 0 || position == 1)
			item = sections.get(0).getItem(position);
		
		else if (position == 2)
			item = titlesHeader.getItem(0);
	
		else if (position == 3)
			item = sections.get(1).getItem(0);
		
		else if (position ==  4)
			item = titlesHeader.getItem(1);
		
		else
			item = sections.get(2).getItem(position - 5);
		
		return item;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		
		if (position == 0 || position == 1)
			view = sections.get(0).getView(position, convertView, parent);
		
		else if (position == 2)
			view = titlesHeader.getView(0, convertView, parent);
	
		else if (position == 3)
			view = sections.get(1).getView(0, convertView, parent);
		
		else if (position ==  4)
			view = titlesHeader.getView(1, convertView, parent);
		
		else
			view = sections.get(2).getView(position - 5, convertView, parent);

		return view;
	}

    public int getViewTypeCount() {  
        int total = 1;  
        
        for(Adapter adapter : this.sections)  
            total += adapter.getViewTypeCount();  
        
        return total;  
    }  
  
    public int getItemViewType(int position) {  
    	
		int type = -1;
		
		if (position == 0 || position == 1)
			type = sections.get(0).getItemViewType(position);
		
		else if (position == 2)
			type = titlesHeader.getItemViewType(0);
	
		else if (position == 3)
			type = sections.get(1).getItemViewType(0);
		
		else if (position ==  4)
			type = titlesHeader.getItemViewType(1);
		
		else
			type = sections.get(2).getItemViewType(position - 5);

		return type;
    }  
}
