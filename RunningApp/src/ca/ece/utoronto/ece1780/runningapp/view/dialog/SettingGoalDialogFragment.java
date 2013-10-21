package ca.ece.utoronto.ece1780.runningapp.view.dialog;

import ca.ece.utoronto.ece1780.runningapp.setting.UserSetting;
import ca.ece.utoronto.ece1780.runningapp.view.R;
import ca.ece.utoronto.ece1780.runningapp.view.RunningExerciseActivity;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.StartFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

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
	    
	    // Initialize dialog
	    builder.setView(root);
		builder.setTitle(R.string.whats_your_target_distance)
				.setPositiveButton(R.string._continue,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								float goal = Float.valueOf(np1.getValue() + "." + np2.getValue());
								new UserSetting(getActivity()).setTargetDistance(goal);
								
								Intent i = new Intent(getActivity(),RunningExerciseActivity.class);
								i.putExtra("goal", goal);
								getActivity().startActivityForResult(i,StartFragment.START_ACTIVITY_REQUEST);
							}
						})
				.setNegativeButton(R.string.no_target,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent i = new Intent(getActivity(),RunningExerciseActivity.class);
								getActivity().startActivityForResult(i,StartFragment.START_ACTIVITY_REQUEST);
							}
						});

		// Create the AlertDialog object and return it
		return builder.create();
	}	
}
