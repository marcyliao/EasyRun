package ca.ece.utoronto.ece1780.runningapp.view;

import java.util.Locale;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.ActivitiesFragment;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.MusicFragment;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.StartFragment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
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

public class HomeActivity extends FragmentActivity implements
		ActionBar.TabListener, LocationListener {
	
	// Page view control
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
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
	protected void onActivityResult(int requestCode, int resultCode,Intent data) {
		if(requestCode == StartFragment.START_ACTIVITY_REQUEST) {
			if(resultCode == SaveActivityActivity.RESULT_SAVE) {
				
				// Force updating data in different fragments.
				// Update start fragment
				StartFragment startFragment = (StartFragment) getSupportFragmentManager()
						.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
				if (startFragment != null && startFragment.getView() != null) {
					startFragment.retestLocationAccuracy();
					startFragment.initWidgets();
				}

				// Update activity fragment
				ActivitiesFragment activityFragment = (ActivitiesFragment) getSupportFragmentManager()
						.findFragmentByTag("android:switcher:" + R.id.pager + ":2");
				if (activityFragment != null && activityFragment.getView() != null) {
					activityFragment.updateList();
				}
				
				Toast.makeText(this, R.string.activity_save, Toast.LENGTH_SHORT).show();
			}
			else if(resultCode == SaveActivityActivity.RESULT_DUMP) {
				Toast.makeText(this, R.string.activity_dump, Toast.LENGTH_SHORT).show();
			}
			
		}
		if(requestCode == ActivitiesFragment.RECORD_DETAIL_REQUEST) {
			if (resultCode == ActivityRecordActivity.DUMP_RECORD) {
				Toast.makeText(this, R.string.activity_dump, Toast.LENGTH_SHORT).show();
				
				// Update activity fragment
				ActivitiesFragment activityFragment = (ActivitiesFragment) getSupportFragmentManager()
						.findFragmentByTag("android:switcher:" + R.id.pager + ":2");
				if (activityFragment != null && activityFragment.getView() != null) {
					activityFragment.updateList();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		StartFragment fragment = (StartFragment) getSupportFragmentManager()
				.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
		if (fragment != null && fragment.isInLayout()) {
			fragment.retestLocationAccuracy();
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
			StartFragment fragment = (StartFragment) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
			if (fragment != null && fragment.getView() != null) {
				fragment.startUpateLocation();
			}
		}
		else {
			StartFragment fragment = (StartFragment) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
			if (fragment != null && fragment.getView() != null) {
				fragment.stopUpateLocation();
			}
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Users select other modes.
		Intent i;
	    switch (item.getItemId()) {
	    case R.id.action_settings:
	    	i = new Intent(this, SettingActivity.class);
	        startActivityForResult(i,0);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
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
