package ca.ece.utoronto.ece1780.runningapp.utility;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

public class AsyTaskUtility {
    @SuppressWarnings("unchecked")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static public <T> void executeAsyncTask(AsyncTask<T, ?, ?> task) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      }
      else {
        task.execute();
      }
    }
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	static public <T> void executeAsyncTask(AsyncTask<String, ?, ?> task,
			String[] param) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param);
		} else {
			task.execute(param);
		}
	}
}
