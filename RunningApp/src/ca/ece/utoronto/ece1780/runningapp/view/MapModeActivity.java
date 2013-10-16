package ca.ece.utoronto.ece1780.runningapp.view;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import ca.ece.utoronto.ece1780.runningapp.controller.RunningActivityController;
import ca.ece.utoronto.ece1780.runningapp.controller.RunningDataChangeListener;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.utility.UtilityCaculator;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MapModeActivity extends Activity {

	// controller
	private RunningActivityController controller;
	private Marker endMarker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		// Start recording running exercise activity
		controller = RunningActivityController.getInstance(getApplicationContext());

		// Bind listener to controller to update UI when record is updated
		controller.bindListener(getRecordChangeListener());
		
		prepareMap();
	}

	private void prepareMap() {
		ActivityRecord record = controller.getCurrentRecord();
		
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		        .getMap();

		// Move the camera instantly to hamburg with a zoom of 15.
		if (record.getLocationPoints().size() >= 1) {
			int last = record.getLocationPoints().size()-1;
			Location lastLocation = record.getLocationPoints().get(last);
			Location startLocation = record.getLocationPoints().get(0);

			// add the start marker
			map.addMarker(new MarkerOptions()
	        .position(new LatLng(startLocation.getLatitude(), startLocation.getLongitude()))
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			

			// add the end marker
			endMarker = map.addMarker(new MarkerOptions()
	        .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			
			// Move the camera instantly to hamburg with a zoom of 15.
		    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 15));
		}

		// make sure that there are at least two points on the map
		if (record.getLocationPoints().size() > 1) {
			Location originalLocation = record.getLocationPoints().get(0);
			Location startLocation = originalLocation;

			// draw the route on the map
			PolylineOptions options = new PolylineOptions();
			for (int i = 1; i < record.getLocationPoints().size(); i++) {
				Location endLocation = record.getLocationPoints().get(i);

				options.add(
						new LatLng(startLocation.getLatitude(),
								startLocation.getLongitude()),
						new LatLng(endLocation.getLatitude(), endLocation
								.getLongitude())).width(5).color(Color.RED);

				startLocation = endLocation;
			}
			map.addPolyline(options);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
	
	private RunningDataChangeListener getRecordChangeListener() {
		return new RunningDataChangeListener(){

			@Override
			public void onDataChange(ActivityRecord currentRecord) {
				
			}

			@Override
			public void onLocationAdded(ActivityRecord record) {
				GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				        .getMap();
				
				int l1Index = record.getLocationPoints().size()-2;
				int l2Index = record.getLocationPoints().size()-1;
				Location l1 = record.getLocationPoints().get(l1Index);
				Location l2 = record.getLocationPoints().get(l2Index);

				PolylineOptions options = new PolylineOptions();
				options.add(new LatLng(l1.getLatitude(),l1.getLongitude()),new LatLng(l2.getLatitude(), l2.getLongitude()))
					.width(5).color(Color.RED);	
				
				map.addPolyline(options);
				
				endMarker.setPosition(new LatLng(l2.getLatitude(),l2.getLongitude()));
			}
		};
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
