package ca.ece.utoronto.ece1780.runningapp.setting;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSetting {
	private Context context;
	
	public UserSetting(Context context) {
		this.context = context;
	}
	
	/*
	public void updateTotalDistance(float distance) {
		SharedPreferences setting = context.getSharedPreferences("setting",
				Context.MODE_PRIVATE);
		setting.edit().putFloat("total_distance", distance).commit();
	}
	
	public Double getDistance() {
		SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
		Double distance = (double) setting.getFloat("total_distance", 0F);
		return distance;
	}
	
	public void updateRuns(int runs) {
		SharedPreferences setting = context.getSharedPreferences("setting",
				Context.MODE_PRIVATE);
		setting.edit().putInt("total_runs", runs).commit();
	}
	
	public Integer getRuns() {
		SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
		Integer runs = setting.getInt("total_runs", 0);
		return runs;
	}
	
	public void updateCalories(int calories) {
		SharedPreferences setting = context.getSharedPreferences("setting",
				Context.MODE_PRIVATE);
		setting.edit().putInt("total_calories", calories).commit();
	}
	
	public Integer getCalories() {
		SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
		Integer calories = setting.getInt("total_calories", 0);
		return calories;
	}
	
	public void updateTotalTime(Long time) {
		SharedPreferences setting = context.getSharedPreferences("setting",
				Context.MODE_PRIVATE);
		setting.edit().putLong("total_time", time).commit();
	}
	
	public Long getTotalTime() {
		SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
		Long totalTime = setting.getLong("total_time", 0L);
		return totalTime;
	}
	*/
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

}
