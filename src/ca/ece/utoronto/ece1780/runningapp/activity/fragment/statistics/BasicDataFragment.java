package ca.ece.utoronto.ece1780.runningapp.activity.fragment.statistics;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.utility.FormatProcessor;
import ca.ece.utoronto.ece1780.runningapp.activity.R;

public class BasicDataFragment extends Fragment {
	
	private ActivityRecord record;

	private View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		

		rootView = inflater.inflate(R.layout.fragment_statistic_basis,
				container, false);
		record = (ActivityRecord) getArguments().getSerializable("record");
		prepareWidgets();
		
		return rootView;
	}

	private void prepareWidgets() {
		TextView textViewDuration = (TextView) rootView.findViewById(R.id.TextViewDuration);
		TextView textViewDistance = (TextView) rootView.findViewById(R.id.TextViewDistance);
		TextView textViewAVGSpeed = (TextView) rootView.findViewById(R.id.TextViewAVGSpeed);
		TextView textViewAVGPace = (TextView) rootView.findViewById(R.id.TextViewAVGPace);
		TextView textViewCalories = (TextView) rootView.findViewById(R.id.TextViewCalories);
		TextView textViewCaloriesSpeed = (TextView) rootView.findViewById(R.id.TextViewCaloriesSpeed);
		
		FormatProcessor fp = new FormatProcessor(rootView.getContext());
		textViewDuration.setText(fp.getDuration(record.getTimeLength()));
		textViewDistance.setText(fp.getDistance(record.getDistance()));
		textViewAVGSpeed.setText(fp.getSpeed(record.getAvgSpeed()));
		textViewAVGPace.setText(fp.getPace(record.getAvgPace()));
		textViewCalories.setText(fp.getCalories(record.getCalories()));
		textViewCaloriesSpeed.setText(fp.getCaloriesSpeed(record.getCaloriesSpeed()));
		
		// deal with distance units
		prepareDistanceUnitWidget(getActivity(),rootView);
	}
	
	private void prepareDistanceUnitWidget(Context context, View rootView) {
		FormatProcessor fp = new FormatProcessor(context);
		((TextView)rootView.findViewById(R.id.textViewDistanceUnit)).setText(fp.getDistanceUnit());
		((TextView)rootView.findViewById(R.id.textViewSpeedUnit)).setText(fp.getSpeedUnit());
		((TextView)rootView.findViewById(R.id.textViewPaceUnit)).setText(fp.getPaceUnit());
	}
}
