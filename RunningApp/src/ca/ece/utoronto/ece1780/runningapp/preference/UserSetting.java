package ca.ece.utoronto.ece1780.runningapp.preference;

import ca.ece.utoronto.ece1780.runningapp.view.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserSetting {
	private Context context;
	
	public UserSetting(Context context) {
		this.context = context;
	}
	
	public void setTargetDistance(float target) {
		SharedPreferences setting = context.getSharedPreferences("setting",
				Context.MODE_PRIVATE);
		setting.edit().putFloat("target", target).commit();
	}
	
	public float getTargetDistance() {
		SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
		float target = setting.getFloat("target", 0.0F);
		return target;
	}

	public int getWeight(){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String weight = sharedPref.getString(context.getString(R.string.pref_key_weight), "");
		if(weight != null && !weight.equals(""))
			return Integer.valueOf(weight);
		return 65;
	}
	
	public String getDistanceUnit(){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String unit = sharedPref.getString(context.getString(R.string.pref_key_distance_unit), "kilometre");
		return unit;
	}
	
	public boolean isSpeechEnabled(){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(context.getString(R.string.pref_key_enable_speech_report), true);
	}
}
