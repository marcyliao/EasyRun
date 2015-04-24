package ca.ece.utoronto.ece1780.runningapp.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ca.ece.utoronto.ece1780.runningapp.activity.ActivityRecordActivity;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.database.ActivityRecordDAO;
import ca.ece.utoronto.ece1780.runningapp.utility.AsyTaskUtility;
import ca.ece.utoronto.ece1780.runningapp.utility.FormatProcessor;
import ca.ece.utoronto.ece1780.runningapp.activity.R;

public class ActivitiesFragment extends Fragment {

	public static final int RECORD_DETAIL_REQUEST = 389;
	private ActivityRecordArrayAdapter mListAdapter;
	
	private TextView textViewNoActivity;
	private List<ActivityRecord> records = new ArrayList<ActivityRecord>();
	

	
	public ActivitiesFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_activity,
				container, false);
		
	    prepareWidgets(rootView);
	    
		return rootView;
	}

	private void prepareWidgets(View rootView) {
		ListView l = (ListView) rootView.findViewById(R.id.listViewActivities);
	    
	    textViewNoActivity = (TextView) rootView.findViewById(R.id.textViewNoActivity);
	    mListAdapter = new ActivityRecordArrayAdapter(getActivity());
	    l.setAdapter(mListAdapter);
	   
	    l.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ActivityRecord record = mListAdapter.getRecords().get(position);
				Bundle b = new Bundle();
				if (record != null)
					b.putLong("id", record.getId());
				Intent i = new Intent(getActivity(), ActivityRecordActivity.class);
				i.putExtras(b);

				getActivity().startActivityForResult(i, RECORD_DETAIL_REQUEST);
			}
	    });
	    
	    updateList();
	}


	public void updateList() {
		
		AsyTaskUtility.executeAsyncTask(new AsyncTask<Object,Object,List<ActivityRecord>>(){

			@Override
			protected void onPostExecute(List<ActivityRecord> result) {
				if(mListAdapter != null) {
					records = result;
					mListAdapter.notifyDataSetChanged();

				}
				super.onPostExecute(result);
			}

			@Override
			protected List<ActivityRecord> doInBackground(Object... params) {
				List<ActivityRecord> records = new ActivityRecordDAO(getActivity().getApplicationContext()).getAllRecords();
				return records;
			}
			
		});
		
	}
	
	private class ActivityRecordArrayAdapter extends BaseAdapter {
		private final Context context;

		@Override
		public void notifyDataSetChanged() {
			
			if(records == null || records.size() == 0)
				textViewNoActivity.setVisibility(View.VISIBLE);
			else
				textViewNoActivity.setVisibility(View.INVISIBLE);
			
			super.notifyDataSetChanged();
		}

		public ActivityRecordArrayAdapter(Context context) {
			super();
			this.context = context;
			records = new ActivityRecordDAO(context).getAllRecords();
			
			if(records == null || records.size() == 0)
				textViewNoActivity.setVisibility(View.VISIBLE);
			else
				textViewNoActivity.setVisibility(View.INVISIBLE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_view_item_activity, parent, false);
            
            FormatProcessor fp = new FormatProcessor(rowView.getContext());
            
            TextView textViewDistance = (TextView) rowView.findViewById(R.id.TextViewDistance);
            TextView textViewTime = (TextView) rowView.findViewById(R.id.TextViewTime);
            
            ActivityRecord record = getRecords().get(position);
            
            textViewDistance.setText(fp.getDistance(record.getDistance()));
            textViewTime.setText(fp.getDate(record.getTime()));
            
            prepareDistanceUnitWidget(fp, rowView);
 
            return rowView;
		}
		
		private void prepareDistanceUnitWidget(FormatProcessor fp, View rootView) {
			((TextView)rootView.findViewById(R.id.textViewDistanceUnit)).setText(fp.getDistanceUnit());
		}
		
		@Override
		public int getCount() {
			if(getRecords() != null)
				return getRecords().size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public List<ActivityRecord> getRecords() {
			return records;
		}
	}

}
