package ca.ece.utoronto.ece1780.runningapp.utility;

import android.media.MediaMetadataRetriever;

public class MediaInformationProvider {
	
	static MediaMetadataRetriever mmr = new MediaMetadataRetriever();
	
	public static String getArtist(String  filePath){
		
		mmr.setDataSource(filePath);
		String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		mmr.setDataSource("");
		return result;
	}
	
	public static String getTitle(String  filePath){
		
		mmr.setDataSource(filePath);
		String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		mmr.setDataSource("");
		return result;
	}
	
	public static String getAlbum(String  filePath){
		
		mmr.setDataSource(filePath);
		String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		mmr.setDataSource("");
		return result;
	}
}
