package ca.ece.utoronto.ece1780.runningapp.preference;

import ca.ece.utoronto.ece1780.runningapp.activity.R;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment {
	
	private EditTextPreference namePref;
	private ListPreference genderPref;
	private ListPreference distanceUnitPref;
	private WeightPickerPreference weightPref;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.preference_screen_setting);
        
        // Set name preference
        namePref =  (EditTextPreference)findPreference(getString(R.string.pref_key_name));
        String currentName = namePref.getText();
        if(currentName != null)
        	namePref.setSummary(currentName);
        
        namePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference pref, Object val) {
		        namePref.setSummary((CharSequence) val);
				return true;
			}
        });
        
        // Set gender preference
        genderPref = (ListPreference)findPreference(getString(R.string.pref_key_gender));
        CharSequence currentGender = genderPref.getEntry();
        String value = genderPref.getValue();
        if(currentGender != null && !value.equals("notset"))
        	genderPref.setSummary(currentGender);
        else
        	genderPref.setSummary(null);
        
        genderPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference pref, Object val) {
		        int index = genderPref.findIndexOfValue((String) val);
		        CharSequence entry = genderPref.getEntries()[index];
		        if(!val.equals("notset"))
		        	genderPref.setSummary(entry);
		        else
		        	genderPref.setSummary(null);
		        	
				return true;
			}
        });
        
        // Set distance unit preference
        distanceUnitPref = (ListPreference)findPreference(getString(R.string.pref_key_distance_unit));
        CharSequence currentDistanceUnit = distanceUnitPref.getEntry();
        if(currentDistanceUnit != null && currentDistanceUnit != "")
        	distanceUnitPref.setSummary(currentDistanceUnit);
        else
        	distanceUnitPref.setSummary("Kilometre");
        
        distanceUnitPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference pref, Object val) {
		        int index = distanceUnitPref.findIndexOfValue((String) val);
		        CharSequence entry = distanceUnitPref.getEntries()[index];
		        distanceUnitPref.setSummary(entry);
		        	
				return true;
			}
        });
        // Set weight
        weightPref = (WeightPickerPreference)findPreference(getString(R.string.pref_key_weight));
        String currentWeight = weightPref.getWeight();
        if(currentWeight != null) {
        	weightPref.setSummary(currentWeight);
        }
        // This pref updates summary by itself
    }
}
