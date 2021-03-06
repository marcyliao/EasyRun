package ca.ece.utoronto.ece1780.runningapp.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ActivityRecordDAO {
	private static final String TABLE_NAME = "activity_record";
	private SQLiteDatabase database = null;
	private SQLiteHelper dbHelper = null;

	final public static String[] ALL_COLUMNS = { "_id", "time", "distance",
			"avg_speed", "calories", "location_points_str",
			"location_points_time_str", "weather", "temperature",
			"time_length", "heart_rate", "mood", "note", "goal"};

	public ActivityRecordDAO(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void insertRecord(ActivityRecord record) {
		this.open();
		ContentValues values = getContentValuesFromRecord(record);
		database.insert(TABLE_NAME, null, values);
		this.close();
	}

	public ActivityRecord getRecord(Long id) {
		ActivityRecord Record = null;
		this.open();
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, "_id=?",
				new String[] { id.toString() }, null, null, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			Record = cursorToRecord(cursor,true);
		}

		cursor.close();
		this.close();
		return Record;
	}

	public void deleteRecord(Long id) {
		this.open();
		database.delete(TABLE_NAME, "_id=?", new String[] { id.toString() });
		this.close();
	}

	public List<ActivityRecord> getAllRecords() {
		return getAllRecords(false);
	}
	
	public List<ActivityRecord> getAllRecords(boolean loadPoints) {
		this.open();
		List<ActivityRecord> Records = new ArrayList<ActivityRecord>();

		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, null, null,
				null, null, "time DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ActivityRecord f = cursorToRecord(cursor,loadPoints);
			Records.add(f);
			cursor.moveToNext();
		}

		cursor.close();
		this.close();
		return Records;
	}

	private ContentValues getContentValuesFromRecord(ActivityRecord Record) {
		ContentValues values = new ContentValues();
		values.put(ALL_COLUMNS[1], Record.getTime());
		values.put(ALL_COLUMNS[2], Record.getDistance());
		values.put(ALL_COLUMNS[3], Record.getAvgSpeed());
		values.put(ALL_COLUMNS[4], Record.getCalories());
		values.put(ALL_COLUMNS[5], Record.getLocationPointsStr());
		values.put(ALL_COLUMNS[6], Record.getLocationPointsTimeStr());
		values.put(ALL_COLUMNS[7], Record.getWeather());
		values.put(ALL_COLUMNS[8], Record.getTemperature());
		values.put(ALL_COLUMNS[9], Record.getTimeLength());
		values.put(ALL_COLUMNS[10], Record.getHeartRate());
		values.put(ALL_COLUMNS[11], Record.getMood());
		values.put(ALL_COLUMNS[12], Record.getNote());
		values.put(ALL_COLUMNS[13], Record.getGoal());

		return values;
	}
	
	// Parameter weak: control loading the big data or not
	private ActivityRecord cursorToRecord(Cursor cursor, boolean weak) {
		ActivityRecord r = new ActivityRecord();
		r.setId(cursor.getLong(0));
		r.setTime(cursor.getLong(1));
		r.setDistance(cursor.getDouble(2));
		r.setAvgSpeed(cursor.getFloat(3));
		r.setCalories(cursor.getInt(4));
		if(weak) {
			r.setLocationPointsStr(cursor.getString(5));
			r.setLocationPointsTimeStr(cursor.getString(6));
		}	
		r.setWeather(cursor.getInt(7));
		r.setTemperature(cursor.getInt(8));
		r.setTimeLength(cursor.getLong(9));
		r.setHeartRate(cursor.getInt(10));
		r.setMood(cursor.getInt(11));
		r.setNote(cursor.getString(12));
		r.setGoal(cursor.getFloat(13));
		return r;
	}
	
	public Map<String,String> getHistory() {
		Map<String,String> map = new HashMap<String,String>();
		
		int totalCalories = 0;
		float totalDistance = 0;
		float totalDuration = 0;
		int totalRuns = 0;
		
		List<ActivityRecord> l = getAllRecords();
		for(ActivityRecord r : l) {
			totalCalories += r.getCalories();
			totalDistance += r.getDistance();
			totalDuration += r.getTimeLength();
			totalRuns++;
		}
		
		map.put("totalCalories", String.valueOf(totalCalories));
		map.put("totalDistance", String.valueOf(totalDistance));
		map.put("totalDuration", String.valueOf(totalDuration));
		map.put("totalRuns", String.valueOf(totalRuns));
		
		return map;
	}
	


}
