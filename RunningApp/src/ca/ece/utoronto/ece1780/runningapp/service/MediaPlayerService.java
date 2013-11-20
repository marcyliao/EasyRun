package ca.ece.utoronto.ece1780.runningapp.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import ca.ece.utoronto.ece1780.runningapp.utility.MediaInformationProvider;
import ca.ece.utoronto.ece1780.runningapp.view.HomeActivity;
import ca.ece.utoronto.ece1780.runningapp.view.R;
import ca.ece.utoronto.ece1780.runningapp.view.listener.OnSpeechCompleteListener;
import ca.ece.utoronto.ece1780.runningapp.view.listener.OnSpeechListener;

public class MediaPlayerService extends Service {
	
	public static final String PATH = "path";
	//used to get the system services such as AudioManager.
	Context context;
	//used to communicate with activity
	MediaBinder mediaBinder;
	//MediaPlayer is the critical component of this Class
	private MediaPlayer mediaPlayer;
	//sign for if the mediaPlayer is paused or not
	private boolean isPaused;
	//sign for whether the media player is paused by auto action or by user
	private boolean isAutoPaused;
	//mediaList is used to store all the media files paths 
	private List<String> mediaList;
	//mediaIndex is used to indicate which song is play on the array list now
	private int mediaIndex;
	//tell whether the service is running or not for other services and activities
	public static boolean isServiceRunning = false;
	
	public static int setMusicCurrentTime = 0x0001;
	public static int setDuration = 0x0002;
	
	Handler activityHandler; 
	
	HandlerThread timeCaculator;
	
	String Tag = "MediaService_1";

	@Override
	public void onCreate() {

		super.onCreate();
		this.context = this.getApplicationContext();
		mediaBinder = new MediaBinder();
		mediaPlayer = new MediaPlayer();
		mediaList = new ArrayList<String>();
		mediaBinder = new MediaBinder();
		setMediaDirectory();
		
		TelephonyManager phoneManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		phoneManager.listen(new MediaPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
		
		TextToSpeechService.addOnSpeechListener(new OnSpeechListener() {
			
			@Override
			public void onSpeech() {
				if(MediaPlayerService.this.isPlaying()){
					MediaPlayerService.this.autoPause();
				}
			}
		});
		
		TextToSpeechService.addOnSpeechCompleteListener(new OnSpeechCompleteListener() {
			
			@Override
			public void onSpeechComplete() {
				if(isAutoPaused){
					MediaPlayerService.this.autoPause();
				}
			}
		});
		
		Log.d(Tag, "OnCreate is called.");
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		isServiceRunning = true;
        Notification notification = new Notification(R.drawable.ic_launcher_small, getText(R.string.app_name),System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name),"music is playing", pendingIntent);
        startForeground(234, notification);
        
        if(this.isReady()){
        	String path = intent.getStringExtra(PATH);
        	if(path != null)
        		this.play(path);
        	else
        		this.play();
        }

        Log.d(Tag, "OnStartCommand is called.");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		
		isServiceRunning = false;
		Log.d(Tag, "OnDestroy is called.");
		
		//fix a bug
		if(timeCaculator != null){
			timeCaculator.killThread();
		}
		
		super.onDestroy();
	}


	@Override
	public IBinder onBind(Intent intent) {
		
		return mediaBinder;
	}
	
	public void resetMediaPlayer(){
		
		mediaPlayer.reset();
		mediaList.clear();
	}
	
	public List<String> getMediaList(){
		
		return mediaList;
	}
	
	public String getArtist(int index){
		
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(mediaList.get(index));
		String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		
		return artist;	
	}
	
	public String getArtist(){
		
		return getArtist(mediaIndex);		
	}
	
	public String getSongName(int index){
		
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(mediaList.get(index));
		String songName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		
		return songName;
	}
	
	public String getSongName(){
		
		return getSongName(mediaIndex);
	}

	public boolean addMediaDirectory(String directoryPath){
		
		File mediaDirectory = new File(directoryPath);
		
		//if meidaDirectory is not a directory 
		if(!mediaDirectory.isDirectory())
			return false;

		//add the absolutePath of all the files end with mp3 and wma under the mediaDirectory into the mediaList		
		for(File mediaFile : mediaDirectory.listFiles()){
			
			if(mediaFile.isDirectory()){
				
				setMediaDirectory(mediaFile);
				
			}else if(mediaFile.toString().endsWith(".mp3") || mediaFile.toString().endsWith("wma")){
					
				mediaList.add(mediaFile.getAbsolutePath());
			}
		}
		//check whether mediaList is empty or not
		if (mediaList.size() == 0)
			return false;
		else{
			mediaIndex = 0;
			return true;
		}
		
	}
	
	//the default directory of music is /music of the external storage directory.
	public boolean setMediaDirectory(){
		
		File mediaDirectory = new File(Environment.getExternalStorageDirectory(),"/Music");

		return setMediaDirectory(mediaDirectory);
	}
	
	public boolean setMediaDirectory(File mediaDirectory){

			mediaList.clear();
			return addMediaDirectory(mediaDirectory.getAbsolutePath());
	}
	
	//return how many songs are in the media list	
	public int getMediaAmount(){
		
		return mediaList.size();
	}
	
	// begin to play music immediately
	public void play(){
		
		play(mediaIndex);
	}
	
