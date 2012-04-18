package org.ukfsn.hexis.SteamCommunityApp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsListAdapter extends BaseAdapter {
	
	Net net = new Net();

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private LayoutInflater inflater = null;
	
	public FriendsListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
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
		ImageView friendAvatar = (ImageView) rowView.findViewById(R.id.friendAvatar);
		
		HashMap<String, String> friend = new HashMap<String, String>();
		friend = data.get(pos);
		
		friendName.setText(friend.get(Friends.KEY_PERSONANAME));
		friendStatus.setText(friend.get(Friends.KEY_PERSONASTATE));
		
		Drawable avatar = net.getImage(friend.get(Friends.KEY_AVATAR));
		friendAvatar.setImageDrawable(avatar);
 		
		return rowView;
	}
}