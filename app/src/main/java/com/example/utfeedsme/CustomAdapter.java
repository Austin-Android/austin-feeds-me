package com.example.utfeedsme;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/*
 * ParseObject gameScore = new ParseObject("FoodEvent");
				gameScore.put("event", event_name.getText().toString());
				gameScore.put("start_time", event_start_time.getText().toString());
				gameScore.put("end_time", event_end_time.getText().toString());
				gameScore.put("date", event_date.getText().toString());
				gameScore.put("where", event_where.getText().toString());
				gameScore.put("food", event_food.getText().toString());
				gameScore.saveInBackground();
 */

public class CustomAdapter extends ParseQueryAdapter<ParseObject> {

    public CustomAdapter(Context context) {
        // Use the QueryFactory to construct a PQA that will only show
        // Todos marked as high-pri
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("FoodEvent");
                //query.whereEqualTo("highPri", true);
                return query;
            }
        });
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.urgent_item, null);
        }

        super.getItemView(object, v, parent);

        /*
        // Add and download the image
        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.icon);
        ParseFile imageFile = object.getParseFile("Image");
        if (imageFile != null) {
            todoImage.setParseFile(imageFile);
            todoImage.loadInBackground();
        }
		*/

        // Add the title view
        TextView foodTextView = (TextView) v.findViewById(R.id.food);
        foodTextView.setText(object.getString("food"));
        
        TextView locationTextView = (TextView) v.findViewById(R.id.location);
        locationTextView.setText(object.getString("where"));
        
        TextView dateTextView = (TextView) v.findViewById(R.id.date);
        dateTextView.setText(object.getString("date"));
        
        TextView startTextView = (TextView) v.findViewById(R.id.start_time);
        startTextView.setText(object.getString("start_time"));

        // Add a reminder of how long this item has been outstanding
        TextView endTextView = (TextView) v.findViewById(R.id.end_time);
        endTextView.setText(object.getString("end_time"));
        return v;
    }

}
