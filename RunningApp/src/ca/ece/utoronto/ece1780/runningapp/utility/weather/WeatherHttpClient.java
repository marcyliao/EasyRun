package ca.ece.utoronto.ece1780.runningapp.utility.weather;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

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

	public byte[] getImage(String code) {
		HttpURLConnection con = null ;
		InputStream is = null;
		try {
			con = (HttpURLConnection) ( new URL(IMG_URL + code)).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(false);
			con.setConnectTimeout(5000);
			con.setReadTimeout(6000);
			con.connect();

			// Let's read the response
			is = con.getInputStream();
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			while ( is.read(buffer) != -1)
				baos.write(buffer);

			Log.d("weather","get IMAGE DATA successfully");
			return baos.toByteArray();
	    }
		catch(Throwable t) {
			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}

		Log.d("weather","fail to get IMAGE DATA");
		return null;

	}
}