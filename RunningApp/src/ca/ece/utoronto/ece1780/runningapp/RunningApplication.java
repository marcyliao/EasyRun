package ca.ece.utoronto.ece1780.runningapp;

import android.app.Application;

import com.appbrain.AppBrain;

public class RunningApplication  extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
        AppBrain.initApp(this);
	}
}