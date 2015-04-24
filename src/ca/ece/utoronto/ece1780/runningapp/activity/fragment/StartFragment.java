package ca.ece.utoronto.ece1780.runningapp.activity.fragment;

import java.util.Map;

import org.json.JSONException;

import ca.ece.utoronto.ece1780.runningapp.activity.dialog.SettingGoalDialogFragment;
import ca.ece.utoronto.ece1780.runningapp.database.ActivityRecordDAO;
import ca.ece.utoronto.ece1780.runningapp.service.ActivityControllerService;
import ca.ece.utoronto.ece1780.runningapp.utility.AsyTaskUtility;
import ca.ece.utoronto.ece1780.runningapp.utility.FormatProcessor;
import ca.ece.utoronto.ece1780.runningapp.utility.weather.JSONWeatherParser;
import ca.ece.utoronto.ece1780.runningapp.utility.weather.Weather;
import ca.ece.utoronto.ece1780.runningapp.utility.weather.WeatherHttpClient;
import ca.ece.utoronto.ece1780.runningapp.activity.R;

import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StartFragment extends Fragment implements LocationListener {
	
	// Different levels of GPS accuracy
	private static final int GPS_SIGNAL_VERY_LOW_LEVEL = 50;
	private static final int GPS_SIGNAL_LOW_LEVEL = 30;
	private static final int GPS_SIGNAL_MEDIUM_LEVEL = 15;
	private static final int GPS_SIGNAL_STRONG_LEVEL = 5;
	
	// GPS control
	private LocationManager locationManager;
	private static long MIN_TIME_INTERVAL_FOR_UPDATE = 2000;
	
	// location used to test GPS accuracy
	private Location testLocation;
	
	private View rootView;
	
	private boolean isWeatherObtained;
	
	public StartFragment() {
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		
		isWeatherObtained = false;
		
		rootView = inflater.inflate(R.layout.fragment_start,
				container, false);
		
		// Initialize GPS service
		// Get the location manager
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !ActivityControllerService.isServiceRunning) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL_FOR_UPDATE, 0, this);
			
			// Get Weather based on last location
			Location locationForWeather = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (!isWeatherObtained && locationForWeather!=null) {
				isWeatherObtained = true;
				prepareWeatherInfo(locationForWeather.getLatitude(),locationForWeather.getLongitude());
			}
			
		}
		prepareWidgets();
		return rootView;
	}
	
	public void prepareWidgets() {
		
		Map<String,String> history = new ActivityRecordDAO(getActivity()).getHistory();
		double totalDistance = Float.valueOf(history.get("totalDistance"));
		float totalDuration = Float.valueOf(history.get("totalDuration"));
		int totalCalories = Integer.valueOf(history.get("totalCalories"));
		int totalRuns = Integer.valueOf(history.get("totalRuns"));
		
		FormatProcessor fp = new FormatProcessor(rootView.getContext());
		
		TextView distanceTextView = (TextView)rootView.findViewById(R.id.TextViewTotalDistance);
		(distanceTextView).setText(fp.getDistance(totalDistance));
		
		double avgSpeed = totalDistance == 0?0:(totalDistance*3600)/(totalDuration);
		((TextView)rootView.findViewById(R.id.TextViewAverageSpeed)).setText(fp.getSpeed(avgSpeed));
		((TextView)rootView.findViewById(R.id.TextViewRuns)).setText(String.valueOf(totalRuns));
		((TextView)rootView.findViewById(R.id.TextViewCalories)).setText(String.valueOf(totalCalories));
		
		initStartButton(rootView);
		
		// deal with different distance units
		prepareDistanceUnitWidget(getActivity(),rootView);
		
	}
	
	private void prepareDistanceUnitWidget(Context context, View rootView) {
		FormatProcessor fp = new FormatProcessor(context);
		((TextView)rootView.findViewById(R.id.textViewDistanceUnit)).setText(fp.getDistanceUnit());
		((TextView)rootView.findViewById(R.id.textViewSpeedUnit)).setText(fp.getSpeedUnit());
	}

	// Initialize button according to whether gps is enabled or not
	private void initStartButton(View rootView) {

		Button startButton = (Button)rootView.findViewById(R.id.buttonStart);
		
		// If GPS is enabled. Users are able to start activity
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			
			// Show the start button. Once the button is clicked, activity of running starts
			startButton.setText(R.string.button_start);
			startButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					// When gps accuracy is low, use a dialog to notify user before start
					if (testLocation == null || testLocation.getAccuracy() > GPS_SIGNAL_MEDIUM_LEVEL) {

						new AlertDialog.Builder(getActivity())
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setTitle(R.string.gps_low_dialog_title)
								.setMessage(R.string.gps_low_dialog_info)
								.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
									@Override
									public void onClick( DialogInterface dialog, int which) {
										SettingGoalDialogFragment f =new SettingGoalDialogFragment();
										f.show(getFragmentManager(),"");
									}
								})
								.setNegativeButton(R.string.no, null).show();

					} else {
						SettingGoalDialogFragment f =new SettingGoalDialogFragment();
						f.show(getFragmentManager(),"");
					}
				}
				
			});
		} else {
			// If gps is not enabled, let users go to the setting page to turn on GPS
			startButton.setText(R.string.enable_gps);
			startButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					  startActivity(intent);
				}
				
			});
		}
	}

	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if (isVisibleToUser) { }
	}
	
	// If location is changed, test GPS signal and show to user
	@Override
	public void onLocationChanged(Location location) {
		testLocation = location;
		if (testLocation!=null || getView() != null) {
			Log.d("GPS test", ""+testLocation.getAccuracy());
			
			ImageView signalView = (ImageView) getView().findViewById(
					R.id.imageViewGpsSignal);

			if (testLocation == null) {
				signalView.setImageResource(R.drawable.icon_signal_0);
				return;
			}
			
			
			// Update weather
			if (!isWeatherObtained) {	
				isWeatherObtained = true;
				prepareWeatherInfo(testLocation.getLatitude(),testLocation.getLongitude());
			}
			
			if (location.getAccuracy() < GPS_SIGNAL_STRONG_LEVEL) {
				signalView.setImageResource(R.drawable.icon_signal_4);
			} else if (location.getAccuracy() < GPS_SIGNAL_MEDIUM_LEVEL) {
				signalView.setImageResource(R.drawable.icon_signal_3);
			} else if (location.getAccuracy() < GPS_SIGNAL_LOW_LEVEL) {
				signalView.setImageResource(R.drawable.icon_signal_2);
			} else if (location.getAccuracy() < GPS_SIGNAL_VERY_LOW_LEVEL) {
				signalView.setImageResource(R.drawable.icon_signal_1);
			} else {
				signalView.setImageResource(R.drawable.icon_signal_0);
			}
		}
	}

	private void prepareWeatherInfo(double lat, double lng) {
		JSONWeatherTask task = new JSONWeatherTask();
		AsyTaskUtility.executeAsyncTask(task,new String[]{"lat="+lat+"&lon="+lng});
	}
	
	private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

		@Override
		protected Weather doInBackground(String... params) {
			Weather weather = null;
			try {
				String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));
				
				if(data == null)
					return null;
			
				weather = JSONWeatherParser.getWeather(data);

			} catch (JSONException e) {				
				e.printStackTrace();
			}
			return weather;

		}
		
		@Override
		protected void onPostExecute(Weather weather) {			
			super.onPostExecute(weather);
			try {
				if(weather == null) {
					TextView t = (TextView) rootView.findViewById(R.id.TextViewWeatherLoading);
					t.setVisibility(TextView.VISIBLE);
					t.setText(R.string.no_weather_advice);
					
					isWeatherObtained = false;
					Log.d("weather","could not get weather");
					return;
				}

				ImageView imgViewWeather = (ImageView) rootView.findViewById(R.id.imageViewWeather);
				
				new WeatherHttpClient().attachImage(weather.currentCondition.getIcon(),imgViewWeather);
				
				TextView textViewTemperature = (TextView) rootView.findViewById(R.id.TextViewTemperature);
				textViewTemperature.setText(String.valueOf(Math.round((weather.temperature.getTemp() - 275.15))) +  getString(R.string.centigrade));
				TextView textViewWeatherDes = (TextView) rootView.findViewById(R.id.TextViewWeatherDesc);
				textViewWeatherDes.setText(weather.currentCondition.getDescr());
				
				rootView.findViewById(R.id.TextViewWeatherLoading).setVisibility(View.INVISIBLE);
				Log.d("weather","get weather successfully");
			}
			catch(Throwable t) {
				t.printStackTrace();
			}

		}
	}




	

	@Override
	public void onProviderDisabled(String provider) {
		// do nothing
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// do nothing
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// do nothing
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
	}

	@Override
	public void onPause() {
		stopUpateLocation();
		super.onPause();
	}

	@Override
	public void onResume() {
		startUpateLocation();
		super.onResume();
	}

	
	public void startUpateLocation() { 
		if(locationManager != null) {
			initStartButton(rootView);
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL_FOR_UPDATE, 0, this);
			}
		}
	}
	
	public void stopUpateLocation() { 
		if(locationManager != null) {
			initStartButton(rootView);
			locationManager.removeUpdates(this);
		}
	}
	
	public void retestLocationAccuracy() {
		testLocation = null;
	}

}
