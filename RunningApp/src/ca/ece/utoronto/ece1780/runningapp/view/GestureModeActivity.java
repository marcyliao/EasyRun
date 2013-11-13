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
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.service.ActivityControllerService;
import ca.ece.utoronto.ece1780.runningapp.service.MediaPlayerService;
import ca.ece.utoronto.ece1780.runningapp.service.RunningDataChangeListener;
import ca.ece.utoronto.ece1780.runningapp.view.listener.OnGestureListener;

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
    
    private void switchPauseAndResume() {
		if(controllerService !=null && !controllerService.isActivityPaused()) {
			controllerService.pauseActivity();
			msgView.setText(getString(R.string.pause));
		} else{
			controllerService.resumeActivity();
			msgView.setText(getString(R.string.tracking));
		}
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_mode);
		
		getActionBar().hide();
		
		// Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
    	//Prepare music service  added by nate
		//set up music service
		Intent intent = new Intent(this, MediaPlayerService.class);
		bindService(intent, new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mediaPlayer = null;	
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				
				MediaPlayerService.MediaBinder mediaBinder = (MediaPlayerService.MediaBinder) service; 
				mediaPlayer = mediaBinder.getMediaPlayerService();
			}
		}, 0);
		
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
				super.oneFingerSingleClick();
			}

			@Override
			public void oneFingerSingleClick() {
			
				super.oneFingerSingleClick();
			}

			@Override
			public void twoFingersLeft2Right() {
				Log.d("Gesture", "twoFingersLeft2Right");
				if(mediaPlayer.isReady()){
					mediaPlayer.playNext();
					Log.d("Gesture", mediaPlayer.getIndex() + mediaPlayer.getSongName());
				}
				super.twoFingersLeft2Right();
			}

			@Override
			public void twoFingersRight2Left() {
				Log.d("Gesture", "twoFingersRight2Left");
				if(mediaPlayer.isReady()){
					mediaPlayer.playPrevious();
					
					Log.d("Gesture", mediaPlayer.getIndex()+ mediaPlayer.getSongName());
				}
				super.twoFingersRight2Left();
			}

			@Override
			public void twoFingersSingleClick() {
				Log.d("Gesture", "twoFingersSingleClick");
				mediaPlayer.pause();
				super.twoFingersSingleClick();
			}

			@Override
			public void twoFingersTop2Bottom() {
				Log.d("Gesture", "twoFingersTop2Bottom");
				if(mediaPlayer.isReady()){
					mediaPlayer.play();
				}
				super.twoFingersTop2Bottom();
			}

			@Override
			public void twoFingersBottom2Top() {
				if(mediaPlayer.isPlaying()){
					mediaPlayer.stop();
				}
				super.twoFingersBottom2Top();
			}

			@Override
			public void twoFingersIncreaseDistance() {
				
				if(mediaPlayer.isPlaying()){
					mediaPlayer.volumeUp(1);
				}
				super.twoFingersIncreaseDistance();
			}

			@Override
			public void twoFingersDecreaseDistance() {
				if(mediaPlayer.isPlaying()){
					mediaPlayer.volumeDown(1);
				}
				super.twoFingersDecreaseDistance();
			}

			@Override
			public void oneFingerCounterClockCircleComplete() {
				if(mediaPlayer.isPlaying()){
					mediaPlayer.volumeDown(1);
				}
				super.oneFingerCounterClockCircleComplete();
			}

			@Override
			public void oneFingerClockCircleComplete() {
				if(mediaPlayer.isPlaying()){
					mediaPlayer.volumeUp(1);
				}
				super.oneFingerClockCircleComplete();
			}

			
			
			
			
    	});
    	
    	
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gesture_mode, menu);
		return true;
	}
	
	private RunningDataChangeListener getRecordChangeListener() {
		return new RunningDataChangeListener(){

			@Override
			public void onDataChange(ActivityRecord currentRecord) {
				// TODO Auto-generated method stub
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
        //mShaker.pause();
		super.onPause();
	}
	
	@Override
	public void onResume() {
        Intent startIntent = new Intent(GestureModeActivity.this, ActivityControllerService.class);
        bindService(startIntent, sconnection, Context.BIND_AUTO_CREATE);  
	    //mShaker.resume();
		super.onResume();
	}
	
	
	
}
