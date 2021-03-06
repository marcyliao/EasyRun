package ca.ece.utoronto.ece1780.runningapp.activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.service.ActivityControllerService;
import ca.ece.utoronto.ece1780.runningapp.service.RunningDataChangeListener;
import ca.ece.utoronto.ece1780.runningapp.utility.FormatProcessor;
import ca.ece.utoronto.ece1780.runningapp.activity.R;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MapModeActivity extends Activity {

	// Controller
	
	private Marker endMarker;
	GoogleMap map;

	private ActivityControllerService controllerService;
	private ServiceConnection sconnection = new ServiceConnection() {  
		
		@Override
        public void onServiceConnected(ComponentName name, IBinder service) {  
        	controllerService = ((ActivityControllerService.ControllerServiceBinder) service).getService();
        	controllerService.bindListener(getRecordChangeListener());
        	
        	// Once the controllerService is obtained, get the data of
        	// the current activity record and update the UI
    		prepareMap();
        }
		
		@Override 
        public void onServiceDisconnected(ComponentName name) {
        	controllerService.unbindListener();
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
	}

	private void prepareMap() {
		ActivityRecord record = controllerService.getCurrentRecord();
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		        .getMap();
		map.clear();
		
		if (record.getLocationPoints().size() >= 1) {
			
			// Get the start location
			ActivityRecord.Location startLocation = record.getLocationPoints().get(0);
			
			// Get the last location
			int last = record.getLocationPoints().size()-1;
			ActivityRecord.Location lastLocation = record.getLocationPoints().get(last);

			// Add the start marker
			map.addMarker(new MarkerOptions()
	        .position(getLatLngFromLocation(startLocation))
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			

			// Add the end marker
			endMarker = map.addMarker(new MarkerOptions()
	        .position(getLatLngFromLocation(lastLocation))
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			
			// Move the camera instantly to hamburg with a zoom of 15.
		    map.moveCamera(CameraUpdateFactory.newLatLngZoom(getLatLngFromLocation(lastLocation), 15));
		}

		// Make sure that there are at least two points on the map
		if (record.getLocationPoints().size() > 1) {
			ActivityRecord.Location originalLocation = record.getLocationPoints().get(0);
			ActivityRecord.Location startLocation = originalLocation;

			// Draw the route on the map
			PolylineOptions options = new PolylineOptions();
			for (int i = 1; i < record.getLocationPoints().size(); i++) {
				ActivityRecord.Location endLocation = record.getLocationPoints().get(i);

				options.add(getLatLngFromLocation(startLocation),getLatLngFromLocation(endLocation))
					.width(5)
					.color(Color.RED);

				startLocation = endLocation;
			}
			map.addPolyline(options);
		}
		
		findViewById(R.id.ButtonRelocate).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Location loc = controllerService.getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(loc != null) {
					LatLng currentLocation = getLatLngFromLocation(loc);
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
				}
			}
			
		});
		
		prepareDistanceUnitWidget(this);
	}
	
	private void prepareDistanceUnitWidget(Context context) {
		FormatProcessor fp = new FormatProcessor(context);
		((TextView)findViewById(R.id.textViewDistanceUnit)).setText(fp.getDistanceUnit());
		((TextView)findViewById(R.id.textViewSpeedUnit)).setText(fp.getSpeedUnit());
	}

	private LatLng getLatLngFromLocation(ActivityRecord.Location startLocation) {
		return new LatLng(startLocation.getLatitude(),
				startLocation.getLongitude());
	}

	private LatLng getLatLngFromLocation(Location startLocation) {
		return new LatLng(startLocation.getLatitude(),
				startLocation.getLongitude());
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
				// When data is updated, update all the relevant UI 
				// to show users the current activity record
				FormatProcessor fp = new FormatProcessor(MapModeActivity.this);
				
				((TextView)findViewById(R.id.TextViewTime)).setText(fp.getDuration(currentRecord.getTimeLength()));
				((TextView)findViewById(R.id.TextViewDistance)).setText(fp.getDistance(currentRecord.getDistance()));
				((TextView)findViewById(R.id.TextViewAVGSpeed)).setText(fp.getSpeed(currentRecord.getAvgSpeed()));
				((TextView)findViewById(R.id.TextViewCalories)).setText(fp.getCalories(currentRecord.getCalories()));
			}

			@Override
			public void onLocationAdded(ActivityRecord record) {
				
				// When a location is added, update the route on the map.
				MapFragment f = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
					if(f != null) {
					GoogleMap map = f.getMap();
					
					int l1Index = record.getLocationPoints().size()-2;
					int l2Index = record.getLocationPoints().size()-1;
					
					ActivityRecord.Location l1 = record.getLocationPoints().get(l1Index);
					ActivityRecord.Location l2 = record.getLocationPoints().get(l2Index);
	
					PolylineOptions options = new PolylineOptions();
					options.add(getLatLngFromLocation(l1),getLatLngFromLocation(l2))
						.width(5).color(Color.RED);	
					
					map.addPolyline(options);
					
					endMarker.setPosition(getLatLngFromLocation(l2));
				}
			}

			@Override
			public void onGoalAchieved() {
				// TODO Auto-generated method stub
			}
		};
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
        unbindService(sconnection);
		super.onPause();
	}
	
	@Override
	public void onResume() {
        Intent startIntent = new Intent(MapModeActivity.this, ActivityControllerService.class);
        bindService(startIntent, sconnection, Context.BIND_AUTO_CREATE);  
		
		super.onResume();
	}
}
