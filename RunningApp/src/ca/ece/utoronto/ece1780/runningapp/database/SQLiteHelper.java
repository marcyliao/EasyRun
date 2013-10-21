package ca.ece.utoronto.ece1780.runningapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "runningapp.db";
	
	// Increase version to call onUpdate. 
	private static final int DATABASE_VERSION = 3;
	
	private static final String DATABASE_CREATE_ACTIVITY_RECORD = "create table activity_record (_id integer primary key not null,"
			+ " time integer not null,"
			+ " distance real,"
			+ " avg_speed real,"
			+ " calories integer,"
			+ " location_points_str text,"
			+ " location_points_time_str text,"
			+ " weather integer,"
			+ " temperature integer,"
			+ " time_length integer,"
			+ " heart_rate integer,"
			+ " mood integer,"
			+ " note text,"
			+ " goal real"
			+ ");";

	private static final String DATABASE_DROP_ACTIVITY_RECORD = "DROP TABLE IF EXISTS activity_record";
	
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_ACTIVITY_RECORD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL(DATABASE_DROP_ACTIVITY_RECORD);
		onCreate(database);
	}

}
