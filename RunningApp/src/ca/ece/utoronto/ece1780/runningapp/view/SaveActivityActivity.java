package ca.ece.utoronto.ece1780.runningapp.view;


import ca.ece.utoronto.ece1780.runningapp.controller.RunningActivityController;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;

public class SaveActivityActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_activity);

		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		        .getMap();
		
		RunningActivityController controller = RunningActivityController.getInstance(getApplicationContext());
		ActivityRecord record = controller.getCurrentRecord();
		
		if (record.getLocationPoints().size() > 1) {
			Location originalLocation = record.getLocationPoints().get(0);
			Location startLocation = originalLocation;
			
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

		    // Move the camera instantly to hamburg with a zoom of 15.
		    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(originalLocation.getLatitude(), originalLocation.getLongitude()), 15));

		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.save_activity, menu);
		return true;
	}
}
