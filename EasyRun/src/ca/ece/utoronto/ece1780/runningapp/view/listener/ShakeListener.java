package ca.ece.utoronto.ece1780.runningapp.view.listener;

import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.content.Context;
import java.lang.UnsupportedOperationException;

public class ShakeListener implements SensorListener {
	private static final int FORCE_THRESHOLD = 3500;
	private static final int TIME_THRESHOLD = 50;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 3;

	private SensorManager mSensorMgr;
	private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
	private long mLastTime;
	private OnShakeListener mShakeListener;
	private Context mContext;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;

	public interface OnShakeListener {
		public void onShake();
	}

	public ShakeListener(Context context) {
		mContext = context;
		resume();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		mShakeListener = listener;
	}

	public void resume() {
		mSensorMgr = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorMgr == null) {
			throw new UnsupportedOperationException("Sensors not supported");
		}
		boolean supported = mSensorMgr.registerListener(this,
				SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_DELAY_GAME);
		if (!supported) {
			mSensorMgr.unregisterListener(this,
					SensorManager.SENSOR_ACCELEROMETER);
			throw new UnsupportedOperationException(
					"Accelerometer not supported");
		}
	}

	public void pause() {
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(this,
					SensorManager.SENSOR_ACCELEROMETER);
			mSensorMgr = null;
		}
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
	}

	public void onSensorChanged(int sensor, float[] values) {
		if (sensor != SensorManager.SENSOR_ACCELEROMETER)
			return;
		long now = System.currentTimeMillis();

		if ((now - mLastForce) > SHAKE_TIMEOUT) {
			mShakeCount = 0;
		}

		if ((now - mLastTime) > TIME_THRESHOLD) {
			long diff = now - mLastTime;
			float speedZ = Math.abs(values[SensorManager.DATA_Z]  - mLastZ)
					/ diff * 10000;
			float speedX = Math.abs(values[SensorManager.DATA_X]  - mLastX)
					/ diff * 10000;
			float speedY = Math.abs(values[SensorManager.DATA_Y]  - mLastY)
					/ diff * 10000;
			
			

			Log.v("speed",Math.round(speedX)+" "+Math.round(speedY)+" "+Math.round(speedZ));
			
			if (speedY > FORCE_THRESHOLD) {
				if ((++mShakeCount >= SHAKE_COUNT)
						&& (now - mLastShake > SHAKE_DURATION)) {
					mLastShake = now;
					mShakeCount = 0;
					if (mShakeListener != null) {
						mShakeListener.onShake();
					}
				}
				mLastForce = now;
			}
			mLastTime = now;
			mLastX = values[SensorManager.DATA_X];
			mLastY = values[SensorManager.DATA_Y];
			mLastZ = values[SensorManager.DATA_Z];
		}
	}

}