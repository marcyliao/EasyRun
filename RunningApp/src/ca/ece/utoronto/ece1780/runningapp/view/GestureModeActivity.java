package ca.ece.utoronto.ece1780.runningapp.view;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.service.ActivityControllerService;
import ca.ece.utoronto.ece1780.runningapp.service.RunningDataChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GestureModeActivity extends Activity {

	private TextView msgView;
	
	private ActivityControllerService controllerService;
	
	private ServiceConnection sconnection = new ServiceConnection() {  
		@Override
        public void onServiceConnected(ComponentName name, IBinder service) {  
        	controllerService = ((ActivityControllerService.ControllerServiceBinder) service).getService();
        	controllerService.bindListener(getRecordChangeListener());
        	
        	// Once the controllerService is obtained, get the data of
        	// the current activity record and update the UI
        	msgView = (TextView)findViewById(R.id.TextViewControllerState);
        	if(controllerService.isActivityPaused()) {
				msgView.setText("Pause");
			} else{
				msgView.setText("Tracking");
			}
			
        	View gestureView =findViewById(R.id.gestureView);
        	gestureView.setOnTouchListener(new OnGestureListener(GestureModeActivity.this){

				@Override
				public void oneFingerBottom2Top() {
					if(!controllerService.isActivityPaused()) {
						controllerService.pauseActivity();
						msgView.setText("Pause");
					} else{
						controllerService.resumeActivity();
						msgView.setText("Tracking");
					}
					super.oneFingerBottom2Top();
				}

				@Override
				public void oneFingerSingleClick() {
					Toast.makeText(GestureModeActivity.this, "report", Toast.LENGTH_SHORT).show();
					super.oneFingerSingleClick();
				}
        	});
        	
        }
		
		@Override 
        public void onServiceDisconnected(ComponentName name) {
        	controllerService.unbindListener();
        }    
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_mode);
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
		super.onPause();
	}
	
	@Override
	public void onResume() {
        Intent startIntent = new Intent(GestureModeActivity.this, ActivityControllerService.class);
        bindService(startIntent, sconnection, Context.BIND_AUTO_CREATE);  
		super.onResume();
	}
	
	
	
}
