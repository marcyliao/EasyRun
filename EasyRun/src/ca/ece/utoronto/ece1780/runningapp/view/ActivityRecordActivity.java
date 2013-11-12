package ca.ece.utoronto.ece1780.runningapp.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.data.Mood;
import ca.ece.utoronto.ece1780.runningapp.database.ActivityRecordDAO;
import ca.ece.utoronto.ece1780.runningapp.utility.UtilityCaculator;
import android.graphics.Color;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityRecordActivity extends Activity {

	public final static int DUMP_RECORD = 979;
	ActivityRecord record;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_record);
		
		Long id = getIntent().getLongExtra("id", -1);
		if(id == -1)
			return;
		
		record = new ActivityRecordDAO(this).getRecord(id);
		record.prepareToShow();
		
		
		prepareWidgets();
		prepareMap();
		
		TestNu.numActivity++;
		Log.v("numActivity", TestNu.numActivity+"");
	}

	private void prepareWidgets() {
		
		// Prepare number data stuff
		((TextView)findViewById(R.id.TextViewAVGSpeed)).setText(String.format("%.1f",record.getAvgSpeed()));
		((TextView)findViewById(R.id.TextViewTime)).setText(UtilityCaculator.getFormatStringFromDuration((int)(record.getTimeLength()/1000)));
		((TextView)findViewById(R.id.TextViewCalories)).setText(String.valueOf(record.getCalories()));
		((TextView)findViewById(R.id.TextViewDistance)).setText(String.format("%.2f",Double.valueOf(record.getDistance())/1000));
		
		// Prepare the mood image
		ImageView imv = (ImageView)findViewById(R.id.imageViewMood);
		if(record.getMood() == Mood.HAPPY) {
			imv.setImageResource(R.drawable.icon_mood_happy);
		}
		else if(record.getMood() == Mood.NICE) {
			imv.setImageResource(R.drawable.icon_mood_nice);
		}
		else if(record.getMood() == Mood.AVERAGE) {
			imv.setImageResource(R.drawable.icon_mood_average);
		}
		else if(record.getMood() == Mood.SAD) {
			imv.setImageResource(R.drawable.icon_mood_sad);
		}
		
		if(record.getNote() != null && !record.getNote().equals(""))
			((TextView)findViewById(R.id.TextViewNote)).setText(record.getNote());
		
		// Set title of actionbar to the date of the activity
		Date date = new Date(record.getTime());
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.US);
		String dateTitle = dateformatYYYYMMDD.format(date);
		getActionBar().setTitle(dateTitle);
			
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		TestNu.numActivity--;
		Log.v("numActivity", TestNu.numActivity+"");
		super.onDestroy();
	}

	private void prepareMap() {
		MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		GoogleMap map = fragment.getMap();
		
		// Just for fixing the black box bug
	    setMapTransparent((ViewGroup)fragment.getView());
		
		if (record.getLocationPoints().size() >= 1) {

			// Add the start marker
			ActivityRecord.Location startLocation = record.getLocationPoints().get(0);
			map.addMarker(new MarkerOptions()
	        .position(getLatLngFromLocation(startLocation))
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			

			// Add the end marker
			int last = record.getLocationPoints().size()-1;
			ActivityRecord.Location lastLocation = record.getLocationPoints().get(last);
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
			ActivityRecord.Location originalLocation = record.getLocationPoints().get(0);
			ActivityRecord.Location startLocation = originalLocation;

			smallestLatitude = startLocation.getLatitude();
			largestLatitude = startLocation.getLatitude();
			smallestLongitude = startLocation.getLongitude();
			largestLongitude = startLocation.getLongitude();

			// Draw the route on the map
			PolylineOptions options = new PolylineOptions();
			for (int i = 1; i < record.getLocationPoints().size(); i++) {

				ActivityRecord.Location endLocation = record.getLocationPoints().get(i);
				options.add(getLatLngFromLocation(startLocation),
						getLatLngFromLocation(endLocation)).width(5)
						.color(Color.RED);
				startLocation = endLocation;

				// Compute the bounder of the route
				if (startLocation.getLatitude() > largestLatitude) {
					largestLatitude = startLocation.getLatitude();
				}
				if (startLocation.getLatitude() < smallestLatitude) {
					smallestLatitude = startLocation.getLatitude();
				}
				if (startLocation.getLongitude() > largestLongitude) {
					largestLongitude = startLocation.getLongitude();
				}
				if (startLocation.getLongitude()  < smallestLongitude) {
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
	
	private LatLng getLatLngFromLocation(ActivityRecord.Location startLocation) {
		return new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_record, menu);
		return true;
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
							new ActivityRecordDAO(ActivityRecordActivity.this).deleteRecord(record.getId());
							setResult(DUMP_RECORD);
							finish();
						}
					}).setNegativeButton(R.string.no, null).show();
		        return true;
		        
		    case R.id.action_statistics:
		    	Intent i = new Intent(this, StatisticActivity.class);
		    	
		    	Bundle b = new Bundle();
		    	b.putSerializable("record", record);
		    	i.putExtras(b);
		    	startActivity(i);
		    	return true;
		    	
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}

}
