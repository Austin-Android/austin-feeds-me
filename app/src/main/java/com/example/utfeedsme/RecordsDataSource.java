package com.example.utfeedsme;

/*
 * Author: Lars Vogel
 * http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RecordsDataSource {
	
	private static final String TAG  = "RecordsDataSource";
	private static final long MS_PER_DAY = 86400000;
	private static final long CST_CONVERT = 18000000;

	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
	      MySQLiteHelper.COLUMN_START_TIME, MySQLiteHelper.COLUMN_END_TIME, MySQLiteHelper.COLUMN_LOCATION,
	      MySQLiteHelper.COLUMN_FOOD,};

	  public RecordsDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public Record createRecord(int startTime, int endTime, String location, String food) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_START_TIME, startTime);
	    values.put(MySQLiteHelper.COLUMN_END_TIME, endTime);
	    values.put(MySQLiteHelper.COLUMN_LOCATION, location);
	    values.put(MySQLiteHelper.COLUMN_FOOD, food);
	    long insertId = database.insert(MySQLiteHelper.TABLE_NAME, null,
	        values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
	        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Record newRecord = cursorToRecord(cursor);
	    cursor.close();
	    return newRecord;
	  }

	  public void deleteRecord(Record record) {
	    long id = record.getId();
	    System.out.println("Record deleted with id: " + id);
	    database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_ID
	        + " = " + id, null);
	  }

	  public List<Record> getAllRecords() {
	    List<Record> records = new ArrayList<Record>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Record record = cursorToRecord(cursor);
	      records.add(record);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return records;
	  }
	  
	  public List<Record> getTimeRecords() {
		    List<Record> records = new ArrayList<Record>();
		    
		    long currentTimeMins = ((System.currentTimeMillis() - CST_CONVERT) % MS_PER_DAY) / (1000 * 60);
		    Log.d(TAG, "minutes: " + currentTimeMins);

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
		        allColumns, MySQLiteHelper.COLUMN_START_TIME + " <= " + currentTimeMins + " and " + MySQLiteHelper.COLUMN_END_TIME + " >= " + currentTimeMins, null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Record record = cursorToRecord(cursor);
		      records.add(record);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return records;
	  }
	  
	  public List<Record> getLocationRecords() {
		    List<Record> records = new ArrayList<Record>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
		        allColumns, MySQLiteHelper.COLUMN_LOCATION + " = 'GDC'", null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Record record = cursorToRecord(cursor);
		      records.add(record);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return records;
	  }

	  private Record cursorToRecord(Cursor cursor) {
	    Record record = new Record();
	    record.setId(cursor.getLong(0));
	    record.setStartTime(cursor.getInt(1));
	    record.setEndTime(cursor.getInt(2));
	    record.setLocation(cursor.getString(3));
	    record.setFood(cursor.getString(4));
	    return record;
	  }
}