	public void play(String mediaPath){
		
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(mediaPath);
			if(!mediaList.get(mediaIndex).equals(mediaPath)){
				
				for(int i = 0; i < mediaList.size(); i++){
					
					if(mediaList.get(i).equals(mediaPath)){
						mediaIndex = i;
					}
				}
				
			}
			mediaPlayer.prepare();
		} catch (Exception e) {
			
			Log.d("Multi_Media", "Exception in MediaPlayer.Play");
			e.printStackTrace();
		}
		mediaPlayer.setOnPreparedListener(new PreparedListener());
		mediaPlayer.setOnCompletionListener(new CompletionListener());
		isPaused = false;
		
	}
	
	//begin to play the song which mdianIndex is pointed
	public void play(int mediaIndex){
		
		String mediaPath = mediaList.get(mediaIndex);
		this.play(mediaPath);
	}
	
	public void playFrom(int time){
		
		if(this.isPlaying()){
			mediaPlayer.seekTo(time);
		}
			
	}
	
	//replay the song from the very beginning of the song
	public void replay(){
		
		if(mediaPlayer.isPlaying()){
			
			mediaPlayer.seekTo(0);
		}
	}
	
	//play the next song, the first one if it was the last song in the list
	public void playNext(){
		
		if(mediaIndex == mediaList.size()-1)
			mediaIndex = 0;
		else
			mediaIndex = mediaIndex + 1;
			
		play(mediaIndex);
	}
	
	//play the previous song, the last one if it was the first song in the list
	public void playPrevious(){
		
		if(mediaIndex == 0)
			mediaIndex = mediaList.size()-1;
		else
			mediaIndex = mediaIndex - 1;
		
		play(mediaIndex);
	}
	
	//make the mediaPlayer pause
	public void pause(){
		
		if (mediaPlayer.isPlaying() && isPaused == false){
			
			mediaPlayer.pause();
			isPaused = true;
		}
		else if (isPaused == true){
			
			mediaPlayer.start();
			isPaused = false;
			
		}
	}

	// make the mediaPlayer pause by system action
	public void autoPause() {

		if (mediaPlayer.isPlaying() && isAutoPaused == false) {

			mediaPlayer.pause();
			isAutoPaused = true;
		} else if (isAutoPaused == true) {

			mediaPlayer.start();
			isAutoPaused = false;

		}
	}
	
	//stop the media player
	public void stop(){
		
		mediaPlayer.stop();
	}
	
	public int getDuration(){
		
		return mediaPlayer.getDuration();
	}
	
	public int getCurPosition(){
		
		return mediaPlayer.getCurrentPosition();
	}
	
	public void setVolumn(int aim){
		
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, aim, 0);
	}
	
	public void volumeUp(int amount){
		
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		for(int i = 0; i < amount; i++){
			
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
		}
		Toast.makeText(context, "Volume is : " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), Toast.LENGTH_SHORT).show();
	}
	
	public void volumeDown(int amount){
		
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		for(int i = 0; i < amount; i++){
			
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
		}
		Toast.makeText(context, "Volume is : " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), Toast.LENGTH_SHORT).show();
	}
	
	public int getIndex(){
		
		return mediaIndex;
	}
	
	public boolean isPaused(){
		
		return isPaused;
	}
	
	public boolean isPlaying(){
		
		return mediaPlayer.isPlaying();
	}
	
	public boolean isReady(){
		
		if (mediaPlayer != null && mediaList.size() != 0)
			return true;
		
		return false;
	}
	
	
	public class MediaBinder extends Binder{

		public MediaPlayerService getMediaPlayerService(){
			
			return MediaPlayerService.this;
		}
		
		public void setActivityHandler(Handler handler){
			
			activityHandler = handler;
			
			System.out.println("activity handler is not null any more");
		}

	}//end of MediaBinder
	
	public class PreparedListener implements OnPreparedListener {

		@Override
		public void onPrepared(MediaPlayer mp) {
			
			mp.start();
			
			if(timeCaculator != null){
				timeCaculator.killThread();
			}
			
			timeCaculator = new HandlerThread();			
			timeCaculator.start();
		}
		

	}//end of PreparedListener
	
	private class HandlerThread extends Thread{

		boolean live = true;
		
		@Override
		public void run() {

			int position = -1;
			int duration = mediaPlayer.getDuration();

			while (position < duration && live) {

				if (activityHandler != null) {
					Message mDuration = new Message();
					mDuration.what = MediaPlayerService.setDuration;
					MediaInformationProvider mip = new MediaInformationProvider();
					String songName = mip.getTitle(mediaList.get(mediaIndex));

					Bundle durationData = new Bundle();
					durationData.putInt("duration", duration);
					durationData.putString("songName", songName);
					mDuration.setData(durationData);

					activityHandler.sendMessage(mDuration);
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				position = mediaPlayer.getCurrentPosition();

				if (activityHandler != null) {
					Message msg = new Message();
					msg.what = MediaPlayerService.setMusicCurrentTime;

					Bundle data = new Bundle();
					data.putInt("progress", position);
					msg.setData(data);

					activityHandler.sendMessage(msg);
				}
			}

		}
		
		public void killThread(){
			
			live = false;
		}
		
	}
	
	public class CompletionListener implements OnCompletionListener{

		@Override
		public void onCompletion(MediaPlayer mp) {
			
			playNext();
		}
		
	}//end of CompletionListener
	
	private class MediaPhoneStateListener extends PhoneStateListener{
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			
			switch(state){
			case TelephonyManager.CALL_STATE_OFFHOOK:
			case TelephonyManager.CALL_STATE_RINGING:{
				if(MediaPlayerService.this.isPlaying()){
					MediaPlayerService.this.autoPause();
				}
				break;
			}
			
			case TelephonyManager.CALL_STATE_IDLE:{
				if(isAutoPaused){
					MediaPlayerService.this.autoPause();
				}
				break;
			}
				
			}//end of switch.
		}
		
	}
}
