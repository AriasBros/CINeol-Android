package es.leafsoft.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class DAAdapter<T> extends ArrayAdapter<T> {

	protected ArrayList<T> items;
	protected int xmlResource;
	protected boolean areAllItemsEnabled = true;
	
	public DAAdapter(Context context, int resource, ArrayList<T> objects) {
		super(context, resource);
		items = objects;
		xmlResource = resource;
	}

	public DAAdapter(Context context, int resource, List<T> objects) {
		super(context, resource);
		items = new ArrayList<T>(objects);
		xmlResource = resource;
	}

	public ArrayList<T> getItems() {
		return items;
	}
	
	public void setItems(ArrayList<T> items) {
		this.items = items;
		this.notifyDataSetChanged();
	}

	@Override
	public void sort(Comparator<? super T> comparator) {
		Collections.sort(items, comparator);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public T getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void add(T object) {
		items.add(object);
	}

	@Override
	public void clear() {
		items.clear();
	}

	@Override
	public int getPosition(T item) {
		return items.indexOf(item);
	}

	@Override
	public void insert(T object, int index) {
		items.add(index, object);
	}

	@Override
	public void remove(T object) {
		items.remove(object);
	}
	
	public void remove(int index) {
		items.remove(index);
	}
	
	public void replace(T object, int index) {
		items.set(index, object);
	}

	public void setAreAllItemsEnabled(boolean areAllItemsEnabled) {
		this.areAllItemsEnabled = areAllItemsEnabled;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return areAllItemsEnabled;
	}

	@Override
	public boolean isEnabled(int position) {
		return areAllItemsEnabled;
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}
}