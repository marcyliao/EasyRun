package ca.ece.utoronto.ece1780.runningapp.utility;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import ca.ece.utoronto.ece1780.runningapp.data.Song;


public class MusicUtility {
	
	private Context context;
	
	public MusicUtility(Context context) {
		this.context = context;
	}
	
	public List<Song> getAllSongs(){
		try {
			String[] proj = { MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media.DISPLAY_NAME,
					MediaStore.Audio.Media.ARTIST, MediaStore.Video.Media.SIZE };

			Cursor musiccursor = context.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj,
					MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);

			ArrayList<Song> songs = new ArrayList<Song>();

			while (musiccursor.moveToNext()) {
				Song song = new Song();
				song.setPath(musiccursor.getString(1));
				song.setTitle(musiccursor.getString(2));
				song.setArtist(musiccursor.getString(3));

				songs.add(song);
			}
			musiccursor.close();
			return songs;
		}
		catch(Exception e) {
			return new ArrayList<Song>();
		}
		
	}
}
