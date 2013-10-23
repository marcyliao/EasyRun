package ca.ece.utoronto.ece1780.runningapp.view;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.service.ControllerService;
import ca.ece.utoronto.ece1780.runningapp.service.RunningDataChangeListener;
import ca.ece.utoronto.ece1780.runningapp.utility.UtilityCaculator;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RunningExerciseActivity extends Activity {
	
	public final static int SAVE_RECORD_REQUEST = 239;
	
	// When screen is locked. All clicks on the screen will not work.
	private boolean screenLock = false;
	private float goal;

	private ControllerService controllerService;
	
	private ServiceConnection sconnection = new ServiceConnection() {  
		
		@Override
        public void onServiceConnected(ComponentName name, IBinder service) {  
        	controllerService = ((ControllerService.ControllerServiceBinder) service).getService();
        	controllerService.bindListener(getRecordChangeListener());
        	
        	if(controllerService.isActivityPaused()) {
        		findViewById(R.id.layoutAfterPause).setVisibility(View.VISIBLE);
				findViewById(R.id.buttonPause).setVisibility(View.INVISIBLE);
        	} 
        	else {
        		findViewById(R.id.layoutAfterPause).setVisibility(View.INVISIBLE);
				findViewById(R.id.buttonPause).setVisibility(View.VISIBLE);
        	}
        }
		
		@Override 
        public void onServiceDisconnected(ComponentName name) {
        	controllerService.unbindListener();
        }    
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running_exercise);
		
		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
		pb.setMax(100);
		pb.setProgress(0);
		
		// By default, the pause button is visible. Press it to pause the activity 
		// and show the continue and stop button. 
		findViewById(R.id.buttonPause).setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				if(screenLock) {
					Toast.makeText(RunningExerciseActivity.this, R.string.screen_is_locked, Toast.LENGTH_SHORT).show();
					return;
				}
		        
				// Pause activity by pause the controller
				if(controllerService != null && controllerService.isActivityGoing())
					controllerService.pauseActivity();
				
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
		         startActivityForResult(i,SAVE_RECORD_REQUEST);
			}
		});
		
		// Once resume button is clicked, resume the activity.
		findViewById(R.id.buttonResume).setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// Resume activity.
				if(controllerService != null && controllerService.isActivityGoing())
					controllerService.resumeActivity();
				
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

		if(!ControllerService.isServiceRunning) {
		// Start activity
			goal = getIntent().getFloatExtra("goal", 0.0f);
			Intent startIntent = new Intent(RunningExerciseActivity.this, ControllerService.class);
			startIntent.putExtra("MSG", "start");
			startIntent.putExtra("goal",goal);
			startService(startIntent);
		}
        
	}

	private RunningDataChangeListener getRecordChangeListener() {
		return new RunningDataChangeListener(){

			@Override
			public void onDataChange(ActivityRecord currentRecord) {
				((TextView)findViewById(R.id.TextViewTime)).setText(UtilityCaculator.getFormatStringFromDuration((int)(currentRecord.getTimeLength()/1000)));
				((TextView)findViewById(R.id.TextViewDistance)).setText(String.format("%.2f",Double.valueOf(currentRecord.getDistance())/1000));
				((TextView)findViewById(R.id.TextViewAVGSpeed)).setText(String.format("%.1f",currentRecord.getAvgSpeed()));
				((TextView)findViewById(R.id.TextViewCalories)).setText(String.valueOf(currentRecord.getCalories()));
				
				if(currentRecord.getGoal()!=0) {
					ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
					pb.setMax(100);
					pb.setProgress((int) (100*(currentRecord.getDistance()/1000/currentRecord.getGoal())));
				}
			}

			@Override
			public void onLocationAdded(ActivityRecord record) {
				// Do nothing
			}

			@Override
			public void onGoalAchieved() {
				Toast.makeText(RunningExerciseActivity.this, R.string.target_achieved, Toast.LENGTH_SHORT).show();
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == SAVE_RECORD_REQUEST) {
			if(resultCode == SaveActivityActivity.RESULT_SAVE) {
				setResult(SaveActivityActivity.RESULT_SAVE);
				controllerService.stopActivity();
				
				Toast.makeText(this, R.string.activity_save, Toast.LENGTH_SHORT).show();
				
				Intent i = new Intent(this,HomeActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
			else if(resultCode == SaveActivityActivity.RESULT_DUMP) {
				setResult(SaveActivityActivity.RESULT_DUMP);
				controllerService.stopActivity();
				
				Toast.makeText(this, R.string.activity_dump, Toast.LENGTH_SHORT).show();
				
				Intent i = new Intent(this,HomeActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.running_exercise, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(screenLock) {
			Toast.makeText(RunningExerciseActivity.this, R.string.screen_is_locked, Toast.LENGTH_SHORT).show();
			return super.onOptionsItemSelected(item);
		}
		
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
	}
	
	@Override
	protected void onPause() {
        unbindService(sconnection);
		super.onPause();
	}

	@Override
	public void onBackPressed() {
	    // Do nothing.
	}
	
	@Override
	public void onResume() {
        Intent startIntent = new Intent(RunningExerciseActivity.this, ControllerService.class);
        bindService(startIntent, sconnection, Context.BIND_AUTO_CREATE);  
		
		super.onResume();
	}
}
