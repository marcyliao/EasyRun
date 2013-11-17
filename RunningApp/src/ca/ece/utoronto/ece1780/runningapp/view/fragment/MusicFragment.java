package ca.ece.utoronto.ece1780.runningapp.view.fragment;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.service.MediaPlayerService;
import ca.ece.utoronto.ece1780.runningapp.utility.MediaInformationProvider;
import ca.ece.utoronto.ece1780.runningapp.utility.MusicUtility;
import ca.ece.utoronto.ece1780.runningapp.view.ActivityRecordActivity;
import ca.ece.utoronto.ece1780.runningapp.view.MediaFileDirectoryActivity;
import ca.ece.utoronto.ece1780.runningapp.view.R;

public class MusicFragment extends Fragment {
	
	private SongArrayAdapter mListAdapter;
	private MediaPlayerService mediaPlayer;
	private TextView textViewNoSong;
	
	Button musicButton;
	
	
	private ServiceConnection mediaConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mediaPlayer = null;	
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			MediaPlayerService.MediaBinder mediaBinder = (MediaPlayerService.MediaBinder) service; 
			mediaPlayer = mediaBinder.getMediaPlayerService();
		}
	};

	public MusicFragment() {
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_music, container, false);
		
		
		
		musicButton = (Button) rootView.findViewById(R.id.MusicButton);
		Button pauseButton = (Button) rootView.findViewById(R.id.musicPause);
		pauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mediaPlayer != null && mediaPlayer.isReady())
					mediaPlayer.pause();
				else {
					startMusicService();
				}
					
			}
		});
		
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

			private void stopMusicService() {
				Intent intent = new Intent(getActivity(), MediaPlayerService.class);
				mediaPlayer.stop();
				getActivity().unbindService(mediaConnection);
				getActivity().stopService(intent);
				MusicFragment.this.musicButton.setText("start");
				mediaPlayer = null;
			}

			
		});
		
	    prepareWidgets(rootView);
    
		return rootView;
	}

	private void startMusicService() {
		Intent intent = new Intent(getActivity(), MediaPlayerService.class);
		getActivity().startService(intent);
		getActivity().bindService(intent, mediaConnection , 0);
		MusicFragment.this.musicButton.setText("stop");
	}
	
	private void startMusicService(String path) {
		Intent intent = new Intent(getActivity(), MediaPlayerService.class);
		intent.putExtra(MediaPlayerService.PATH, path);
		getActivity().startService(intent);
		getActivity().bindService(intent, mediaConnection , 0);
		MusicFragment.this.musicButton.setText("stop");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(MediaPlayerService.isServiceRunning == true){
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
	}


	public void updateList() {
		
		if(mListAdapter != null) {
			mListAdapter.notifyDataSetChanged();
		}
	}
	
	private class SongArrayAdapter extends BaseAdapter {
		private final Context context;
		private List<String> songPaths;

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
			
			songPaths = new MusicUtility().getAllMusicPath();
			
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


