package ca.ece.utoronto.ece1780.runningapp.controller;

import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.utility.UtilityCaculator;

public class RunningActivityController implements LocationListener {
	
	private static final int INTERVAL_ADDING_LOCATION_TO_RECORD = 2000;

	// Time interval to update the timer thread. Unit: ms
	private static final long MIN_TIME_INTERVAL_FOR_UPDATE = 300;

	// The context to get system services like gps
	private static Context appContext;
	
	// A record to store the data of the current activity
	private ActivityRecord currentRecord;
	
	// Last time to add location point to record 
	private Date lastRecordFrameTime;
	
	// Last time to update record, such as increasing the duration
	private Date lastUpdateTime;
	
	// Is the activity currently going on, or being paused or stopped
	private boolean activityGoing;
	
	// Timer used to update the record
	private RefreshTask refreshTask;
	
	// The singleton instance controller
	private static RunningActivityController controller;
	
	// Location manager to get gps location
	private LocationManager locationManager;
	
	// Listener used to notify UI thread when record is updated
	private RunningDataChangeListener listener;
	
	// Use singleton
	private RunningActivityController() {
		
		activityGoing = false;
		locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
		refreshTask = new RefreshTask();
	}
	
	public ActivityRecord getCurrentRecord(){
		return currentRecord;
	}
	
	public static RunningActivityController getInstance(Context context) {
		if(controller == null) {
			appContext = context;
			controller = new RunningActivityController();
		}
		
		return controller;
	}
	
	public boolean isActivityGoing() {
		return activityGoing;
	}

	// Start activity
	public void startActivity(){	

		// Create a new record
		currentRecord = new ActivityRecord();
		
		// Resume Activity
		resumeActivity();
	}
	
	public void stopActivity() {
		pauseActivity();
		stopUpateLocation();
	}
	
	public void pauseActivity() {
		activityGoing = false;
		
		refreshTask.finish();
	}
	
	public void resumeActivity() {

		activityGoing = true;
		
		// Start updating location from gps system
		startUpateLocation();
		
		// Add the first point to the location list. 
		addTheFirstLocation();
		
		// Start timer
		refreshTask = new RefreshTask();
		refreshTask.execute();
	}

	private void addTheFirstLocation() {
		Date currentTime = new Date();
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location != null) {
			currentRecord.getLocationPoints().add(location);
			currentRecord.getLocationPointsTime().add(currentTime);
			lastRecordFrameTime = currentTime;
			
			Log.v("runners", "runners - location point added: " + location.getLatitude() + "," + location.getLatitude());
			Log.v("runners", "runners - time: " + currentTime);
		}
	}
	
	
	// Logic of updating record
	public void updateCurrentRecord(){
		
		if(isActivityGoing()) {
			Date currentTime = new Date();
			currentRecord.setTimeLength(currentRecord.getTimeLength() + (currentTime.getTime() - lastUpdateTime.getTime()));
			lastUpdateTime = currentTime;
			
			// Add a time point and location point to record every time interval.
			if(currentTime.getTime() - lastRecordFrameTime.getTime() > INTERVAL_ADDING_LOCATION_TO_RECORD) {
				
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(location != null) {
					
					// Add a location and time point to record
					currentRecord.getLocationPoints().add(location);
					currentRecord.getLocationPointsTime().add(currentTime);
					lastRecordFrameTime = currentTime;
					
					Log.v("runners", "runners - location point added: " + location.getLatitude() + "," + location.getLatitude());
					Log.v("runners", "runners - time: " + currentTime);
					
					// Add the distance to record once a new location point is added
					int locationPointsLength = currentRecord.getLocationPoints().size();
					if(locationPointsLength > 1) {
						Location last = currentRecord.getLocationPoints().get(locationPointsLength-1);
						Location lastSecond = currentRecord.getLocationPoints().get(locationPointsLength-2);
						
						// Compute new distance
						currentRecord.setDistance(currentRecord.getDistance()+last.distanceTo(lastSecond));
						Log.v("runners", "runners - distance: " + currentRecord.getDistance());
						
						// Compute calories
						currentRecord.setCalories((int) UtilityCaculator.computeColories(70, currentRecord.getDistance()));
						
						// Compute avg speed
						double avgSpeed = (double) ((currentRecord.getDistance()*3600)/(currentRecord.getTimeLength()));
						Log.v("runners", "avg speed: " + avgSpeed + " = " + currentRecord.getDistance()+"/"+currentRecord.getTimeLength()/3600);
						currentRecord.setAvgSpeed((float)avgSpeed);
					}
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public void startUpateLocation() { 
		if(locationManager != null) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL_FOR_UPDATE, 0, this);
		}
	}
	
	public void stopUpateLocation() { 
		if(locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	// Release the listener to avoid changing UI thread during update
	public void unbindListener() {
		listener = null;
	}

	// Bind the listener to notify changing UI thread during udpate
	public void bindListener(RunningDataChangeListener listener) {
		this.listener = listener;
	}

	// A timer used to keep track of the activity
	class RefreshTask extends AsyncTask<Object, Object, Object> {

		boolean finish = false;
		
		
		@Override
		protected void onPreExecute() {
			finish = false;
			super.onPreExecute();
		}

		public void finish() {
			finish = true;
		}
		
		@Override
		protected void onProgressUpdate(Object... values) {
			
			super.onProgressUpdate(values);
			
			// Update record
			updateCurrentRecord();
			
			// Notify listener
			if(listener != null)
				listener.onDataChange(currentRecord);
		}
		 
        @Override
        protected Object doInBackground(Object... params) {
    		lastUpdateTime = new Date();
    		
            while(isActivityGoing() && !finish) {
                try {
                    Thread.sleep(MIN_TIME_INTERVAL_FOR_UPDATE);
                    publishProgress();
                } catch (Exception e) {
                	Log.e("runners","runners-" + e.getMessage());
                };
            }
            return null;
        }  
	};
	

}	
