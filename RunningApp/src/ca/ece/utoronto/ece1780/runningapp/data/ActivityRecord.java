package ca.ece.utoronto.ece1780.runningapp.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.location.Location;
import android.location.LocationManager;

public class ActivityRecord implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//unique id
	private Long id;

	// time and date of the activity
	private Long time;

	// Total distance (current distance while recording). Unit: km
	private Double distance;
	
	// Average speed. Unit: km/h
	private Float avgSpeed;
	
	// Total calories (burned calories so far while recording).
	private Integer calories;

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
	private Integer weather;
	
	// Temperature of the day. Unit: degree centigrade
	private Integer temperature;
	
	// Time length since the start of the activity
	private Long timeLength;
	
	// Heart rate
	private Integer heartRate;

	// Mood
	private Integer mood;
	
	// Note
	private String note;
	
	public ActivityRecord() {
		id = 0L;
		time = new Date().getTime();
		distance = 0D;
		avgSpeed = 0F;
		calories = 0;
		locationPoints = new ArrayList<Location>();
		locationPointsTime = new ArrayList<Date>();
		weather = Weather.NONSET;
		temperature = null;
		timeLength = 0L;
		heartRate = null;
		mood = Mood.NONSET;
		note = null;
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

	public Integer getCalories() {
		return calories;
	}

	public void setCalories(Integer calories) {
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

	public Integer getWeather() {
		return weather;
	}

	public void setWeather(Integer weather) {
		this.weather = weather;
	}

	public Integer getTemperature() {
		return temperature;
	}

	public void setTemperature(Integer temperature) {
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

	public void setLocationPointsTimeStr(String locationPointsTimeStr) {
		this.locationPointsTimeStr = locationPointsTimeStr;
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

	public Integer getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(Integer heartRate) {
		this.heartRate = heartRate;
	}

	public Integer getMood() {
		return mood;
	}

	public void setMood(Integer mood) {
		this.mood = mood;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void prepareToSave() {
		// convert location points array to string
		StringBuilder builder = new StringBuilder();
		if (locationPoints.size() > 0) {
			Location value = locationPoints.get(0);
			builder.append(value.getLatitude());
			builder.append(",");
			builder.append(value.getLongitude());
			
			for (Integer i=1; i<locationPoints.size(); i++) {
				builder.append(",");
				builder.append(locationPoints.get(i).getLatitude());
				builder.append(",");
				builder.append(locationPoints.get(i).getLongitude());
			}
		}
		locationPointsStr = builder.toString();
		
		// convert time point array to string
		builder = new StringBuilder();
		if (locationPointsTime.size() > 0) {
			Date value = locationPointsTime.get(0);
			builder.append(value.getTime());
			
			for (Integer i=1; i<locationPointsTime.size(); i++) {
				builder.append(",");
				builder.append(locationPointsTime.get(i).getTime());
			}
		}
		locationPointsTimeStr = builder.toString();
		
	}

	public void prepareToShow() {
		String locationXandY [] = locationPointsStr.split(",");
		String time [] = this.locationPointsTimeStr.split(",");
		for(int i=0; i<time.length-1; i++) {
			// Convert location String list to location list
			Location l = new Location(LocationManager.GPS_PROVIDER);
			l.setLatitude(Double.valueOf(locationXandY[2*i]));
			l.setLongitude(Double.valueOf(locationXandY[2*i+1]));
			locationPoints.add(l);
			
			// Convert location time to time
			locationPointsTime.add(new Date(Long.valueOf(time[i])));
		}
		
	}
}
