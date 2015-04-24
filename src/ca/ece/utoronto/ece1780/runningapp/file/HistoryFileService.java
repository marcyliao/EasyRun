package ca.ece.utoronto.ece1780.runningapp.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;

public class HistoryFileService {
	public String storeHistoryFile(Context context,
			List<ActivityRecord> records) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		String json = gson.toJson(records);
		return writeToFile(context, new Date(), json);
	}
	
	public List<ActivityRecord> loadHistoryFile(Context context, Uri fileUri){
		String json = readFromFile(context, fileUri);
		if(json == null)
			return null;
		
		try {
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
			return gson.fromJson(json, new TypeToken<List<ActivityRecord>>(){}.getType());
		}
		catch(Exception e) {
			return null;
		}
		
	}
	
	public String readFromFile(Context context, Uri fileUri) {
		try {
			String realPath = getPath(context,fileUri);
			File file = new File(realPath);
			BufferedReader in = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file), "UTF8"));

			StringBuilder json = new StringBuilder();
			String str;
			while ((str = in.readLine()) != null) {
				json.append(str);
			}
			
			in.close();
			return json.toString();
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public static String getPath(Context context, Uri uri) throws URISyntaxException {
	    if ("content".equalsIgnoreCase(uri.getScheme())) {
	        String[] projection = { "_data" };
	        Cursor cursor = null;

	        try {
	            cursor = context.getContentResolver().query(uri, projection, null, null, null);
	            int column_index = cursor.getColumnIndexOrThrow("_data");
	            if (cursor.moveToFirst()) {
	                return cursor.getString(column_index);
	            }
	        } catch (Exception e) {
	            // Eat it
	        }
	    }
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}

	private String writeToFile(Context context, Date date, String data) {
		try {
			String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/EasyRun/";
			//String rootPath = Environment.getDataDirectory().getAbsolutePath()  + "/EasyRun/";
            File dir = new File(rootPath);
            if(!dir.exists()){
                dir.mkdirs();
                ((Activity)context).sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dir)));
            }
            String fileName = "history-"+date.getTime()+".er";
            File file = new File(dir, fileName);
            file.createNewFile();
            OutputStreamWriter escritor = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            escritor.write(data);
            escritor.flush();
            escritor.close();

            ((Activity)context).sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

			return rootPath+fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
