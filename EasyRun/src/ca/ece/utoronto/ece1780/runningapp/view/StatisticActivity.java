package ca.ece.utoronto.ece1780.runningapp.view;

import java.util.Locale;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.statistics.BasicDataFragment;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.statistics.GraphsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class StatisticActivity extends FragmentActivity {

	public static final int FROM_DB = 0;
	public static final int FROM_CONTROLLER = 1;
	public static final String WHERE_IS_DATA = "where is data";
	
	private ActivityRecord record;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic);
		
		record = (ActivityRecord) getIntent().getExtras().get("record");
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistic, menu);
		return true;
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
			
			if(position == 0) {
				BasicDataFragment fragment = new BasicDataFragment();
				Bundle b = new Bundle();
				b.putSerializable("record", record);
				fragment.setArguments(b);
				return fragment;
			}
			else if(position == 1) {
				GraphsFragment fragment = new GraphsFragment();
				Bundle b = new Bundle();
				b.putSerializable("record", record);
				fragment.setArguments(b);
				return fragment;
			}
			
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_basic_data).toUpperCase(l);
			case 1:
				return getString(R.string.title_graphs).toUpperCase(l);
			}
			return null;
		}
	}

}
