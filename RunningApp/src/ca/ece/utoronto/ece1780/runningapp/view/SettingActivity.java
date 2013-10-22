package ca.ece.utoronto.ece1780.runningapp.view;

import ca.ece.utoronto.ece1780.runningapp.preference.SettingsFragment;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SettingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

}
