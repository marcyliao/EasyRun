package ca.ece.utoronto.ece1780.runningapp.activity.dialog;

import ca.ece.utoronto.ece1780.runningapp.activity.RunningExerciseActivity;
import ca.ece.utoronto.ece1780.runningapp.preference.UserSetting;
import ca.ece.utoronto.ece1780.runningapp.utility.FormatProcessor;
import ca.ece.utoronto.ece1780.runningapp.activity.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

public class SettingGoalDialogFragment extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View root = inflater.inflate(R.layout.dialog_set_goal, null);
	    
	    // Initialize number picker for setting target
	    final NumberPicker np1 = (NumberPicker) root.findViewById(R.id.np1);
	    np1.setMaxValue(50);
	    final NumberPicker np2 = (NumberPicker) root.findViewById(R.id.np2);
	    np2.setMaxValue(9);
	    float targetOfLastTime = new UserSetting(getActivity()).getTargetDistance();
	    int np1Value = (int)targetOfLastTime;
	    int np2Value = (int)(targetOfLastTime*10)%10;
	    np1.setValue(np1Value);
	    np2.setValue(np2Value);	
	    
	    // deal with different units
	    prepareDistanceUnitWidget(getActivity(), root);
	    
	    // Initialize dialog
	    builder.setView(root);
		builder.setTitle(R.string.whats_your_target_distance)
			.setPositiveButton(R.string._continue,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
							
						float goal = Float.valueOf(np1.getValue() + "." + np2.getValue());
						new UserSetting(getActivity()).setTargetDistance(goal);
						
						// Start running activity
						Intent i = new Intent(getActivity(),RunningExerciseActivity.class);

						FormatProcessor fp = new FormatProcessor(getActivity());
						i.putExtra("goal", fp.getGoalFromUserInput(goal));
						getActivity().startActivity(i);
						getActivity().finish();
					}
				})
			.setNegativeButton(R.string.no_target,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(getActivity(),RunningExerciseActivity.class);
						getActivity().startActivity(i);
						getActivity().finish();
					}
				});

		// Create the AlertDialog object and return it
		return builder.create();
	}	
	
	private void prepareDistanceUnitWidget(Context context, View rootView) {
		FormatProcessor fp = new FormatProcessor(context);
		((TextView)rootView.findViewById(R.id.textViewDistanceUnit)).setText(fp.getDistanceUnit());
	}
}
