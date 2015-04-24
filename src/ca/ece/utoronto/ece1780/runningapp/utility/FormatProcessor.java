package ca.ece.utoronto.ece1780.runningapp.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.ece.utoronto.ece1780.runningapp.preference.UserSetting;

import android.content.Context;

public class FormatProcessor {
	private Context context;
	private enum DistanceUnit {KM,MILE};
	private DistanceUnit distanceUnit;
	private double RATE_KM_TO_MILE = 0.62137d;
	
	public FormatProcessor(Context context) {
		this.context = context;
		
		String unit = new UserSetting(context).getDistanceUnit();
		if(unit.equals("kilometre"))
			distanceUnit = DistanceUnit.KM;
		else if (unit.equals("mile"))
			distanceUnit = DistanceUnit.MILE;
			
	}
	
	// distance unit: m
	public String getDistance(Double distance) {
		if(distanceUnit == DistanceUnit.MILE)
			distance *= RATE_KM_TO_MILE;
		return String.format("%.2f",Double.valueOf(distance)/1000);
	}
	
	public double getDistanceValue(Double distance) {
		if(distanceUnit == DistanceUnit.MILE)
			distance *= RATE_KM_TO_MILE;
		return distance/1000.0;
	}
	
	public String getDistanceUnit(){
		if(distanceUnit == DistanceUnit.MILE)
			return "mile";
		else// if(distanceUnit == DistanceUnit.KM)
			return "km";
	}
	
	// duration unit: ms
	public String getDuration(Long duration) {
		return UtilityCaculator.getFormatStringFromDuration((int)(duration/1000));
	}
	
	// speed unit: km/h
	public String getSpeed(double speed) {
		if(distanceUnit == DistanceUnit.MILE)
			speed *= RATE_KM_TO_MILE;
		return String.format("%.1f",speed);
	}

	public float getGoalFromUserInput(float goal){
		if(distanceUnit == DistanceUnit.MILE)
			return (float)(goal / RATE_KM_TO_MILE);
		else //if (distanceUnit == DistanceUnit.KM)
			return goal;
	}
	
	public String getSpeedUnit(){
		if(distanceUnit == DistanceUnit.MILE)
			return "mph";
		else// if(distanceUnit == DistanceUnit.KM)
			return "km/h";
	}
	
	// pace unit: min/km or min/mile
	public String getPace(float avgPace) {
		if(distanceUnit == DistanceUnit.MILE)
			avgPace /= (RATE_KM_TO_MILE);
		return String.format("%.1f",avgPace);
	}
	
	public String getPaceUnit(){
		if(distanceUnit == DistanceUnit.MILE)
			return "min/mile";
		else// if(distanceUnit == DistanceUnit.KM)
			return "min/km";
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
