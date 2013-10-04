package ca.ece.utoronto.ece1780.runningapp.data;

import java.util.ArrayList;
import java.util.Date;

import android.location.Location;

public class ActivityRecord {
	
	//unique id
	private Long id;

	// time and date of the activity
	private Long time;

	// Total distance (current distance while recording). Unit: km
	private double distance;
	
	// Average speed. Unit: km/h
	private float avgSpeed;
	
	// Total calories (burned calories so far while recording).
	private int calories;

	// All the location points of the route of the activity. Record a point every 5 s
	private ArrayList<Location> locationPoints;
	
	// Converted from the location points in order to store in database
	// Format: x1,y1,x2,y2,x3,y3 .... xn,yn
	private String locationPointsStr;
	
	// Time of all the location points
	private ArrayList<Date> locationPointsTime;
	
	// Converted from the location points time in order to store in database
	// Format: t1,t2,t3,...,tn
	private String locationPointsTimeStr;
	
	// Weather of the day
	private int weather;
	
	// Temperature of the day. Unit: degree centigrade
	private int temperature;
	
	// Time length since the start of the activity
	private Long timeLength;

	public ActivityRecord() {
		id = 0L;
		time = new Date().getTime();
		distance = 0;
		avgSpeed = 0;
		calories = 0;
		locationPoints = new ArrayList<Location>();
		locationPointsTime = new ArrayList<Date>();
		weather = Weather.NONSET;
		temperature = 0;
		timeLength = 0L;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public float getAvgSpeed() {
		return avgSpeed;
	}

	public void setAvgSpeed(float avgSpeed) {
		this.avgSpeed = avgSpeed;
	}

	public int getCalories() {
		return calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
	}

	public ArrayList<Location> getLocationPoints() {
		return locationPoints;
	}

	public void setLocationPoints(ArrayList<Location> locationPoints) {
		this.locationPoints = locationPoints;
	}

	public String getLocationPointsStr() {
		return locationPointsStr;
	}

	public void setLocationPointsStr(String locationPointsStr) {
		this.locationPointsStr = locationPointsStr;
	}

	public int getWeather() {
		return weather;
	}

	public void setWeather(int weather) {
		this.weather = weather;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public ArrayList<Date> getLocationPointsTime() {
		return locationPointsTime;
	}

	public void setLocationPointsTime(ArrayList<Date> pointsTime) {
		this.locationPointsTime = pointsTime;
	}

	public String getLocationPointsTimeStr() {
		return locationPointsTimeStr;
	}

	public void setSpeedsStr(String locationPointsTimeStr) {
		this.locationPointsTimeStr = locationPointsTimeStr;
	}

	public Long getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(Long timeLength) {
		this.timeLength = timeLength;
	}
}
