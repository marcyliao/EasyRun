package ca.ece.utoronto.ece1780.runningapp.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.service.ActivityControllerService;
import ca.ece.utoronto.ece1780.runningapp.service.MediaPlayerService;
import ca.ece.utoronto.ece1780.runningapp.service.RunningDataChangeListener;
import ca.ece.utoronto.ece1780.runningapp.utility.FormatProcessor;
import ca.ece.utoronto.ece1780.runningapp.utility.UtilityCaculator;

public class RunningExerciseActivity extends Activity {
	
	private final static int SAVE_RECORD_REQUEST = 239;
	
	// When screen is locked. All clicks on the screen will not work.
	private boolean screenLock = false;
	
	// The goal of the running actvity. If it is zero, no goal is set.
	private float goal;

	private ActivityControllerService controllerService;
	
	public MediaPlayerService mediaPlayer;
	
	public ServiceConnection mediaConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mediaPlayer = null;	
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			MediaPlayerService.MediaBinder mediaBinder = (MediaPlayerService.MediaBinder) service; 
			mediaPlayer = mediaBinder.getMediaPlayerService();
			
			if(!mediaPlayer.isReady()) {
				Toast.makeText(RunningExerciseActivity.this, "no song found", Toast.LENGTH_SHORT).show();
				unbindService(this);
				Intent intent = new Intent(RunningExerciseActivity.this, MediaPlayerService.class);
				stopService(intent);
				
				return;
			}
			updateMusicUI();
		}
	};

	private void updateMusicUI() {
		if(mediaPlayer == null || mediaPlayer.isPaused() || !mediaPlayer.isReady()) {
			musicButton.setBackgroundResource(R.drawable.icon_music_play);
		}
		else {
			musicButton.setBackgroundResource(R.drawable.icon_music_pause);
		}
	}
	
	private ServiceConnection sconnection = new ServiceConnection() {  
		
		@Override
        public void onServiceConnected(ComponentName name, IBinder service) {  
        	controllerService = ((ActivityControllerService.ControllerServiceBinder) service).getService();
        	controllerService.bindListener(getRecordChangeListener());

        	// Once the controllerService is obtained, get the data of
        	// the current activity record and update the UI
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

	private Button musicButton;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running_exercise);
		
		// Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// Update the progress bar
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

				if(screenLock) {
					Toast.makeText(RunningExerciseActivity.this, R.string.screen_is_locked, Toast.LENGTH_SHORT).show();
					return;
				}
				
				Intent i = new Intent(RunningExerciseActivity.this, SaveActivityActivity.class);
				startActivityForResult(i,SAVE_RECORD_REQUEST);
				// TextToSpeechService.speak(getString(R.string.activity_stopped), RunningExerciseActivity.this);
			}
		});
		
		// Once resume button is clicked, resume the activity.
		findViewById(R.id.buttonResume).setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {

				if(screenLock) {
					Toast.makeText(RunningExerciseActivity.this, R.string.screen_is_locked, Toast.LENGTH_SHORT).show();
					return;
				}
				
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

				if(screenLock) 
					((ImageButton)v).setImageResource(R.drawable.icon_lock);
				else 
					((ImageButton)v).setImageResource(R.drawable.icon_unlock);
			}
		});
		
		// when click the panel, report the data by speech
		findViewById(R.id.data_panel).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(screenLock) {
					Toast.makeText(RunningExerciseActivity.this, R.string.screen_is_locked, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(controllerService != null && controllerService.isActivityGoing())
					controllerService.reportActivity();
			}
		});

		// if the controllerService is not running, start a new running activity
		if(!ActivityControllerService.isServiceRunning) {
			// Start activity
			goal = getIntent().getFloatExtra("goal", 0.0f);
			Intent startIntent = new Intent(RunningExerciseActivity.this, ActivityControllerService.class);
			startIntent.putExtra("MSG", "start");
			startIntent.putExtra("goal",goal);
			startService(startIntent);
		}
		
		//check if the mediaPlayerService is running or not
		if(MediaPlayerService.isServiceRunning == true){
			Intent intent = new Intent(this, MediaPlayerService.class);
			bindService(intent, mediaConnection, 0);
		}
		
	
		musicButton = (Button)findViewById(R.id.ButtonMusic);
		musicButton.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				
				if(screenLock) {
					Toast.makeText(RunningExerciseActivity.this, R.string.screen_is_locked, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(MediaPlayerService.isServiceRunning == false){
					startMusicService();
				}
				else{
					if(mediaPlayer.isPlaying()){
						mediaPlayer.pause();
						updateMusicUI();
					}
					else if(mediaPlayer.isPaused()){
						mediaPlayer.pause();
						updateMusicUI();
					}
					else{
						mediaPlayer.play();
						updateMusicUI();
					}
				}
			}

			
		});//end of setOnClickListener
		
		Button nextSongButton = (Button)findViewById(R.id.ButtonNextSong);
		nextSongButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(screenLock) {
					Toast.makeText(RunningExerciseActivity.this, R.string.screen_is_locked, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(mediaPlayer != null && mediaPlayer.isReady()){
					mediaPlayer.playNext();
					updateMusicUI();
				}
				else
					startMusicService();
			}
		});
		
		Button prevSongButton= (Button)findViewById(R.id.ButtonPrevSong);
		prevSongButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(screenLock) {
					Toast.makeText(RunningExerciseActivity.this, R.string.screen_is_locked, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(mediaPlayer != null && mediaPlayer.isReady()) {
					mediaPlayer.playPrevious();
					updateMusicUI();
				}
				else
					startMusicService();
					
			}
		});
	}

	private void startMusicService() {
		Intent intent = new Intent(RunningExerciseActivity.this, MediaPlayerService.class);
		startService(intent);
		bindService(intent, mediaConnection, 0);
	}
	
	private RunningDataChangeListener getRecordChangeListener() {
		return new RunningDataChangeListener(){

			@Override
			public void onDataChange(ActivityRecord currentRecord) {
				
				// When data is updated, update all the relevant UI 
				// to show users the current activity record
				FormatProcessor fp = new FormatProcessor(RunningExerciseActivity.this);
				
				((TextView)findViewById(R.id.TextViewTime)).setText(fp.getDuration(currentRecord.getTimeLength()));
				((TextView)findViewById(R.id.TextViewDistance)).setText(fp.getDistance(currentRecord.getDistance()));
				((TextView)findViewById(R.id.TextViewAVGSpeed)).setText(fp.getSpeed(currentRecord.getAvgSpeed()));
				((TextView)findViewById(R.id.TextViewCalories)).setText(fp.getCalories(currentRecord.getCalories()));
				
				// If goal is set, update the progress bar
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
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == SAVE_RECORD_REQUEST) {
			if(resultCode == SaveActivityActivity.RESULT_SAVE) {
				
				// If the activity is saved, show msg and go back to the start page
				Toast.makeText(this, R.string.activity_save, Toast.LENGTH_SHORT).show();
				
				setResult(SaveActivityActivity.RESULT_SAVE);
				controllerService.stopActivity();
				
				Intent i = new Intent(this,HomeActivity.class);
				startActivity(i);
				finish();
			}
			else if(resultCode == SaveActivityActivity.RESULT_DUMP) {

				// If the activity is dumped, show msg and go back to the start page
				Toast.makeText(this, R.string.activity_dump, Toast.LENGTH_SHORT).show();
				
				setResult(SaveActivityActivity.RESULT_DUMP);
				controllerService.stopActivity();
				
				Intent i = new Intent(this,HomeActivity.class);
				startActivity(i);
				finish();
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
		
	    // Users switch to other modes.
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
        if(MediaPlayerService.isServiceRunning){
        	unbindService(mediaConnection);
        }
		super.onPause();
	}

	@Override
	public void onBackPressed() {
	    // Do nothing.
	}
	
	@Override
	public void onResume() {
        Intent startIntent = new Intent(RunningExerciseActivity.this, ActivityControllerService.class);
        bindService(startIntent, sconnection, Context.BIND_AUTO_CREATE);  
        
        Intent mediaIntent = new Intent(RunningExerciseActivity.this, MediaPlayerService.class);
        if(MediaPlayerService.isServiceRunning){
        	bindService(mediaIntent, mediaConnection, 0);
        }
        else {
        	updateMusicUI();
        }
		
		super.onResume();
	}
}
