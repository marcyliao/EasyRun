package ca.ece.utoronto.ece1780.runningapp.view;

import ca.ece.utoronto.ece1780.runningapp.controller.RunningActivityController;
import ca.ece.utoronto.ece1780.runningapp.controller.RunningDataChangeListener;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.utility.UtilityCaculator;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class RunningExerciseActivity extends Activity {
	
	// When screen is locked. All clicks on the screen will not work.
	private boolean screenLock = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running_exercise);
		
		// By default, the pause button is visible. Press it to pause the activity 
		// and show the continue and stop button. 
		findViewById(R.id.buttonPause).setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				if(screenLock)
					return;
		        
				// Pause activity by pause the controller
				RunningActivityController controller = RunningActivityController.getInstance(getApplicationContext());
				if(controller.isActivityGoing()) {
					controller.pauseActivity();
				}
				
				// Show the continue and stop buttons and hide the pause button
				findViewById(R.id.layoutAfterPause).setVisibility(View.VISIBLE);
				findViewById(R.id.buttonPause).setVisibility(View.INVISIBLE);
			}
		});
		
		// Once stopped button is clicked, go to the saving activity page
		findViewById(R.id.buttonStop).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
		         Intent i = new Intent(RunningExerciseActivity.this, SaveActivityActivity.class);
		         startActivityForResult(i,0);
			}
		});
		
		// Once ressume button is clicked, resume activity.
		findViewById(R.id.buttonResume).setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// Resume activity.
				RunningActivityController controller = RunningActivityController.getInstance(getApplicationContext());
				if(!controller.isActivityGoing()) {
					controller.resumeActivity();
				}
				
				// Show the pause button and hide the stop and resume buttons
				findViewById(R.id.layoutAfterPause).setVisibility(View.INVISIBLE);
				findViewById(R.id.buttonPause).setVisibility(View.VISIBLE);
			}
		});
		
		// Screen lock control
		findViewById(R.id.buttonLock).setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				screenLock = !screenLock;

				if(screenLock) {
					((ImageButton)v).setImageResource(R.drawable.icon_lock);
				}
				else {
					((ImageButton)v).setImageResource(R.drawable.icon_unlock);
				}
			}
		});
		
		// Start recording running exercise activity
		RunningActivityController controller = RunningActivityController.getInstance(getApplicationContext());
		
		// Bind listener to controller to update UI when record is updated
		controller.bindListener(new RunningDataChangeListener(){

			@Override
			public void onDataChange(ActivityRecord currentRecord) {
				((TextView)findViewById(R.id.TextViewTime)).setText(UtilityCaculator.getFormatStringFromDuration((int)(currentRecord.getTimeLength()/1000)));
				((TextView)findViewById(R.id.TextViewDistance)).setText(String.format("%.2f",Double.valueOf(currentRecord.getDistance())/1000));
				((TextView)findViewById(R.id.TextViewAVGSpeed)).setText(String.format("%.1f",currentRecord.getAvgSpeed()));
				((TextView)findViewById(R.id.TextViewCalories)).setText(String.valueOf(currentRecord.getCalories()));
			}
		});
		
		// Start activity
		controller.startActivity();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.running_exercise, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(screenLock)
			 return super.onOptionsItemSelected(item);
		
	    // Users select other modes.
		Intent i;
	    switch (item.getItemId()) {
	    case R.id.action_gesture_mode:
	        i = new Intent(this, GestureModeActivity.class);
	        startActivityForResult(i,0);
	        return true;
	    case R.id.action_watch_map:
	        i = new Intent(this, MapModeActivity.class);
	        startActivityForResult(i,0);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
    	RunningActivityController.getInstance(getApplicationContext()).stopActivity();
	}
}
