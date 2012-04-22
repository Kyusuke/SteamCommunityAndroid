package org.ukfsn.hexis.SteamCommunityApp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsListAdapter extends BaseAdapter {
	
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private LayoutInflater inflater = null;
	
	private ImageView friendAvatar;
	private HashMap<String, String> friend;
	
	public FriendsListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	private final ListviewImageDownloader listviewImageDownloader = new ListviewImageDownloader();
	
	public int getCount(){
		return data.size();
	}
	
	public Object getItem(int position){
		return position;
	}
	
	public long getItemId(int position){
		return position;
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		if (rowView == null){
			rowView = inflater.inflate(R.layout.friendslistview, null);
		}
 		
		TextView friendName = (TextView) rowView.findViewById(R.id.friendName);
		TextView friendStatus = (TextView) rowView.findViewById(R.id.friendStatus);
		friendAvatar = (ImageView) rowView.findViewById(R.id.friendAvatar);
		
		friend = new HashMap<String, String>();
		friend = data.get(pos);
		
		friendName.setText(friend.get(Friends.KEY_PERSONANAME));
		friendStatus.setText(friend.get(Friends.KEY_PERSONASTATE));
		
		listviewImageDownloader.download(friend.get(Friends.KEY_AVATAR), friendAvatar);
 		
		return rowView;
	}
	
	public ListviewImageDownloader getImageDownloader(){
		return listviewImageDownloader;
	}
}