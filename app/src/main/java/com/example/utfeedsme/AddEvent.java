package com.example.utfeedsme;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.parse.ParseObject;
//import android.widget.TextView;

public class AddEvent extends FragmentActivity {

	private static final String TAG = "addevent";
	
	private static String mDate;
	private static String mStartTime;
	private static String mEndTime;

	// private final int MINS_PER_DAY = 1440;
	// protected RecordsDataSource dataSource;
	Button add_event;
	EditText event_name, event_where, event_food;
	Button event_date, event_start_time, event_end_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("addevent", "entered oncreate for addevent");
		Log.d(TAG, "alsjdlahsdjvhasjhgas");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_event);
		
		event_name = (EditText) findViewById(R.id.add_event_name);
		event_where = (EditText) findViewById(R.id.add_event_where);
		event_date = (Button) findViewById(R.id.add_event_date);
		event_start_time = (Button) findViewById(R.id.add_event_start_time);
		event_end_time = (Button) findViewById(R.id.add_event_end_time);
		event_food = (EditText) findViewById(R.id.add_event_food);
		Button add_event = (Button) findViewById(R.id.add_event_btn);
		
		if (savedInstanceState != null) {
			event_date.setText(savedInstanceState.getString("date"));
			event_start_time.setText(savedInstanceState.getString("startTime"));
			event_end_time.setText(savedInstanceState.getString("endTime"));
		}

		event_start_time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimePickerFragment(0);
				newFragment.show(getSupportFragmentManager(), "startTimePicker");
			}
		});

		event_end_time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimePickerFragment(1);
				newFragment.show(getSupportFragmentManager(), "endTimePicker");
			}
		});
		
		event_date.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});

		add_event.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				Log.v("addevent", "event: " + event_name.getText().toString()+
						", " + event_where.getText().toString() +
						", " + event_start_time.getText().toString() +
						", " + event_end_time.getText().toString() +
						", " + event_food.getText().toString());
				*/		
				ParseObject gameScore = new ParseObject("FoodEvent");
				gameScore.put("event", event_name.getText().toString());
				gameScore.put("start_time", event_start_time.getText().toString());
				gameScore.put("end_time", event_end_time.getText().toString());
				gameScore.put("date", event_date.getText().toString());
				gameScore.put("where", event_where.getText().toString());
				gameScore.put("food", event_food.getText().toString());
				gameScore.saveInBackground();
				
				
			}
		});
	}

	public class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {

		int buttonId;
		
		

		public TimePickerFragment (int i) {
			super();
			buttonId = i;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			//Code based on Android documentation example
			// Use the current time as the default values for the picker
			Log.d(TAG, "created a new TimePickerFragment");
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			//change text to hour..
			//see what calls this
			// Do something with the time chosen by the user
			Log.d(TAG, "in onTimeSet");
			if (buttonId == 0) {
				Button startButton = (Button) findViewById(R.id.add_event_start_time);
				mStartTime = new StringBuilder().append(String.valueOf(hourOfDay))
				.append(":").append(String.valueOf(minute)).toString();
				startButton.setText(mStartTime);
			}
			else {
				Button endButton = (Button) findViewById(R.id.add_event_end_time);
				mEndTime = new StringBuilder().append(String.valueOf(hourOfDay))
				.append(":").append(String.valueOf(minute)).toString();
				endButton.setText(mEndTime);
			}
		}
	}

	public class DatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {
		
		public DatePickerFragment() {
			super();
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			Button dateButton = (Button) findViewById(R.id.add_event_date);
			mDate = new StringBuilder().append(String.valueOf(month))
					.append(" ").append(String.valueOf(day)).append(", ").append(String.valueOf(year)).toString();
			dateButton.setText(mDate);
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString("date", mDate);
		outState.putString("startTime", mStartTime);
		outState.putString("endTime", mEndTime);
	}
}