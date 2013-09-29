package ca.ece.utoronto.ece1780.runningapp.view;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GestureModeActivity extends Activity {

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

}
