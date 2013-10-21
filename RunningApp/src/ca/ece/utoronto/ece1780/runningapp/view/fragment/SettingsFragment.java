package ca.ece.utoronto.ece1780.runningapp.view.fragment;

import ca.ece.utoronto.ece1780.runningapp.view.R;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment {
	
	private EditTextPreference namePref;
	private ListPreference genderPref;
	
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
        if(currentGender != null)
        	genderPref.setSummary(currentGender);
        
        genderPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference pref, Object val) {
		        int index = genderPref.findIndexOfValue((String) val);
		        CharSequence entry = genderPref.getEntries()[index];
		        genderPref.setSummary(entry);
				return true;
			}
        });
        
        
    }
}
