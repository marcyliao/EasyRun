package ca.ece.utoronto.ece1780.runningapp.preference;

import ca.ece.utoronto.ece1780.runningapp.view.R;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class WeightPickerPreference extends DialogPreference {

	public WeightPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_set_weight);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
	}

}
