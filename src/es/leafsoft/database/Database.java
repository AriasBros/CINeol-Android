package es.leafsoft.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class Database {
	
	protected SQLiteDatabase database;

    protected boolean isItemStored(long itemId, String column, String table) {
    	String[] colums = {"count(" + column + ")"};
		String where = column + " = " + itemId;
    	
    	Cursor cursor =  database.query(table, colums, where, null, null, null, null);
    	
    	boolean result = false;
    	if (cursor != null) {
    		cursor.moveToFirst();
    		result = (cursor.getInt(0) > 0);
    		cursor.close();
    	}
    	
    	return result;
    }
    
    protected boolean isItemStored(String itemId, String column, String table) {
    	String[] colums = {"count(" + column + ")"};
		String where = column + " like " + "\"" + itemId + "\"";
    	
    	Cursor cursor =  database.query(table, colums, where, null, null, null, null);
    	
    	boolean result = false;
    	if (cursor != null) {
    		cursor.moveToFirst();
    		result = (cursor.getInt(0) > 0);
    		cursor.close();
    	}
    	
    	return result;
    }
    
    protected boolean deleteItems(long itemId, String column, String table) {
		String where = column + " = " + itemId;
	   	int result = database.delete(table, where, null);
		
		return (result > 0);
    }
    
    protected Cursor getCursorForItem(long itemId, String colum, String table) {
       	String where = colum + " = " + itemId;

    	return database.query(table, null, where, null, null, null, null);
    }
}
