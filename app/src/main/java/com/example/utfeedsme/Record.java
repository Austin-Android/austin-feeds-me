package com.example.utfeedsme;

/*
 * Database code derived from Lars Vogel
 * http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */

import java.sql.Time;

import android.util.Log;

public class Record {
	  private long id;
	 // private String comment;
	  private String location;
	  private int startTime;
	  private int endTime;
	  private String foodType;
	  
	  private static final String TAG = "Columns";
	  private final int MINS_PER_HOUR = 60;

	  public long getId() {
		Log.d(TAG, "getting ID");  
	    return id;
	  }

	  public void setId(long id) {
		Log.d(TAG, "setting ID"); 
	    this.id = id;
	  }
	  
	  public String getLocation() {
		  return location;
	  }
	  
	  public void setLocation(String location) {
		this.location = location;   
	  }
	  
	  public int getStartTime() {
		  return startTime;
	  }
	  
	  public void setStartTime(int time) {
		  startTime = time;
	  }
	  
	  public int getEndTime() {
		  return endTime;
	  }
	  
	  public void setEndTime(int time) {
		  endTime = time;
	  }
	  
	  public String getFood() {
		  return foodType;
	  }
	  
	  public void setFood(String foodType) {
		  this.foodType = foodType;
	  }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
		  int realStartHour, realEndHour, startHour, endHour, startMin, endMin; 
		  realStartHour = startTime / MINS_PER_HOUR; // time = total number of minutes passed
		  startHour = (realStartHour > 12) ? (realStartHour - 12) : realStartHour;
		  if (realStartHour == 0) { // 12:00 am
			  startHour = 12;
		  }
		  String ampmStart = (realStartHour < 12) ? "am" : "pm";
		  startMin = startTime % MINS_PER_HOUR;
		  String smRep = (startMin < 10) ? ("0" + startMin) : Integer.toString(startMin);
		  
		  realEndHour = endTime / MINS_PER_HOUR; // time = total number of minutes passed
		  endHour = (realEndHour > 12) ? (realEndHour - 12) : realEndHour;
		  if (realEndHour == 0) { // 12:00 am
			  endHour = 12;
		  }
		  String ampmEnd = (realEndHour < 12) ? "am" : "pm";
		  endMin = endTime % MINS_PER_HOUR;
		  String emRep = (endMin < 10) ? ("0" + endMin) : Integer.toString(endMin);
		  String rStart = startHour + ":" + smRep + " " + ampmStart + " - ";
		  String rEnd = + endHour + ":" + emRep + " " + ampmEnd;
		  return rStart + "\t\t" + location + "\t\t" + foodType + "\n" + rEnd;
	  }
}
