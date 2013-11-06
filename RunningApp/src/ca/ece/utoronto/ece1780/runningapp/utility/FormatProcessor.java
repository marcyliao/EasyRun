package ca.ece.utoronto.ece1780.runningapp.utility;

import android.content.Context;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;

public class FormatProcessor {
	private Context context;
	
	public FormatProcessor(Context context) {
		this.context = context;
	}
	
	public String getDistance(Double distance) {
		return String.format("%.2f",Double.valueOf(distance)/1000);
	}

	public String getDuration(Long duration) {
		return UtilityCaculator.getFormatStringFromDuration((int)(duration/1000));
	}
	
	public String getSpeed(double d) {
		return String.format("%.1f",d);
	}

	public String getPace(float avgPace) {
		return String.format("%.1f",avgPace);
	}

	public CharSequence getCalories(int calories) {
		return String.valueOf(calories);
	}

	public CharSequence getCaloriesSpeed(float caloriesSpeed) {
		return String.valueOf(Integer.valueOf((int)caloriesSpeed));
	}
	
}
