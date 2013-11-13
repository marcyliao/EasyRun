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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import ca.ece.utoronto.ece1780.runningapp.service.MediaPlayerService;
import ca.ece.utoronto.ece1780.runningapp.view.MediaFileDirectoryActivity;
import ca.ece.utoronto.ece1780.runningapp.view.R;

public class MusicFragment extends Fragment {
	
	private SongArrayAdapter mListAdapter;
	private MediaPlayerService mediaPlayer;
	private TextView textViewNoSong;

	public MusicFragment() {
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_music, container, false);
		
		Intent intent = new Intent(getActivity(), MediaPlayerService.class);
		this.getActivity().startService(intent);
		this.getActivity().bindService(intent, new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mediaPlayer = null;	
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
	
				MediaPlayerService.MediaBinder mediaBinder = (MediaPlayerService.MediaBinder) service; 
				mediaPlayer = mediaBinder.getMediaPlayerService();
			}
		}, 0);
		
	    prepareWidgets(rootView);
    
		return rootView;
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
		Button addSongs = (Button) rootView.findViewById(R.id.ButtonAddSongs);
		addSongs.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MediaFileDirectoryActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	    mListAdapter = new SongArrayAdapter(getActivity());
	    l.setAdapter(mListAdapter);
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

			//TODO: update medialist
			
			if(songPaths == null || songPaths.size() == 0)
				textViewNoSong.setVisibility(View.VISIBLE);
			else
				textViewNoSong.setVisibility(View.INVISIBLE);
		}

		public SongArrayAdapter(Context context) {
			super();
			this.context = context;
			
			//TODO: update medialist
			
			if(songPaths == null || songPaths.size() == 0)
				textViewNoSong.setVisibility(View.VISIBLE);
			else
				textViewNoSong.setVisibility(View.INVISIBLE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_view_item_activity, parent, false);
			
			TextView textViewSongName = (TextView) rowView.findViewById(R.id.TextViewSongName);
			TextView textViewSongDesc = (TextView) rowView.findViewById(R.id.TextViewSongDesc);
			
			//TODO: setup each list item according to the mediaList.get(position)

			return rowView;
		}
		
		@Override
		public int getCount(){
			if(getSongPaths() != null)
				return getSongPaths().size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
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


