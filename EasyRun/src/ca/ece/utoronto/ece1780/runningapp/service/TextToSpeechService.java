package ca.ece.utoronto.ece1780.runningapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import ca.ece.utoronto.ece1780.runningapp.preference.UserSetting;
import ca.ece.utoronto.ece1780.runningapp.view.listener.OnSpeechCompleteListener;
import ca.ece.utoronto.ece1780.runningapp.view.listener.OnSpeechListener;

public class TextToSpeechService extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener{

	public static boolean isServiceRunning = false;
	static private TextToSpeech mTts;
    private String spokenText;
    public static final String MSG_ETRA_CODE = "msg_code";
    public static List<OnSpeechListener> onSpeechListeners = new ArrayList<OnSpeechListener>();
    public static List<OnSpeechCompleteListener> onSpeechCompleteListeners = new ArrayList<OnSpeechCompleteListener>();
    
    public static int addOnSpeechListener(OnSpeechListener listener){
    	TextToSpeechService.onSpeechListeners.add(listener);
    	return onSpeechListeners.size() - 1;
    }
    
    public static int addOnSpeechCompleteListener(OnSpeechCompleteListener listener){
    	TextToSpeechService.onSpeechCompleteListeners.add(listener);
    	return onSpeechCompleteListeners.size() - 1;
    }
    
    public static void speak(String msg, Context context){
    	isServiceRunning = true;
    	boolean userSetting = new UserSetting(context).isSpeechEnabled();
    	
    	if(userSetting) {
    		for(OnSpeechListener listener : TextToSpeechService.onSpeechListeners){
    			listener.onSpeech();
    		}
    		Intent i = new Intent(context,TextToSpeechService.class);
    		i.putExtra(MSG_ETRA_CODE, msg);
    		context.startService(i);
    	}
    }
    
    public static void start(Context context){
    	if(isServiceRunning != true) {
	    	isServiceRunning = true;
	    	boolean userSetting = new UserSetting(context).isSpeechEnabled();
	    	
	    	if(userSetting) {
	    		for(OnSpeechListener listener : TextToSpeechService.onSpeechListeners){
	    			listener.onSpeech();
	    		}
	    		Intent i = new Intent(context,TextToSpeechService.class);
	    		context.startService(i);
	    	}
    	}
    }
    
    static public void stop(Context context){
    	Intent i = new Intent(context,TextToSpeechService.class);
    	context.stopService(i);
    }
    
    @Override
    public void onCreate() {
    }

    @Override
    public void onInit(int status) {

    	Log.d("speak","init");
    	boolean available = false;
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
            	if (spokenText != null) {
	            	HashMap<String, String> params = new HashMap<String, String>();
	            	params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"dumpId");
	
	            	mTts.setSpeechRate(0.9f);
	            	mTts.setOnUtteranceCompletedListener(this);
	                mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, params);
            	}
            	
                available = true;
            }
        }
        
        if(!available)
            stopSelf();
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
    	if(intent == null)
    		return;
    	
        spokenText = intent.getStringExtra(MSG_ETRA_CODE);
        if(mTts == null)
        	mTts = new TextToSpeech(this, this);
        else if (spokenText != null){
        	HashMap<String, String> params = new HashMap<String, String>();
        	params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"dumpId");

        	mTts.setSpeechRate(0.9f);
        	mTts.setOnUtteranceCompletedListener(this);
            mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, params);
        }
    }

    @Override
    public void onUtteranceCompleted(String uttId) {
    	for(OnSpeechCompleteListener listener : TextToSpeechService.onSpeechCompleteListeners){
    		listener.onSpeechComplete();
    	}
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
            mTts = null;
        }

    	isServiceRunning = false;
    	Log.d("speak","stop");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

}
