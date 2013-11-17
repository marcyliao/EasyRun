package ca.ece.utoronto.ece1780.runningapp.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class MusicUtility {

	private List<String> mediaList = new ArrayList<String>();
	
	public MusicUtility() {
		setMediaDirectory();
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
			return true;
		}
	}
	
	public List<String> getAllMusicPath() {
		return mediaList;
	}
}
