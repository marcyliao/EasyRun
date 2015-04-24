package ca.ece.utoronto.ece1780.runningapp.activity;

import ca.ece.utoronto.ece1780.runningapp.activity.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GestureHelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_help);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gesture_help, menu);
		return true;
	}

}
