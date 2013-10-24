package ca.ece.utoronto.ece1780.runningapp.service;

import java.util.HashMap;
import java.util.Locale;

import ca.ece.utoronto.ece1780.runningapp.preference.UserSetting;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class TextToSpeechService extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener{

	private static boolean beingUsed = false;
	private TextToSpeech mTts;
    private String spokenText;
    public static final String MSG_ETRA_CODE = "msg_code";

    static void speak(String msg, Context context){
    	boolean userSetting = new UserSetting(context).isSpeechEnabled();
    	
    	if(!beingUsed && userSetting) {
    		Intent i = new Intent(context,TextToSpeechService.class);
    		i.putExtra(MSG_ETRA_CODE, msg);
    		context.startService(i);
    		beingUsed = true;  
    	}
    }
    
    @Override
    public void onCreate() {
    }

    @Override
    public void onInit(int status) {
    	boolean available = false;
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.ENGLISH);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
            	
            	HashMap<String, String> params = new HashMap<String, String>();
            	params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"dumpId");

            	mTts.setOnUtteranceCompletedListener(this);
                mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, params);
                available = true;
                
            }
        }
        
        if(!available)
            stopSelf();
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        spokenText = intent.getStringExtra(MSG_ETRA_CODE);
        mTts = new TextToSpeech(this, this);
    }

    @Override
    public void onUtteranceCompleted(String uttId) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        beingUsed = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
