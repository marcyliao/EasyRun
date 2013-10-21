package ca.ece.utoronto.ece1780.runningapp.view;


import ca.ece.utoronto.ece1780.runningapp.controller.RunningActivityController;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.data.Mood;
import ca.ece.utoronto.ece1780.runningapp.database.ActivityRecordDAO;
import ca.ece.utoronto.ece1780.runningapp.setting.UserSetting;
import ca.ece.utoronto.ece1780.runningapp.utility.UtilityCaculator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class SaveActivityActivity extends Activity  {
	public static final int RESULT_SAVE = 728;
	public static final int RESULT_DUMP = 142;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_activity);
		
		prepareMap();
		prepareWidgets();
	}

	private void prepareWidgets() {

		// Get the current activity record to save
		RunningActivityController controller = RunningActivityController.getInstance(getApplicationContext());
		ActivityRecord record = controller.getCurrentRecord();
		
		((TextView)findViewById(R.id.TextViewAVGSpeed)).setText(String.format("%.1f",record.getAvgSpeed()));
		((TextView)findViewById(R.id.TextViewTime)).setText(UtilityCaculator.getFormatStringFromDuration((int)(record.getTimeLength()/1000)));
		((TextView)findViewById(R.id.TextViewCalories)).setText(String.valueOf(record.getCalories()));
		((TextView)findViewById(R.id.TextViewDistance)).setText(String.format("%.2f",Double.valueOf(record.getDistance())/1000));
		
		// When the save button clicked
		findViewById(R.id.buttonSave).setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				RunningActivityController controller = RunningActivityController.getInstance(getApplicationContext());
				ActivityRecord record = controller.getCurrentRecord();
				record.setNote(((EditText)findViewById(R.id.editTextNote)).getText().toString());
				
				Spinner moodSpinner = (Spinner)findViewById(R.id.spinnerMood);
				
				if(moodSpinner.getSelectedItemPosition() == 0)
					record.setMood(Mood.HAPPY);
				else if(moodSpinner.getSelectedItemPosition() == 1)
					record.setMood(Mood.NICE);
				else if(moodSpinner.getSelectedItemPosition() == 2)
					record.setMood(Mood.AVERAGE);
				else if(moodSpinner.getSelectedItemPosition() == 3)
					record.setMood(Mood.SAD);
				else
					record.setMood(null);
				
				record.prepareToSave();
				
				// Insert the current record to database
				new ActivityRecordDAO(SaveActivityActivity.this).insertRecord(record);
				
				setResult(RESULT_SAVE);
				finish();
			}
		});
		
		// Prepare mood spinner
		Spinner moodSpinner = (Spinner)findViewById(R.id.spinnerMood);
		moodSpinner.setAdapter(new MyAdapter(SaveActivityActivity.this, R.layout.mood_spinner_row, new String[]{"Happy","Nice","Avg","Sad"}));
		
	
	}

	private void prepareMap() {
		MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		GoogleMap map = fragment.getMap();
		
		// Just for fixing the black box bug
	    setMapTransparent((ViewGroup)fragment.getView());
	    
		// Get the current activity record to save
		RunningActivityController controller = RunningActivityController.getInstance(getApplicationContext());
		ActivityRecord record = controller.getCurrentRecord();
		
		if (record.getLocationPoints().size() >= 1) {

			// Add the start marker
			Location startLocation = record.getLocationPoints().get(0);
			map.addMarker(new MarkerOptions()
	        .position(getLatLngFromLocation(startLocation))
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			

			// Add the end marker
			int last = record.getLocationPoints().size()-1;
			Location lastLocation = record.getLocationPoints().get(last);
			map.addMarker(new MarkerOptions()
	        .position(getLatLngFromLocation(lastLocation))
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			
			 // Move the camera instantly to hamburg with a zoom of 15.
		    map.moveCamera(CameraUpdateFactory.newLatLngZoom(getLatLngFromLocation(lastLocation), 15));
		}
		
		// Compute the center and the zoom level of the route
		double smallestLatitude = Double.MIN_VALUE;
		double largestLatitude = Double.MAX_VALUE;
		double smallestLongitude = Double.MIN_VALUE;
		double largestLongitude = Double.MAX_VALUE;
		
		// Make sure that there are at least two points on the map
		if (record.getLocationPoints().size() > 1) {
			Location originalLocation = record.getLocationPoints().get(0);
			Location startLocation = originalLocation;
			
			smallestLatitude = startLocation.getLatitude();
			largestLatitude = startLocation.getLatitude();
			smallestLongitude = startLocation.getLongitude();
			largestLongitude = startLocation.getLongitude();
			
			// Draw the route on the map
			PolylineOptions options = new PolylineOptions();
			for (int i = 1; i < record.getLocationPoints().size(); i++) {
				
				Location endLocation = record.getLocationPoints().get(i);
				options.add(getLatLngFromLocation(startLocation),getLatLngFromLocation(endLocation))
					.width(5)
					.color(Color.RED);
				startLocation = endLocation;
				
				// Compute the bounder of the route
				if(startLocation.getLatitude() > largestLatitude) {
					largestLatitude = startLocation.getLatitude();
				}
				if(startLocation.getLatitude() < smallestLatitude) {
					smallestLatitude = startLocation.getLatitude();
				}
				if(startLocation.getLatitude() > largestLongitude) {
					largestLongitude = startLocation.getLongitude();
				}
				if(startLocation.getLatitude() < smallestLongitude) {
					smallestLongitude = startLocation.getLongitude();
				}
			}
			map.addPolyline(options);

			// Decide the center
			Double centerLat = (smallestLatitude + largestLatitude) / 2;
			Double centerLng = (smallestLongitude + largestLongitude) / 2;
			
			// Decide the zoom level
			Double rangeLat = Math.abs(largestLatitude - smallestLatitude);
			Double rangeLng = Math.abs(largestLongitude - smallestLongitude);
			float zoomLevel = 15;
			float zoomHor = (float) (Math.log(rangeLat/0.0005)/Math.log(2));
			float zoomVer = (float) (Math.log(rangeLng/0.0008)/Math.log(2));
			zoomLevel = (float) (18.5 - (zoomHor>zoomVer?zoomHor:zoomVer));
			if (zoomLevel > 20)
				zoomLevel = 20;
			
			// Move and zoom the camera
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(centerLat, centerLng), zoomLevel));
		
			
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(centerLat, centerLng), zoomLevel));
		    
		    
		}
	}

	private LatLng getLatLngFromLocation(Location startLocation) {
		return new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.save_activity, menu);
		return true;
	}
	
	public class MyAdapter extends ArrayAdapter<String> {

		private int[] arr_images =  { R.drawable.icon_mood_happy, R.drawable.icon_mood_nice, R.drawable.icon_mood_average, R.drawable.icon_mood_sad};

		public MyAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.mood_spinner_row, parent,
					false);

			ImageView icon = (ImageView) row.findViewById(R.id.moodIcon);
			icon.setImageResource(arr_images [position]);

			return row;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    // Users select other modes.
	    switch (item.getItemId()) {
	    case R.id.action_dump:
	    	new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.dump_activity_dialog_title)
				.setMessage(R.string.dump_activity_dialog_info)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick( DialogInterface dialog, int which) {
						setResult(RESULT_DUMP);
						finish();
					}
				}).setNegativeButton(R.string.no, null).show();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	// Fix the black box bug
	private void setMapTransparent(ViewGroup group) {
		int childCount = group.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = group.getChildAt(i);
			if (child instanceof ViewGroup) {
				setMapTransparent((ViewGroup) child);
			} else if (child instanceof SurfaceView) {
				child.setBackgroundColor(0x00000000);
			}
		}
	}
	
}
