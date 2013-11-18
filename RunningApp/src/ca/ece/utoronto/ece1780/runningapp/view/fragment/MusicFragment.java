package ca.ece.utoronto.ece1780.runningapp.view.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import ca.ece.utoronto.ece1780.runningapp.service.MediaPlayerService;
import ca.ece.utoronto.ece1780.runningapp.utility.MediaInformationProvider;
import ca.ece.utoronto.ece1780.runningapp.utility.MusicUtility;
import ca.ece.utoronto.ece1780.runningapp.view.MediaFileDirectoryActivity;
import ca.ece.utoronto.ece1780.runningapp.view.R;

public class MusicFragment extends Fragment {
	
	private SongArrayAdapter mListAdapter;
	private MediaPlayerService mediaPlayer;
	private TextView textViewNoSong;

	private List<String> songPaths = new ArrayList<String>();
	
	private ServiceConnection mediaConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mediaPlayer = null;	
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			MediaPlayerService.MediaBinder mediaBinder = (MediaPlayerService.MediaBinder) service; 
			mediaPlayer = mediaBinder.getMediaPlayerService();
			updateMusicUI();
		}
	};
	private Button pauseButton;

	public MusicFragment() {
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_music, container, false);
		
		pauseButton = (Button) rootView.findViewById(R.id.musicPause);
		pauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mediaPlayer != null && mediaPlayer.isReady())
				{
					mediaPlayer.pause();
					updateMusicUI();
				}
				else {
					startMusicService();
				}
					
			}
		});

		/*
		Button musicButton = (Button) rootView.findViewById(R.id.MusicButton);
		if(MediaPlayerService.isServiceRunning == true){
			musicButton.setText("stop");
		}

		musicButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!MediaPlayerService.isServiceRunning){
					startMusicService();
				}
				else{
					stopMusicService();
				}
				
			}


		});
		*/
		Button nextButton = (Button) rootView.findViewById(R.id.buttonNextSong);
		Button previousButton = (Button) rootView.findViewById(R.id.buttonPrevSong);
		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!MediaPlayerService.isServiceRunning){
					startMusicService();
				}
				else{
					if(mediaPlayer != null && mediaPlayer.isReady()) {
						mediaPlayer.playNext();
						updateMusicUI();
					}
					else {
						startMusicService();
					}
				}
			}
		});
		
		previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!MediaPlayerService.isServiceRunning){
					startMusicService();
				}
				else{
					if(mediaPlayer != null && mediaPlayer.isReady()){
						mediaPlayer.playPrevious();
						updateMusicUI();
					}
					else {
						startMusicService();
					}
				}
			}
		});
		
	    prepareWidgets(rootView);
    
		return rootView;
	}

	private void startMusicService() {
		Intent intent = new Intent(getActivity(), MediaPlayerService.class);
		getActivity().startService(intent);
		getActivity().bindService(intent, mediaConnection , 0);
	}
	
	private void updateMusicUI() {
		if(mediaPlayer==null || !mediaPlayer.isReady() || mediaPlayer.isPaused())
			pauseButton.setBackgroundResource(R.drawable.icon_music_play);
		else
			pauseButton.setBackgroundResource(R.drawable.icon_music_pause);
	}


	private void startMusicService(String path) {
		if(songPaths==null && songPaths.isEmpty()) {
			Toast.makeText(getActivity(), "no song found", Toast.LENGTH_SHORT).show();
		}
		
		Intent intent = new Intent(getActivity(), MediaPlayerService.class);
		intent.putExtra(MediaPlayerService.PATH, path);
		getActivity().startService(intent);
		getActivity().bindService(intent, mediaConnection , 0);
	}
	
	private void stopMusicService() {
		Intent intent = new Intent(getActivity(), MediaPlayerService.class);
		mediaPlayer.stop();
		getActivity().unbindService(mediaConnection);
		getActivity().stopService(intent);
		mediaPlayer = null;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(MediaPlayerService.isServiceRunning == true){
			if(mediaPlayer.isPaused() == true) {
				stopMusicService();
			}
			else
				getActivity().unbindService(mediaConnection);
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		if(MediaPlayerService.isServiceRunning == true){
			Intent intent = new Intent(getActivity(), MediaPlayerService.class);
			getActivity().bindService(intent, mediaConnection, 0);
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		if(resultCode == MediaFileDirectoryActivity.resultCode){
			
			String directoryPath = data.getExtras().getString("mediaDirectoryPath");
			mediaPlayer.addMediaDirectory(directoryPath);
		}
		
	}
	
	private void prepareWidgets(View rootView) {
		ListView l = (ListView) rootView.findViewById(R.id.listViewSongs);
		textViewNoSong = (TextView) rootView.findViewById(R.id.textViewNoSong);
//		Button addSongs = (Button) rootView.findViewById(R.id.ButtonAddSongs);
//		addSongs.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), MediaFileDirectoryActivity.class);
//				startActivityForResult(intent, 0);
//			}
//		});
	    mListAdapter = new SongArrayAdapter(getActivity());
	    l.setAdapter(mListAdapter);
	    l.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String path = mListAdapter.getItem(position);

				if(mediaPlayer != null && mediaPlayer.isReady())
					mediaPlayer.play(path);
				else {
					startMusicService(path);
				}
			}
	    });
	    
	    updateList();
	}


	public void updateList() {
		
		new AsyncTask<Object,Object,List<String>>(){

			@Override
			protected void onPostExecute(List<String> result) {
				if(mListAdapter != null) {
					songPaths = result;
					mListAdapter.notifyDataSetChanged();
				}
				super.onPostExecute(result);
			}

			@Override
			protected List<String> doInBackground(Object... params) {
				List<String>  records = new MusicUtility().getAllMusicPath();
				return records;
			}
			
		}.execute();
	}
	
	private class SongArrayAdapter extends BaseAdapter {
		private final Context context;

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();

			if(songPaths == null || songPaths.size() == 0)
				textViewNoSong.setVisibility(View.VISIBLE);
			else
				textViewNoSong.setVisibility(View.INVISIBLE);
		}

		public SongArrayAdapter(Context context) {
			super();
			this.context = context;
			if(songPaths == null || songPaths.size() == 0)
				textViewNoSong.setVisibility(View.VISIBLE);
			else
				textViewNoSong.setVisibility(View.INVISIBLE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_view_item_song, parent, false);
			
			TextView textViewSongName = (TextView) rowView.findViewById(R.id.TextViewSongName);
			TextView textViewSongDesc = (TextView) rowView.findViewById(R.id.TextViewSongDesc);
			
			MediaInformationProvider provider = new MediaInformationProvider();
			
			textViewSongName.setText(provider.getTitle(songPaths.get(position)));
			textViewSongDesc.setText(provider.getArtist(songPaths.get(position)));
			
			return rowView;
		}
		
		@Override
		public int getCount(){
			if(getSongPaths() != null)
				return getSongPaths().size();
			return 0;
		}

		@Override
		public String getItem(int position) {
			return songPaths.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public List<String> getSongPaths() {
			return songPaths;
		}
	}
}


