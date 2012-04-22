package org.ukfsn.hexis.SteamCommunityApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BackpackListAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<TreeMap<String, String>> data;
	private LayoutInflater inflater = null;
	
	private ImageView backpackItemIcon;
	private TreeMap<String, String> backpack;
	
	public BackpackListAdapter(Activity a, ArrayList<TreeMap<String, String>> d){
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
	public View getView(int pos, View convertView, ViewGroup parent){
		View rowView = convertView;	
		
		if (rowView == null){
			rowView = inflater.inflate(R.layout.backpacklistview, null);
		}
			
		TextView backpackItemName = (TextView) rowView.findViewById(R.id.backpackItemName);
		TextView backpackDefindex = (TextView) rowView.findViewById(R.id.backpackDefindex);
		backpackItemIcon = (ImageView) rowView.findViewById(R.id.backpackItemIcon);
		
		backpack = new TreeMap<String, String>();
		backpack = data.get(pos);
		
		String bpDefindex = backpack.get(Backpack.KEY_BPDEFINDEX);
		String bpName = backpack.get(Backpack.KEY_BPITEMNAME);
		String image = backpack.get(Backpack.KEY_BPIMAGE);
		
		backpackItemName.setText(bpName);
		backpackDefindex.setText(bpDefindex);
		
		backpackItemIcon.setMaxHeight(50);
		backpackItemIcon.setMaxWidth(50);
		listviewImageDownloader.download(image, backpackItemIcon);
			
		return rowView;
	}
	
	public ListviewImageDownloader getImageDownloader(){
		return listviewImageDownloader;
	}
}
