package ca.ece.utoronto.ece1780.runningapp.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import ca.ece.utoronto.ece1780.runningapp.view.listener.MediaInformaitonReceiver;
import ca.ece.utoronto.ece1780.runningapp.view.listener.OnSpeechCompleteListener;
import ca.ece.utoronto.ece1780.runningapp.view.listener.OnSpeechListener;

public class MediaPlayerService extends Service {
	
	//used to get the system services such as AudioManager.
	Context context;
	//used to communicate with activity
	MediaBinder mediaBinder;
	//MediaPlayer is the critical component of this Class
	MediaPlayer mediaPlayer;
	//sign for if the mediaPlayer is paused or not
	boolean isPaused;
	//mediaList is used to store all the media files paths 
	List<String> mediaList;
	//mediaIndex is used to indicate which song is play on the array list now
	int mediaIndex;
	//
	private static List<MediaInformaitonReceiver> receivers = new ArrayList<MediaInformaitonReceiver>();

	@Override
	public void onCreate() {

		super.onCreate();
		this.context = this.getApplicationContext();
		mediaBinder = new MediaBinder();
		mediaPlayer = new MediaPlayer();
		mediaList = new ArrayList<String>();
		mediaBinder = new MediaBinder();
		setMediaDirectory();
		Log.d("MPS", "Service is created...");
		
		TelephonyManager phoneManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		phoneManager.listen(new MediaPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
		
		TextToSpeechService.addOnSpeechListener(new OnSpeechListener() {
			
			@Override
			public void onSpeech() {
				MediaPlayerService.this.pause();
			}
		});
		
		TextToSpeechService.addOnSpeechCompleteListener(new OnSpeechCompleteListener() {
			
			@Override
			public void onSpeechComplete() {
				
				Log.d("WAkaka", "This is from onSpeechCompelete");
				MediaPlayerService.this.pause();
			}
		});
		
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d("MPS", "Service is started...");
		return super.onStartCommand(intent, flags, startId);
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		
		return mediaBinder;
	}
	
	public static void regiestReceiver(MediaInformaitonReceiver receiver){
		
		receivers.add(receiver);
	}
	
	public void resetMediaPlayer(){
		
		mediaPlayer.reset();
		mediaList.clear();
	}
	
	
	public String getArtist(){
		
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(mediaList.get(mediaIndex));
		String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
	
		return artist;		
	}
	
	public String getSongName(){
		
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(mediaList.get(mediaIndex));
		String songName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		
		return songName;
	}
	
	//the default directory of music is /music of the external storage directory.
	public boolean setMediaDirectory(){
		
		File mediaDirectory = new File(Environment.getExternalStorageDirectory(),"/Music");
		return setMediaDirectory(mediaDirectory);
	}
	
	public boolean setMediaDirectory(File mediaDirectory){
		//if meidaDirectory is not a directory 
		if(!mediaDirectory.isDirectory())
			return false;
		//clear the mediaList
		mediaList.clear();
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
	
	//return how many songs are in the media list	
	public int getMediaAmount(){
		
		return mediaList.size();
	}
	
	// begin to play music immediately
	public void play(){
		
		play(mediaIndex);
	}
	
	//begin to play the song which mdianIndex is pointed
	public void play(int mediaIndex){
		
		System.out.println(this.getArtist() + " : " + this.getSongName());
		String mediaPath = mediaList.get(mediaIndex);
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(mediaPath);
			mediaPlayer.prepare();
		} catch (Exception e) {
			
			Log.d("Multi_Media", "Exception in MediaPlayer.Play");
			e.printStackTrace();
		}
		mediaPlayer.setOnPreparedListener(new PreparedListener());
		mediaPlayer.setOnCompletionListener(new CompletionListener());
		isPaused = false;
		for(MediaInformaitonReceiver receiver : receivers){
			
			receiver.setMediaName(this.onWhichSong());
			receiver.setMediaDuration(mediaPlayer.getDuration());
		}
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
		Toast.makeText(context, "Volume was : " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), Toast.LENGTH_SHORT).show();
		for(int i = 0; i < amount; i++){
			
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
		}
		Toast.makeText(context, "Volume is : " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), Toast.LENGTH_SHORT).show();
	}
	
	public void volumeDown(int amount){
		
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		Toast.makeText(context, "Volume was : " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), Toast.LENGTH_SHORT).show();
		for(int i = 0; i < amount; i++){
			
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
		}
		Toast.makeText(context, "Volume is : " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), Toast.LENGTH_SHORT).show();
	}
	
	public int getIndex(){
		
		return mediaIndex;
	}
	
	public String onWhichSong(){
		
		//delete the file dictionary information from the mediaName
		String mediaName = mediaList.get(mediaIndex);
		int index = mediaName.indexOf('/', 0);
		while( index != -1){
			
			if(index < mediaName.length()){
				mediaName = mediaName.substring(index+1, mediaName.length());
				index = mediaName.indexOf('/', 0);
			}
		}
		//delete the .mp3 and .wma from the mediaName
		index = mediaName.indexOf('.', 0);
		if(index != -1 && index < mediaName.length()){
			mediaName = mediaName.substring(0, index);
		}
		
		return mediaName;
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

	}//end of MediaBinder
	
	public class PreparedListener implements OnPreparedListener {

		@Override
		public void onPrepared(MediaPlayer mp) {
			
			mp.start();
			
			Thread timeCaculator = new Thread(){
				
				@Override
				public void run() {
					
					int position = -1;
					int duration = mediaPlayer.getDuration();
					while( position < duration){
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						position = mediaPlayer.getCurrentPosition();
						for(MediaInformaitonReceiver receiver : receivers){
							receiver.setMediaPlayPosition(position);
						}
						
					}
							
				}
				
				
			};	
			
			timeCaculator.start();
		}
		

	}//end of PreparedListener
	
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
				
				MediaPlayerService.this.pause();
				break;
			}
			
			case TelephonyManager.CALL_STATE_IDLE:{
				
				MediaPlayerService.this.pause();
				break;
			}
				
			}//end of switch.
		}
		
	}
}
