package ca.ece.utoronto.ece1780.runningapp.view;

import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.database.ActivityRecordDAO;
import ca.ece.utoronto.ece1780.runningapp.file.HistoryFileService;
import ca.ece.utoronto.ece1780.runningapp.service.ActivityControllerService;
import ca.ece.utoronto.ece1780.runningapp.service.TextToSpeechService;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.ActivitiesFragment;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.MusicFragment;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.StartFragment;

public class HomeActivity extends FragmentActivity implements
		ActionBar.TabListener, LocationListener {

    private static final int PICK_HISTORY_FILE_RESULT_CODE = 52;

	private static final int SETTING_REQUEST = 18;
    
	// Page view control
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// start the tts service
		TextToSpeechService.start(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		if(ActivityControllerService.isServiceRunning) {
			Intent i = new Intent(this,RunningExerciseActivity.class);
			startActivity(i);
			finish();
		}
		
	    // Set up UI components
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if(TextToSpeechService.isServiceRunning && !ActivityControllerService.isServiceRunning) {
			TextToSpeechService.stop(this);
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivitiesFragment.RECORD_DETAIL_REQUEST) {
			if (resultCode == ActivityRecordActivity.DUMP_RECORD) {
				Toast.makeText(this, R.string.activity_dump, Toast.LENGTH_SHORT)
						.show();

				// Update activity fragment
				updateActivitiyList();
				updateStartFragment();
			}
		}
		
		else if (requestCode == PICK_HISTORY_FILE_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				List<ActivityRecord> history = new HistoryFileService()
						.loadHistoryFile(this, data.getData());

				ActivityRecordDAO dao = new ActivityRecordDAO(this);
				for (ActivityRecord record : history) {
					dao.insertRecord(record);
				}
				Toast.makeText(this, "loading histoty is completed!",
						Toast.LENGTH_SHORT).show();

				updateActivitiyList();
				updateStartFragment();
			}
		}
		
		else if (requestCode == SETTING_REQUEST) {
			updateActivitiyList();
			updateStartFragment();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateStartFragment() {
		StartFragment startFragment = (StartFragment) getSupportFragmentManager()
				.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
		if (startFragment != null && startFragment.getView() != null) {
			startFragment.prepareWidgets();
		}
	}

	private void updateActivitiyList() {
		ActivitiesFragment activityFragment = (ActivitiesFragment) getSupportFragmentManager()
				.findFragmentByTag("android:switcher:" + R.id.pager + ":2");
		
		if (activityFragment != null && activityFragment.getView() != null) {
			activityFragment.updateList();
		}
	}
	
	@Override
	protected void onResume() {
		
		// Force to re-evaluate the GPS accuracy
		StartFragment startFragment = (StartFragment) getSupportFragmentManager()
				.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
		if (startFragment != null && startFragment.getView() != null) {
			startFragment.retestLocationAccuracy();
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		
		// Start tracking location when start fragment is on, otherwise stop it
		// to save battery
		if(0 == tab.getPosition()) {
			StartFragment startFragment = (StartFragment) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
			if (startFragment != null && startFragment.getView() != null) {
				startFragment.startUpateLocation();
			}
		}
		else {
			StartFragment startFragment = (StartFragment) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
			if (startFragment != null && startFragment.getView() != null) {
				startFragment.stopUpateLocation();
			}
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Users select other modes.
		Intent i;
	    switch (item.getItemId()) {
	    case R.id.action_settings:
	    	i = new Intent(this, SettingActivity.class);
	        startActivityForResult(i,SETTING_REQUEST);
	        return true;
	    case R.id.action_load_history_file:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			// set title
			alertDialogBuilder.setTitle("Alert");

			alertDialogBuilder
					.setMessage("Are you sure to add all records in file?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									selectHistoryFile();
								}

							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
	    	
	    	return true;
	    case R.id.action_store_history_file:
	    	List<ActivityRecord> records = new ActivityRecordDAO(this).getAllRecords(true);
	    	String path = new HistoryFileService().storeHistoryFile(this, records);
	    	if(path != null)
	    		Toast.makeText(this, "Backup file created! The file was saved at "+path, Toast.LENGTH_LONG).show();
	    	else
	    		Toast.makeText(this, "Unable to create the file, please check the file system of your phone", Toast.LENGTH_LONG).show();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void selectHistoryFile() {
		try {
			Intent intent = new Intent(
					Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			startActivityForResult(intent,
					PICK_HISTORY_FILE_RESULT_CODE);
		} catch (ActivityNotFoundException exp) {
			Toast.makeText(
					getBaseContext(),
					"No File (Manager / Explorer)etc Found In Your Device",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	// Adapter for the tab bar and action bar
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new StartFragment();
			case 1:
				return new MusicFragment();
			case 2:
				return new ActivitiesFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
			//return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_start).toUpperCase(l);
			case 1:
				return getString(R.string.title_music).toUpperCase(l);
			case 2:
				return getString(R.string.title_activities).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public void onLocationChanged(Location loc) {
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
