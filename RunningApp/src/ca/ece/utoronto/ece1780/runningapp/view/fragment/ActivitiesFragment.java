package ca.ece.utoronto.ece1780.runningapp.view.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.database.ActivityRecordDAO;
import ca.ece.utoronto.ece1780.runningapp.view.R;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ActivitiesFragment extends Fragment {

	private ActivityRecordArrayAdapter mListAdapter;

	public ActivitiesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_activity,
				container, false);
	    ListView l = (ListView) rootView.findViewById(R.id.listViewActivities);
	    mListAdapter = new ActivityRecordArrayAdapter(getActivity());
	    l.setAdapter(mListAdapter);
		return rootView;
	}


	public void updateList() {
        mListAdapter.notifyDataSetChanged();
	}
	
	private class ActivityRecordArrayAdapter extends BaseAdapter {
		private final Context context;
		private final List<ActivityRecord> records;

		public ActivityRecordArrayAdapter(Context context) {
			super();
			this.context = context;
			records = new ActivityRecordDAO(context).getAllRecords();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_view_item_activity, parent, false);
			
			TextView textViewDistance = (TextView) rowView.findViewById(R.id.TextViewDistance);
			TextView textViewTime = (TextView) rowView.findViewById(R.id.TextViewTime);
			
			textViewDistance.setText(String.format("%.2f", records.get(position).getDistance()/1000));
			
			Date date = new Date(records.get(position).getTime());
			SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.US);
			textViewTime.setText(dateformatYYYYMMDD.format(date));

			return rowView;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(records != null)
				return records.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
	}
}
