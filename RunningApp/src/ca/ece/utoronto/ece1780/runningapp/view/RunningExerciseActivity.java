package ca.ece.utoronto.ece1780.runningapp.view;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class RunningExerciseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running_exercise);
		
		findViewById(R.id.buttonPause).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Show continue and stop button
		        Intent i = new Intent(RunningExerciseActivity.this, SaveActivityActivity.class);
		        startActivityForResult(i,0);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.running_exercise, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
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

}
