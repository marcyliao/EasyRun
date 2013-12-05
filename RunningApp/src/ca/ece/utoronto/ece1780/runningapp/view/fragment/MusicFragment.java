package ca.ece.utoronto.ece1780.runningapp.view.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import ca.ece.utoronto.ece1780.runningapp.data.Song;
import ca.ece.utoronto.ece1780.runningapp.service.MediaPlayerService;
import ca.ece.utoronto.ece1780.runningapp.utility.MusicUtility;
import ca.ece.utoronto.ece1780.runningapp.view.R;

public class MusicFragment extends Fragment {
	
	private SongArrayAdapter mListAdapter;
	private MediaPlayerService mediaPlayer;
	private TextView textViewNoSong;

	private TextView textViewSongName;
	private TextView musicCurrentTime;
	private TextView musicDuration;
	private SeekBar processBar;   
	private Handler musicPlayerHandler;
	

	
	private List<Song> songs = new ArrayList<Song>();
	
	private ServiceConnection mediaConnection;
	
	private Button pauseButton;

	public MusicFragment() {
		
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);

		musicPlayerHandler  = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				if(msg.what == MediaPlayerService.setMusicCurrentTime){
					
					Bundle data = msg.getData();
					int progress = data.getInt("progress");
					int minutes = progress/60000;
					int seconds = (progress%60000)/1000;
					musicCurrentTime.setText((minutes>9?"":"0")+ minutes + ":" + (seconds>9?"":"0")+ seconds);
					processBar.setProgress(progress);
				}
				else if(msg.what == MediaPlayerService.setDuration){
					
					Bundle data = msg.getData();
					int duration = data.getInt("duration");
					int minutes = duration/60000;
					int seconds = (duration%60000)/1000;
					musicDuration.setText((minutes>9?"":"0")+ minutes + ":" + (seconds>9?"":"0")+ seconds);
					processBar.setMax(duration);
					String songName = data.getString("songName");
					textViewSongName.setText(songName);
				}
			}
		};
		
		mediaConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mediaPlayer = null;	
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				MediaPlayerService.MediaBinder mediaBinder = (MediaPlayerService.MediaBinder) service; 
				mediaPlayer = mediaBinder.getMediaPlayerService();
				mediaBinder.setActivityHandler(musicPlayerHandler);
				updateMusicUI();
			}
		};
		
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_music, container, false);
		textViewSongName = (TextView)rootView.findViewById(R.id.textViewSongName);
		musicCurrentTime = (TextView)rootView.findViewById(R.id.textViewMusicCurrentTime);
		musicDuration    = (TextView)rootView.findViewById(R.id.textViewMusicDuration);
		processBar       = (SeekBar)rootView.findViewById(R.id.musicProcess);
		
		processBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				if(fromUser == false)
					return;
				if(mediaPlayer == null || !mediaPlayer.isReady())
					return;
				
				mediaPlayer.playFrom(progress);
			}
		});

	    prepareWidgets(rootView);  
		return rootView;
	}

	private void startMusicService() {
		
		if(songs==null || songs.isEmpty()) {
			Toast.makeText(getActivity(), "no song found", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent(getActivity(), MediaPlayerService.class);
		getActivity().startService(intent);
		getActivity().bindService(intent, mediaConnection , 0);
	}
	
	
	// Update the user panel according to the media service states
	private void updateMusicUI() {
		if(mediaPlayer==null || !mediaPlayer.isReady() || mediaPlayer.isPaused())
			pauseButton.setBackgroundResource(R.drawable.icon_music_play);
		else
			pauseButton.setBackgroundResource(R.drawable.icon_music_pause);
	}

	private void startMusicService(String path) {
		if(songs==null || songs.isEmpty()) {
			Toast.makeText(getActivity(), "no song found", Toast.LENGTH_SHORT).show();
			return;
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
	
	private void prepareWidgets(View rootView) {
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
		
		ListView l = (ListView) rootView.findViewById(R.id.listViewSongs);
		textViewNoSong = (TextView) rootView.findViewById(R.id.textViewMusicListMessage);
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
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String path = mListAdapter.getItem(position).getPath();

				if(mediaPlayer != null && mediaPlayer.isReady()) {
					mediaPlayer.play(path);
					updateMusicUI();
				}
				else {
					startMusicService(path);
				}
			}
	    });
	    
	    updateList();
	}


	public void updateList() {
		textViewNoSong.setText("Loading...");
		new AsyncTask<Object,Object,List<Song>>(){

			@Override
			protected void onPostExecute(List<Song> result) {
				if(mListAdapter != null) {
					songs = result;
					mListAdapter.notifyDataSetChanged();
				}
				super.onPostExecute(result);
			}

			@Override
			protected List<Song> doInBackground(Object... params) {
				List<Song>  records = new ArrayList<Song>();
				records = new MusicUtility(getActivity()).getAllSongs();
				return records;
			}
			
		}.execute();
	}
	
	private class SongArrayAdapter extends BaseAdapter {
		private final Context context;

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();

			if(songs == null || songs.size() == 0) {
				textViewNoSong.setVisibility(View.VISIBLE);
				textViewNoSong.setText("No Song found..");
			}
			else
				textViewNoSong.setVisibility(View.INVISIBLE);
		}

		public SongArrayAdapter(Context context) {
			super();
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_view_item_song, parent, false);
			
			TextView textViewSongName = (TextView) rowView.findViewById(R.id.TextViewSongName);
			TextView textViewSongDesc = (TextView) rowView.findViewById(R.id.TextViewSongDesc);
			
			textViewSongName.setText(songs.get(position).getTitle());
			textViewSongDesc.setText(songs.get(position).getArtist());
			
			return rowView;
		}
		
		@Override
		public int getCount(){
			if(getSongPaths() != null)
				return getSongPaths().size();
			return 0;
		}

		@Override
		public Song getItem(int position) {
			return songs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public List<Song> getSongPaths() {
			return songs;
		}
	}
}


