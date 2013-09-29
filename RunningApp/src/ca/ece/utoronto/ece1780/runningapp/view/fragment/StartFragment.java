package ca.ece.utoronto.ece1780.runningapp.view.fragment;

import ca.ece.utoronto.ece1780.runningapp.view.R;
import ca.ece.utoronto.ece1780.runningapp.view.RunningExerciseActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class StartFragment extends Fragment {

	public StartFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_start,
				container, false);
		
		// set up start button
        rootView.findViewById(R.id.buttonStart).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Show dialog to let users set goal of the activity
                Intent intent = new Intent(getActivity(), RunningExerciseActivity.class);
                StartFragment.this.startActivityForResult(intent, 0);
			}
        	
        });
		
		return rootView;
	}
}
