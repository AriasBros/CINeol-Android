package app.cineol.adapters;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class SectionsAdapter extends BaseAdapter {
	
    private final static int TYPE_SECTION_HEADER = 0;
    final Map<String, BaseAdapter> sections = new LinkedHashMap<String, BaseAdapter>();  
	ArrayAdapter<String> titlesSections;

	public SectionsAdapter (Context context, int headerLayout) {
		this.titlesSections = new ArrayAdapter<String>(context, headerLayout);
	}
	
	public void addSection(String titleSection, BaseAdapter adapterSection) {
		titlesSections.add(titleSection);
		sections.put(titleSection, adapterSection);
	}
	
	public int getCount() {
        // total together all sections, plus one for each section header  
        int total = 0;  
        for(Adapter adapter : this.sections.values())  
            total += adapter.getCount() + 1;  
        return total;  
	}

	public Object getItem(int position) {
        for(Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount() + 1;  
  
            // check if position inside this section  
            if(position == 0) return section;  
            if(position < size) return adapter.getItem(position - 1);  
  
            // otherwise jump into next section  
            position -= size;  
        }  
        return null;  
	}
	
	@SuppressWarnings("unchecked")
	public void remove(Object object) {
        for(Object section : this.sections.values()) {
        	((ArrayAdapter)section).remove(object);
        }
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        int sectionnum = 0;  
        for(Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount() + 1;  
  
            // check if position inside this section  
            if(position == 0) return titlesSections.getView(sectionnum, convertView, parent);  
            if(position < size) return adapter.getView(position - 1, convertView, parent);  
  
            // otherwise jump into next section  
            position -= size;  
            sectionnum++;  
        }  
        return null;  
	}
	
    public int getViewTypeCount() {  
        // assume that headers count as one, then total all sections  
        int total = 1;  
        for(Adapter adapter : this.sections.values())  
            total += adapter.getViewTypeCount();  
        return total;  
    }  
  
    public int getItemViewType(int position) {  
        int type = 1;  
        for(Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount() + 1;  
  
            // check if position inside this section  
            if(position == 0) return TYPE_SECTION_HEADER;  
            if(position < size) return type + adapter.getItemViewType(position - 1);  
  
            // otherwise jump into next section  
            position -= size;  
            type += adapter.getViewTypeCount();  
        }  
        return -1;  
    }  
    
    public boolean areAllItemsSelectable() {  
        return false;  
    }  
  
    public boolean isEnabled(int position) {  
        int sectionnum = 0;  
        for(Object section : this.sections.keySet()) {  
        	BaseAdapter adapter = sections.get(section);  
            int size = adapter.getCount() + 1;  
  
            // check if position inside this section  
            if(position == 0) return false;  
            if(position < size) return adapter.isEnabled(position - 1); 
  
            // otherwise jump into next section  
            position -= size;  
            sectionnum++;  
        }  
        
        return false;  
    }  
}