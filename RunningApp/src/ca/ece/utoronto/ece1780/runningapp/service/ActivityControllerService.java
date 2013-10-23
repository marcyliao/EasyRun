package ca.ece.utoronto.ece1780.runningapp.service;

import java.util.Date;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.preference.UserSetting;
import ca.ece.utoronto.ece1780.runningapp.utility.UtilityCaculator;
import ca.ece.utoronto.ece1780.runningapp.view.HomeActivity;
import ca.ece.utoronto.ece1780.runningapp.view.R;
import ca.ece.utoronto.ece1780.runningapp.view.RunningExerciseActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ActivityControllerService extends Service implements LocationListener {
	
	// If the service is alive
	public static boolean isServiceRunning = false;

	/*Timer control*/
	// Time interval to add a point to location list. Unit: ms
    private static final int INTERVAL_ADDING_LOCATION_TO_RECORD = 2000;
	// Time interval to update the timer thread. Unit: ms
	private static final long MIN_TIME_INTERVAL_FOR_UPDATE = 300;
	// Last time to add location point to record 
	private Date lastRecordFrameTime;
	// Last time to update record, such as increasing the duration
	private Date lastUpdateTime;
	// Timer used to update the record
	private RefreshTask refreshTask;

	// A record to store the data of the current activity
	private ActivityRecord currentRecord;
	// Whether the distance is greater than the goal
	private boolean goalAchieved;
	// current user weight
	private int weight;
	
	/*Controller states*/
	// Is the activity currently going on, or being stopped
	private boolean activityGoing;
	// Is the activity currently going on, or being paused
	private boolean activityPaused;
	
	
	// Location manager to get gps location
	private LocationManager locationManager;
	
	// Listener used to notify UI thread when record is updated
	private RunningDataChangeListener listener;
	
	
	/*Android service control*/
	// Binder used to communicate with activities;
    public final IBinder binder = new ControllerServiceBinder();  
    public class ControllerServiceBinder extends Binder {  
    	public ActivityControllerService getService() {  
            return ActivityControllerService.this;  
        }  
    } 
    
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) { 
    	String MSG = null;
    	if(intent != null)
    		MSG = intent.getStringExtra("MSG");  
    	
        if(MSG!=null && MSG.equals("start") && !isActivityGoing()) {
            float goal = intent.getFloatExtra("goal",0.0f); 
            startActivity(goal);
            
            // Start notification to show the users that the app is running on the background
            Notification notification = new Notification(R.drawable.ic_launcher_small, getText(R.string.app_name),System.currentTimeMillis());
            Intent notificationIntent = new Intent(this, RunningExerciseActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            notification.setLatestEventInfo(this, getText(R.string.app_name),getText(R.string.notification_msg), pendingIntent);
            startForeground(123, notification);
            
            isServiceRunning = true;
        } 
    	
        return super.onStartCommand(intent, flags, startId);  
    }
    
	
	@Override  
	public void onCreate() { 
		activityGoing = false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public ActivityRecord getCurrentRecord(){
		return currentRecord;
	}
	
	public boolean isActivityGoing() {
		return activityGoing;
	}
	
	public boolean isActivityPaused() {
		return activityPaused;
	}

	// Start activity
	public void startActivity(float goal){	
		locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

		goalAchieved = false; 
		weight = new UserSetting(getApplicationContext()).getWeight();
		activityGoing = true;
		
		// Create a new record
		currentRecord = new ActivityRecord();
		currentRecord.setGoal(goal);
		
		// Resume Activity
		resumeActivity();
		refreshTask = new RefreshTask();
		refreshTask.execute();
		
	}
	
	public void stopActivity() {
		pauseActivity();
        isServiceRunning = false;
		activityGoing = false;
		refreshTask.finish();
		stopUpateLocation();
	}
	
	public void pauseActivity() {
		activityPaused = true;
	}
	
	public void resumeActivity() {

		activityPaused = false;
		lastUpdateTime = new Date();
		// Start updating location from gps system
		startUpateLocation();
		// Add the first point to the location list. 
		addTheFirstLocation();
	}
	
	// Logic of updating record
	public void updateCurrentRecord(){
		
		if(isActivityGoing() && !isActivityPaused()) {
			Date currentTime = new Date();
			currentRecord.setTimeLength(currentRecord.getTimeLength() + (currentTime.getTime() - lastUpdateTime.getTime()));
			lastUpdateTime = currentTime;
			
			// Add a time point and location point to record every time interval.
			if(currentTime.getTime() - lastRecordFrameTime.getTime() > INTERVAL_ADDING_LOCATION_TO_RECORD) {
				processNewLocationAdded(currentTime);
			}
		}
		
		// Notify record update
		if(listener != null) {
			listener.onDataChange(currentRecord);
			if(currentRecord.getGoal() != 0.0f && !goalAchieved && currentRecord.getDistance() >= currentRecord.getGoal()*1000) {
				goalAchieved = true;
				listener.onGoalAchieved();
			}
		}
	}

	private void processNewLocationAdded(Date currentTime) {
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location != null) {
			
			// Add a location and time point to record
			currentRecord.getLocationPoints().add(location);
			currentRecord.getLocationPointsTime().add(currentTime);
			lastRecordFrameTime = currentTime;
			
			// Add the distance to record once a new location point is added
			int locationPointsLength = currentRecord.getLocationPoints().size();
			if(locationPointsLength > 1) {
				Location last = currentRecord.getLocationPoints().get(locationPointsLength-1);
				Location lastSecond = currentRecord.getLocationPoints().get(locationPointsLength-2);
				
				// Compute new distance
				currentRecord.setDistance(currentRecord.getDistance()+last.distanceTo(lastSecond));
				
				// Compute calories
				currentRecord.setCalories((int) UtilityCaculator.computeColories(weight, currentRecord.getDistance()));

				// Compute avg speed
				double avgSpeed = (double) ((currentRecord.getDistance()*3600)/(currentRecord.getTimeLength()));
				currentRecord.setAvgSpeed((float)avgSpeed);
			}
			
			// Nodify location added
			if(listener != null)
				listener.onLocationAdded(currentRecord);
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
	
	
	/*GPS control*/
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
            
            stopSelf();
            return null;
        }  
	};
	
	private void addTheFirstLocation() {
		Date currentTime = new Date();
		lastRecordFrameTime = currentTime;
		
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location != null) {
			currentRecord.getLocationPoints().add(location);
			currentRecord.getLocationPointsTime().add(currentTime);
		}
	}
}
