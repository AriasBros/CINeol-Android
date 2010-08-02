package app.cineol.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import app.cineol.rows.DetailRowView;

public class DetailsAdapter extends ArrayAdapter<String> {
	
	static private final int YEAR_ROW 				= 0;
	static private final int COUNTRY_ROW 			= 1;
	static private final int FORMAT_ROW 			= 2;
	static private final int ORIGINAL_TITLE_ROW 	= 3;
	static private final int ORIGINAL_PREMIER_ROW 	= 4;
	static private final int SPAIN_TAKINGS_ROW 		= 5;
	static private final int USA_TAKINGS_ROW 		= 6;
	
	private int XMLResource;
	
	public DetailsAdapter(Context context, int textViewResourceId, List<String> strings) {
		super(context, textViewResourceId, strings);
		
		XMLResource = textViewResourceId;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		DetailRowView rowView;
		
        if (convertView == null) {
    		Activity activity = (Activity) getContext();
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(XMLResource, null);
            
            rowView = new DetailRowView(convertView);
            convertView.setTag(rowView);
        }
        else
        	rowView = (DetailRowView)convertView.getTag();
        
        String info = this.getItem(position);
        if (info != null) {        	
        	switch (position) {
        		case DetailsAdapter.YEAR_ROW:
                	rowView.getDetailView().setText("Año");
        			break;
        			
        		case DetailsAdapter.COUNTRY_ROW:
                	rowView.getDetailView().setText("País");
        			break;

        		case DetailsAdapter.FORMAT_ROW:
        			rowView.getDetailView().setText("Formato");
        			break;

        		case DetailsAdapter.ORIGINAL_TITLE_ROW:
        			rowView.getDetailView().setText("Título original");
        			break;

        		case DetailsAdapter.ORIGINAL_PREMIER_ROW:
        			rowView.getDetailView().setText("Estreno mundial");
        			break;

        		case DetailsAdapter.SPAIN_TAKINGS_ROW:
        			rowView.getDetailView().setText("Recaudación\nEspaña");
        			break;

        		case DetailsAdapter.USA_TAKINGS_ROW:
        			rowView.getDetailView().setText("Recaudación\nUSA");
        			break;
        	}
        	
        	rowView.getInfoView().setText(info);
        }
        
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
