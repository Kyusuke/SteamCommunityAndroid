package org.ukfsn.hexis.SteamCommunityApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Main extends Activity implements OnClickListener, OnKeyListener {
	
	ProgressBar progressBar;
	Net net = new Net();
	String user = null;
	EditText userForm;
	
	String profileJSON;
	JSONObject jsonObject;
	JSONArray players;
	Button button;
	
	String steamid = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		progressBar = (ProgressBar)findViewById(R.id.userSearchProgress);
		progressBar.setVisibility(View.INVISIBLE);
		
		button = (Button)findViewById(R.id.buttonSearch);
		button.setOnClickListener(this);
		
		userForm = (EditText)findViewById(R.id.userText);
		userForm.setOnKeyListener(this);
	}
	
	public void onClick (View v){
		user = userForm.getText().toString();
		if(user.length() > 0){
			button.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
			new Thread (new Runnable() {
				public void run() {
					profileJSON = net.getData("http://picpit.net/kyu/proxy.php?mode=profile&steamid=" +user);
					try {
						parseJSON(profileJSON);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.e("JSON Return", profileJSON);
					if (steamid.equals("0")){
						final Context context = getApplicationContext();
				        final CharSequence userBlank = getString(R.string.mainidInvalid);
				    	final int duration = Toast.LENGTH_SHORT;
						progressBar.post(new Runnable() {
							public void run() {
								Toast invalidID = Toast.makeText(context, userBlank, duration);
								progressBar.setVisibility(View.INVISIBLE);
								button.setVisibility(View.VISIBLE);
								invalidID.show(); 
							}
						});			
					}
					else{
						Intent myintent = new Intent();
			    		myintent.setClass(Main.this,Profile.class);
			    		myintent.putExtra("profileJSON", profileJSON);
			    		progressBar.post(new Runnable() {
			    			public void run() {
			    			progressBar.setVisibility(View.INVISIBLE);
			    			button.setVisibility(View.VISIBLE);
			    			}
			    		});
			    		startActivity(myintent);
					}
				}
			}).start();
		}
		else{
			Context context = getApplicationContext();
	        CharSequence userBlank = getString(R.string.mainidBlank);
	    	int duration = Toast.LENGTH_SHORT;
			Toast userBlankToast = Toast.makeText(context, userBlank, duration);
			userBlankToast.show();
		}
	}
	
	public boolean onKey (View v, int keyCode, KeyEvent event) {
		if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
	            (keyCode == KeyEvent.KEYCODE_ENTER)) {
	          onClick(v);
	          return true;
	        }
	        return false;
	}
	
	public void parseJSON(String result) throws JSONException {
		jsonObject = new JSONObject(result);
				
		JSONObject response = jsonObject.getJSONObject("response");
		players = response.getJSONArray("players");
		
		for(int i=0;i<players.length(); i++)
		{
			steamid = players.getJSONObject(i).getString("steamid");
		}		
    }
	
}
