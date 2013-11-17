package ca.ece.utoronto.ece1780.runningapp.utility;

import java.io.File;

import android.media.MediaMetadataRetriever;

public class MediaInformationProvider {
	
	MediaMetadataRetriever mmr = new MediaMetadataRetriever();
	
	public String getArtist(String  filePath){
		
		mmr.setDataSource(filePath);
		String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		if(result == null)
			return "Unknow";
		
		return result;
	}
	
	public String getTitle(String  filePath){
		
		mmr.setDataSource(filePath);
		String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		if(result == null) {
			File f = new File(filePath);
			return f.getName();
		}
			
		return result;
	}
	
	public String getAlbum(String  filePath){
		
		mmr.setDataSource(filePath);
		String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		if(result == null)
			return "Unknow";
		
		return result;
	}
}
