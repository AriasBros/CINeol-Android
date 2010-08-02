package app.cineol.adapters;

import java.util.List;

import net.cineol.model.Person;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import app.cineol.rows.CreditRowView;

public class CreditsAdapter extends ArrayAdapter<Person> {

	private int XMLResource;
	
	public CreditsAdapter(Context context, int textViewResourceId, List<Person> persons) {
		super(context, textViewResourceId, persons);
		
		XMLResource = textViewResourceId;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		CreditRowView rowView;
		
        if (convertView == null) {
    		Activity activity = (Activity) getContext();
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(XMLResource, null);
            
            rowView = new CreditRowView(convertView);
            convertView.setTag(rowView);
        }
        else
        	rowView = (CreditRowView)convertView.getTag();
        
        	Person person = this.getItem(position);
            rowView.getNameView().setText(person.getName());
        	rowView.getJobView().setText(person.getJob());
        
        return convertView;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}
}
