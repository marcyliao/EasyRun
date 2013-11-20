package ca.ece.utoronto.ece1780.runningapp.utility;

import java.io.File;

import android.media.MediaMetadataRetriever;

public class MediaInformationProvider {
	
	MediaMetadataRetriever mmr = new MediaMetadataRetriever();
	
	public String getArtist(String  filePath){
		
		mmr.setDataSource(filePath);
		String result = extractArtist();
		
		return result;
	}

	private String extractArtist() {
		String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		if(result == null)
			result = "Unknow";
		return result;
	}
	
	public String getTitle(String  filePath){
		
		mmr.setDataSource(filePath);
		String result = extractTitle(filePath);
			
		return result;
	}

	private String extractTitle(String filePath) {
		String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		if(result == null) {
			File f = new File(filePath);
			result = f.getName();
		}
		return result;
	}
	
	public String getAlbum(String  filePath){
		
		mmr.setDataSource(filePath);
		String result = extractAlbum();
		
		return result;
	}

	private String extractAlbum() {
		String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		if(result == null)
			result = "Unknow";
		return result;
	}
	
	public MediaInfo getMediaInfo(String  filePath) {
		MediaInfo info = new MediaInfo();
		mmr.setDataSource(filePath);
		info.setAlbum(extractAlbum());
		info.setArtist(extractArtist());
		info.setTitle(extractTitle(filePath));
		
		return info;
	}
	
	public class MediaInfo {
		private String album;
		private String artist;
		private String title;
		
		public String getAlbum() {
			return album;
		}
		public void setAlbum(String album) {
			this.album = album;
		}
		public String getArtist() {
			return artist;
		}
		public void setArtist(String artist) {
			this.artist = artist;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
		
	}
}
