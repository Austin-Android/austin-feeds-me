package com.example.utfeedsme;

/*
 * Database code derived from Lars Vogel
 * http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */

import java.util.List;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class HappeningNow extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.happening_now);
		
		 ParseQueryAdapter.QueryFactory<ParseObject> factory =
			     new ParseQueryAdapter.QueryFactory<ParseObject>() {
			       public ParseQuery<ParseObject> create() {
			         ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("FoodEvent");
			         query.orderByAscending("event");
			         return query;
			       }
			     };

		ParseQueryAdapter<ParseObject> mainAdapter = new ParseQueryAdapter<ParseObject>(this, factory);
		mainAdapter.setTextKey("event");
		setListAdapter(mainAdapter);
	}
}
