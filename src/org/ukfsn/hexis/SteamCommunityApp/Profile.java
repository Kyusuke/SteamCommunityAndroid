package org.ukfsn.hexis.SteamCommunityApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends Activity implements OnClickListener {

	Main main = new Main();
	Net net = new Net();
	SteamDatabase steamDatabase = new SteamDatabase(this);
	
	JSONObject jsonObject;
	
	String user = null;
	String steamid = null;
	String personaname = null;
	String avatarURL = null;
	String headline = null;
	String summary = null;
	String joinDate = null;
	String hoursPlayed = null;
	String rating = null;
	String visibilityState = null;
	
	Button buttonFriends;
	Button buttonBackpack;
	Button buttonAddToFav;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		Bundle bundle = getIntent().getExtras();
		String profileJSON = bundle.getString("profileJSON");
		user = bundle.getString("user");
		
		try {
			parseJSON(profileJSON);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(visibilityState.equals("3")){
			super.onCreate(savedInstanceState);
			setContentView(R.layout.profile);
			
			new Thread (new Runnable() {
				public void run() {
					final ImageView steamAvatar = (ImageView)findViewById(R.id.userAvatar);
					final Drawable avatar = net.getImage(avatarURL);
					steamAvatar.post(new Runnable() {
						public void run(){
							steamAvatar.setImageDrawable(avatar);
						}
					});
				}
			}).start();
			
			TextView personanameEdit = (TextView)findViewById(R.id.personaname);
			personanameEdit.setText(personaname);
			
			TextView headlineEdit = (TextView)findViewById(R.id.headline);
			headlineEdit.setText(headline);
			
			TextView summaryEdit = (TextView)findViewById(R.id.summary);
			summaryEdit.setText(summary);
			
			TextView joinDateEdit = (TextView)findViewById(R.id.joinDate);
			joinDateEdit.setText(personaname+ " joined at " +joinDate);
			
			TextView hoursPlayedEdit = (TextView)findViewById(R.id.hoursPlayed);
			hoursPlayedEdit.setText(personaname+ " has played " +hoursPlayed+ "hours in their past two weeks.");
			
			TextView ratingEdit = (TextView)findViewById(R.id.rating);
			ratingEdit.setText("Therefore, " +personaname+ " has a rating of " +rating);
			
			buttonFriends = (Button)findViewById(R.id.buttonFriends);
			buttonFriends.setOnClickListener(this);
			
			buttonBackpack = (Button)findViewById(R.id.buttonBackpack);
			buttonBackpack.setOnClickListener(this);
			
			buttonAddToFav = (Button)findViewById(R.id.buttonAddToFav);
			buttonAddToFav.setVisibility(View.VISIBLE);
			steamDatabase.open();
			Cursor c = steamDatabase.getFavProfile(steamid);
			if(c.getCount() > 0){
				buttonAddToFav.setVisibility(View.INVISIBLE);
			}
			else{
				buttonAddToFav.setOnClickListener(this);
			}
			steamDatabase.close();
		}
		else{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.profileprivate);
			
			new Thread (new Runnable() {
				public void run() {
					final ImageView steamAvatar = (ImageView)findViewById(R.id.userAvatar);
					final Drawable avatar = net.getImage(avatarURL);
					steamAvatar.post(new Runnable() {
						public void run(){
							steamAvatar.setImageDrawable(avatar);
						}
					});
				}
			}).start();
			
			TextView personanameEdit = (TextView)findViewById(R.id.personaname);
			personanameEdit.setText(personaname);
		}
	}
	
	public void parseJSON(String result) throws JSONException {
		jsonObject = new JSONObject(result);
				
		JSONObject response = jsonObject.getJSONObject("response");
		JSONArray players = response.getJSONArray("players");
		
		for(int i=0;i<players.length(); i++)
		{
			steamid = players.getJSONObject(i).getString("steamid");
			personaname = players.getJSONObject(i).getString("personaname");
			avatarURL = players.getJSONObject(i).getString("avatarmedium");
		}

		visibilityState = jsonObject.getString("visibilityState");
		if(visibilityState.equals("3")){
			headline = jsonObject.getString("headline");
			summary = jsonObject.getString("summary");
			joinDate = jsonObject.getString("memberSince");
			hoursPlayed = jsonObject.getString("hoursPlayed2Wk");
			rating = jsonObject.getString("steamRating");
		}	

    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonFriends:
			Intent intentFriends = new Intent();
			intentFriends.setClass(this, Friends.class);
			intentFriends.putExtra("steamID", steamid);
			startActivity(intentFriends);
			break;
		case R.id.buttonBackpack:
			Intent intentBackpack = new Intent();
			intentBackpack.setClass(this, Backpack.class);
			intentBackpack.putExtra("steamID", steamid);
			startActivity(intentBackpack);
			break;
		case R.id.buttonAddToFav:
			steamDatabase.open();
			steamDatabase.addFavProfile(personaname,steamid);
			steamDatabase.close();
			buttonAddToFav.setVisibility(View.INVISIBLE);
			Context context = getApplicationContext();
	        CharSequence userBlank = getString(R.string.profileFavAdded);
	    	int duration = Toast.LENGTH_SHORT;
			Toast userBlankToast = Toast.makeText(context, userBlank, duration);
			userBlankToast.show();
			break;
		}
	}
}
