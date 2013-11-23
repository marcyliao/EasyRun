package ca.ece.utoronto.ece1780.runningapp.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;

public class FormatProcessor {
	private Context context;
	
	public FormatProcessor(Context context) {
		this.context = context;
	}
	
	// distance unit: m
	public String getDistance(Double distance) {
		return String.format("%.2f",Double.valueOf(distance)/1000);
	}
	
	// duration unit: ms
	public String getDuration(Long duration) {
		return UtilityCaculator.getFormatStringFromDuration((int)(duration/1000));
	}
	
	// speed unit: km/h
	public String getSpeed(double speed) {
		return String.format("%.1f",speed);
	}

	// pace unit: min/km
	public String getPace(float avgPace) {
		return String.format("%.1f",avgPace);
	}

	public CharSequence getCalories(int calories) {
		return String.valueOf(calories);
	}

	// unit: kcal/h
	public CharSequence getCaloriesSpeed(float caloriesSpeed) {
		return String.valueOf(Integer.valueOf((int)caloriesSpeed));
	}
	
	public String getDate(long time) {
		Date date = new Date(time);
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.US);
		String formatedDate = dateformatYYYYMMDD.format(date);
		return formatedDate;
	}
	
}
