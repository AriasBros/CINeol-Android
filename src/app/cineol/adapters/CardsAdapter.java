package app.cineol.adapters;

import java.util.List;

import net.cineol.model.Movie;
import net.cineol.model.Person;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import app.cineol.rows.CreditRowView;

public class CardsAdapter extends ArrayAdapter<Movie> {
	
	private int XMLResource;

	public CardsAdapter(Context context, int textViewResourceId, List<Movie> objects) {
		super(context, textViewResourceId, objects);
		XMLResource = textViewResourceId;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		return parent;
	}
}
