package org.ukfsn.hexis.SteamCommunityApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Backpack extends Activity{

	Net net = new Net();
	SteamDatabase steamDB = new SteamDatabase(this);
	
	String steamID;
	
	String schema;
	String sharedPref = "SteamCommPrefs";
	String schemaFilename = "tf2itemschema.txt";
	
	FileOutputStream fos;
	FileInputStream fis;
	SharedPreferences settings;
	String dateMod;
	TextView backpackStatus;
	ByteArrayOutputStream bo= new ByteArrayOutputStream();

	JSONArray items;
	
	static final String KEY_DEFINDEX = "defindex";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backpack);
		
		Bundle bundle = getIntent().getExtras();
		steamID = bundle.getString("steamID");
		
		settings = getSharedPreferences(sharedPref, 0);
		String modDate = settings.getString("schemaModDate", "0");
		
		String dateCheck = net.getData("http://picpit.net/kyu/proxy.php?mode=itemschemadatecheck&modifieddate=" +modDate);
		dateCheck = dateCheck.substring(0, dateCheck.length() - 1);
		dateMod = dateCheck;

		backpackStatus = (TextView)findViewById(R.id.backpackStatus);
		final Context context = getApplicationContext();
    	final int duration = Toast.LENGTH_SHORT;
    	
		if(dateCheck.equals("0")){
	        new RetrieveSchema().execute();
		}
		else if(dateCheck.equals("1")){
			final CharSequence userBlank = "Data is 1";
			Toast invalidID = Toast.makeText(context, userBlank, duration);
			invalidID.show(); 
		}
		else{
			new DownloadNewSchema().execute();
		}
		
	}
	
	class DownloadNewSchema extends AsyncTask<String, Integer, String>{

		protected void onPreExecute(){
			backpackStatus.setText("New schema downloading");
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			schema = net.getData("http://picpit.net/kyu/proxy.php?mode=itemschema");
			try {
				fos = openFileOutput(schemaFilename, Context.MODE_PRIVATE);
				fos.flush();
				fos.write(schema.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return schema;
		}
		
		protected void onPostExecute(String result){
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("schemaModDate", dateMod);
			editor.commit();
			new RetrieveSchema().execute();
		}		
	}
	
	class RetrieveSchema extends AsyncTask<ByteArrayOutputStream, Integer, String>{
		
		protected void onPreExecute(){
			backpackStatus.setText("Loading schema");
		}
		
		@Override
		protected String doInBackground(ByteArrayOutputStream... arg0) {
			try {
				fis = openFileInput(schemaFilename);
				int i = fis.read();
				while(i != -1){
					bo.write(i);
					i = fis.read();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bo.toString();
		}
		
		protected void onPostExecute(String result){
			new RetrieveUserBackpack().execute();
		}
	}
	
	class RetrieveUserBackpack extends AsyncTask<Integer, Integer, String>{
		
		protected void onPreExecute(){
			backpackStatus.setText("Retrieving profile backpack");
		}
		
		@Override
		protected String doInBackground(Integer... args0){
			String profileBackpack = net.getData("http://picpit.net/kyu/proxy.php?mode=backpack&steamid=" +steamID);

			return profileBackpack;
		}
		
		protected void onPostExecute(String profileBackpack){
			ArrayList<HashMap<String, String>> backpackList = new ArrayList<HashMap<String, String>>();
			try {
				parseJSON(profileBackpack);

				for(int i = 0; i < items.length(); i++){
					JSONObject backpackItem = items.getJSONObject(i);
					
					String defindex = backpackItem.getString(KEY_DEFINDEX);
					
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(KEY_DEFINDEX, defindex);
					
					backpackList.add(map);					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			backpackStatus.setText("Everything's loaded, need to parse everything now. Good luck");
		}
	}
	
	public void parseJSON(String result) throws JSONException {
		JSONObject jsonObject = new JSONObject(result);				
		JSONObject response = jsonObject.getJSONObject("result");
		items = response.getJSONArray("items");
	}
}