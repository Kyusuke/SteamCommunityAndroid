package org.ukfsn.hexis.SteamCommunityApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Profile extends Activity {

	Main main = new Main();
	Net net = new Net();
	
	JSONObject jsonObject;
	
	String personaname = null;
	String avatarURL = null;
	String headline = null;
	String summary = null;
	String joinDate = null;
	String hoursPlayed = null;
	String rating = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		Bundle bundle = getIntent().getExtras();
		String profileJSON = bundle.getString("profileJSON");
		
		try {
			parseJSON(profileJSON);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		hoursPlayedEdit.setText(personaname+ " has wasted " +hoursPlayed+ "hours of their past two weeks.");
		
		TextView ratingEdit = (TextView)findViewById(R.id.rating);
		ratingEdit.setText("Therefore, " +personaname+ " has an awesome rating of " +rating);
	}
	
	public void parseJSON(String result) throws JSONException {
		jsonObject = new JSONObject(result);
				
		JSONObject response = jsonObject.getJSONObject("response");
		JSONArray players = response.getJSONArray("players");
		
		for(int i=0;i<players.length(); i++)
		{
			personaname = players.getJSONObject(i).getString("personaname");
			avatarURL = players.getJSONObject(i).getString("avatarmedium");
		}

		headline = jsonObject.getString("headline");
		summary = jsonObject.getString("summary");
		joinDate = jsonObject.getString("memberSince");
		hoursPlayed = jsonObject.getString("hoursPlayed2Wk");
		rating = jsonObject.getString("steamRating");

    }
}
