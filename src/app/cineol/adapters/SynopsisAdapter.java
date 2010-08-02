package app.cineol.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

public class SynopsisAdapter extends ArrayAdapter<String> {

	public SynopsisAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
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