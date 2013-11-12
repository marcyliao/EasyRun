package ca.ece.utoronto.ece1780.runningapp.preference;

import ca.ece.utoronto.ece1780.runningapp.view.R;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class WeightPickerPreference extends DialogPreference {

	private String weight;
	private EditText weightEditText;
	
	public WeightPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_set_weight);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        this.setSummary(getPersistedString(null));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    // When the user selects "OK", persist the new value
		
	    if (positiveResult) {
			weight = weightEditText.getText().toString();
	        persistString(weight);
	        this.setSummary(weight);
	    }
	}
	
	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	@Override
	protected void onBindDialogView(View view) {
		weightEditText = (EditText) view.findViewById(R.id.editTextWeight);
		weightEditText.setText(weight);
	    super.onBindDialogView(view);
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
	    if (restorePersistedValue) {
	        // Restore existing state
	    	weight = getPersistedString(null);
	    }
	}
	
	public String getWeight() {
		return getPersistedString(null);
	}
	
	
}
