package ca.ece.utoronto.ece1780.runningapp.utility.weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.util.Log;
import android.widget.ImageView;

public class WeatherHttpClient {

	private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
	private static String IMG_URL = "http://openweathermap.org/img/w/";


	public String getWeatherData(String params) {
		HttpURLConnection con = null ;
		InputStream is = null;

		try {
			con = (HttpURLConnection) ( new URL(BASE_URL + params)).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();

			// Let's read the response
			StringBuffer buffer = new StringBuffer();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while (  (line = br.readLine()) != null )
				buffer.append(line + "\r\n");

			is.close();
			con.disconnect();
			Log.d("weather","get JSON successfully");
			return buffer.toString();
	    }
		catch(Throwable t) {
			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}

		Log.d("weather","fail to get JSON");
		return null;

	}
	
	public void attachImage(String code, ImageView view) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(view.getContext()).build();
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.build();

		imageLoader.displayImage(IMG_URL + code, view, options);
	}
}