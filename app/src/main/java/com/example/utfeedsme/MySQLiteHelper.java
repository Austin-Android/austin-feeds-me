package com.example.utfeedsme;

/*
 * Author: Lars Vogel
 * http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	  public static final String TABLE_NAME = "events";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_START_TIME = "start_time";
	  public static final String COLUMN_END_TIME = "end_time";
	  public static final String COLUMN_LOCATION = "location";
	  public static final String COLUMN_FOOD = "food";

	  private static final String DATABASE_NAME = "events.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_NAME + "(" + COLUMN_ID
	      + " integer primary key autoincrement, " + COLUMN_START_TIME
	      + " int, " + COLUMN_END_TIME
	      + " int, " + COLUMN_LOCATION
	      + " text, " + COLUMN_FOOD
	      + " text);";

	  public MySQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	    onCreate(db);
	  }

}
