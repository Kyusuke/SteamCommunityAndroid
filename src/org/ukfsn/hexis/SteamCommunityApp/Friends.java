package org.ukfsn.hexis.SteamCommunityApp;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

public class Friends extends Activity {
	
	Net net = new Net();
	
	String jsonString = null;
	JSONArray summary = null;
	
	static final String KEY_PERSONANAME = "personaname";
	static final String KEY_PERSONASTATE = "personastate";
	static final String KEY_AVATAR = "avatar";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		final ProgressBar friendProgress = (ProgressBar)findViewById(R.id.friendProgress);
		friendProgress.setVisibility(View.VISIBLE);
		
		new Thread (new Runnable() {
			public void run() {
		
				ArrayList<HashMap<String, String>> friendList = new ArrayList<HashMap<String, String>>();
				
				Bundle bundle = getIntent().getExtras();
				String steamID = bundle.getString("steamID");
				
				jsonString = net.getData("http://picpit.net/kyu/proxy.php?mode=friends&steamid=" +steamID);
				try {
					parseJSON(jsonString);
					
					for(int i = 0; i < summary.length(); i++){
						JSONObject friendObject = summary.getJSONObject(i);
						
						String personaname = friendObject.getString(KEY_PERSONANAME);
						String personastate = friendObject.getString(KEY_PERSONASTATE);
						String avatar = friendObject.getString(KEY_AVATAR);
						
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(KEY_PERSONANAME,personaname);
						map.put(KEY_PERSONASTATE, personastate);
						map.put(KEY_AVATAR, avatar);
						
						friendList.add(map);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				final ListView list=(ListView)findViewById(R.id.friendList);
		
		        final FriendsListAdapter adapter=new FriendsListAdapter(Friends.this, friendList);
		        friendProgress.post(new Runnable() {
		        	public void run() {
		        		list.setAdapter(adapter);
						friendProgress.setVisibility(View.INVISIBLE);
		        	}
		        });		        
			}
		}).start();
	}
	
	public void parseJSON(String result) throws JSONException {
		JSONObject jsonObject = new JSONObject(result);
				
		summary = jsonObject.getJSONArray("summary");
	}
}
