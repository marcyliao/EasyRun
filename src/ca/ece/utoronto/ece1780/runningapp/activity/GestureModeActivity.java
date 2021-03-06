package ca.ece.utoronto.ece1780.runningapp.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import ca.ece.utoronto.ece1780.runningapp.activity.listener.OnGestureListener;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.service.ActivityControllerService;
import ca.ece.utoronto.ece1780.runningapp.service.MediaPlayerService;
import ca.ece.utoronto.ece1780.runningapp.service.RunningDataChangeListener;
import ca.ece.utoronto.ece1780.runningapp.utility.FormatProcessor;
import ca.ece.utoronto.ece1780.runningapp.activity.R;

public class GestureModeActivity extends Activity {

	
	private TextView msgView;
	
	private ActivityControllerService controllerService;
	
	private MediaPlayerService mediaPlayer;
	
	//private ShakeListener mShaker;
	
	private ServiceConnection sconnection = new ServiceConnection() {  
		@Override
        public void onServiceConnected(ComponentName name, IBinder service) {  
        	controllerService = ((ActivityControllerService.ControllerServiceBinder) service).getService();
        	controllerService.bindListener(getRecordChangeListener());
        	
        	// Once the controllerService is obtained, get the data of
        	// the current activity record and update the UI
        	msgView = (TextView)findViewById(R.id.TextViewControllerState);
        	if(controllerService.isActivityPaused()) {
				msgView.setText(getString(R.string.pause));
			} else{
				msgView.setText(getString(R.string.tracking));
			}
        }
		
		@Override 
        public void onServiceDisconnected(ComponentName name) {
        	controllerService.unbindListener();
        }    
    };
    
	private ServiceConnection mediaConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mediaPlayer = null;	
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			MediaPlayerService.MediaBinder mediaBinder = (MediaPlayerService.MediaBinder) service; 
			mediaPlayer = mediaBinder.getMediaPlayerService();
		}
	};
    
    private void switchPauseAndResume() {
		if(controllerService !=null && !controllerService.isActivityPaused()) {
			controllerService.pauseActivity();
			msgView.setText(getString(R.string.pause));
		} else{
			controllerService.resumeActivity();
			msgView.setText(getString(R.string.tracking));
		}
	}
    
	private void startMusic() {
		Intent intent = new Intent(this, MediaPlayerService.class);
		startService(intent);
		bindService(intent, mediaConnection, 0);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running_exercise_gesture);
		
		// getActionBar().hide();
		
		// Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
		// Prepare Gesture listener
		View gestureView =findViewById(R.id.gestureView);
    	gestureView.setOnTouchListener(new OnGestureListener(GestureModeActivity.this){

			@Override
			public void oneFingerBottom2Top() {
				switchPauseAndResume();
				super.oneFingerBottom2Top();
			}

			@Override
			public void oneFingerTop2Bottom() {
				if(controllerService !=null) {
					controllerService.reportActivity();
				}
				super.oneFingerTop2Bottom();
			}

			@Override
			public void oneFingerDoubleClick() {
				Log.d("double click ss", "double click");
				if(mediaPlayer != null && mediaPlayer.isReady())
					mediaPlayer.pause(); 
				else {
					startMusic();
				}
				super.oneFingerDoubleClick();
			}

			@Override
			public void oneFingerLeft2Right() {
				if(mediaPlayer!=null && mediaPlayer.isReady()){
					mediaPlayer.playNext();
				}
				else {
					startMusic();
				}
				super.twoFingersLeft2Right();
			}

			@Override
			public void oneFingerRight2Left() {
				if(mediaPlayer!=null &&mediaPlayer.isReady()){
					mediaPlayer.playPrevious();
					
				}
				else {
					startMusic();
				}
				super.twoFingersRight2Left();
			}

			@Override
			public void twoFingersIncreaseDistance() {
				
				AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
				super.twoFingersIncreaseDistance();
			}

			@Override
			public void twoFingersDecreaseDistance() {

				AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
				super.twoFingersDecreaseDistance();
			}

			@Override
			public void oneFingerCounterClockCircleComplete() {

				AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
				super.oneFingerCounterClockCircleComplete();
			}

			@Override
			public void oneFingerClockCircleComplete() {

				AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				super.oneFingerClockCircleComplete();
			}
    	});
    	
    	prepareDistanceUnitWidget(this);
    	// Prepare shake listener
    	/*
		mShaker = new ShakeListener(this);
		mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
			public void onShake() {
				switchPauseAndResume();
			}
		});
		*/
	}
	
	private void prepareDistanceUnitWidget(Context context) {
		FormatProcessor fp = new FormatProcessor(context);
		((TextView)findViewById(R.id.textViewDistanceUnit)).setText(fp.getDistanceUnit());
		((TextView)findViewById(R.id.textViewSpeedUnit)).setText(fp.getSpeedUnit());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gesture_mode, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Users select other modes.
		Intent i;
	    switch (item.getItemId()) {
	    case R.id.action_help:
	    	i = new Intent(this, GestureHelpActivity.class);
	        startActivityForResult(i,0);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private RunningDataChangeListener getRecordChangeListener() {
		return new RunningDataChangeListener(){

			@Override
			public void onDataChange(ActivityRecord currentRecord) {
				
				// When data is updated, update all the relevant UI 
				// to show users the current activity record
				FormatProcessor fp = new FormatProcessor(GestureModeActivity.this);
				
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
				// TODO Auto-generated method stub
			}

			@Override
			public void onGoalAchieved() {
				// TODO Auto-generated method stub
			}
		};
	}
	
	@Override
	protected void onPause() {
        
		unbindService(sconnection);
		if(MediaPlayerService.isServiceRunning){
			unbindService(mediaConnection);
		}
        //mShaker.pause();
		super.onPause();
	}
	
	@Override
	public void onResume() {
        Intent startIntent = new Intent(GestureModeActivity.this, ActivityControllerService.class);
        bindService(startIntent, sconnection, Context.BIND_AUTO_CREATE);

		if(MediaPlayerService.isServiceRunning){
			Intent mediaIntent = new Intent(GestureModeActivity.this, MediaPlayerService.class);
			bindService(mediaIntent, mediaConnection, 0);
		}
	    //mShaker.resume();
		super.onResume();
	}
	
	
	
}
