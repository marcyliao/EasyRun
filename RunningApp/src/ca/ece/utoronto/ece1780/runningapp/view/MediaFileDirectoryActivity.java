package ca.ece.utoronto.ece1780.runningapp.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ca.ece.utoronto.ece1780.runningapp.view.fragment.MusicFragment;

public class MediaFileDirectoryActivity extends Activity {
	
	List<String> mediaDirectories = new ArrayList<String>();
	public static final int resultCode = 7741;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_file_directory_activity);
		File rootDirectory = new File(Environment.getExternalStorageDirectory(),"");//Environment.getRootDirectory()
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

		for(File file : rootDirectory.listFiles()){
			
			if(file.isDirectory() && !file.getName().contains(".")){
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("directoryName", file.getName());
				listItems.add(listItem);
				
				mediaDirectories.add(file.getAbsolutePath());
			}
		}
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.list_view_item_folder, new String[]{"directoryName"}, new int[]{R.id.filePath});
		ListView listView = (ListView) findViewById(R.id.directoryList);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(MediaFileDirectoryActivity.this, HomeActivity.class);
				intent.putExtra("mediaDirectoryPath", mediaDirectories.get(position));
				setResult(MediaFileDirectoryActivity.resultCode, intent);
				finish();
			}
		});
		
		listView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
			
		});
		listView.setAdapter(simpleAdapter);

	}


	
	

}
