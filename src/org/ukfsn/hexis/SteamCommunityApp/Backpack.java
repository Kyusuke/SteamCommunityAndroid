package org.ukfsn.hexis.SteamCommunityApp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Backpack extends Activity{

	Net net = new Net();
	SteamDatabase steamDB = new SteamDatabase(this);
	
	String steamID;
	
	String schema;
	String sharedPref = "SteamCommPrefs";
	String schemaFilename = "tf2itemschema.txt";
	String schemaJSON;
	
	FileOutputStream fos;
	SharedPreferences settings;
	String dateMod;
	TextView backpackStatus;
	BufferedReader r;
	StringBuilder sb;

	JSONArray items;
	JSONArray schemaItems;
	
	private ArrayList<TreeMap<String, String>> backpackList;
	
	String defindex = null;
	String level = null;
	String quality = null;
	String origin = null;
	String bpDefindex = null;
	String bpItemName = null;
	String bpDescription = null;
	String bpImage = null;
	
	static final String KEY_DEFINDEX = "defindex";
	static final String KEY_LEVEL = "level";
	static final String KEY_QUALITY = "quality";
	static final String KEY_ORIGIN = "origin";
	static final String KEY_BPDEFINDEX = "defindex";
	static final String KEY_BPITEMNAME = "item_name";
	static final String KEY_BPDESCRIPTION = "item_description";
	static final String KEY_BPIMAGE = "image_url";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backpack);
		
		Bundle bundle = getIntent().getExtras();
		steamID = bundle.getString("steamID");
		
		settings = getSharedPreferences(sharedPref, 0);
		String modDate = settings.getString("schemaModDate", "0");
		
		String dateCheck = net.getData("http://picpit.net/kyu/proxy.php?mode=itemschemadatecheck&modifieddate=" +modDate);
		Log.e("dateCheck", dateCheck);
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
			backpackStatus.setVisibility(View.VISIBLE);
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
			backpackStatus.setVisibility(View.VISIBLE);
			backpackStatus.setText("Loading schema");
		}
		
		@Override
		protected String doInBackground(ByteArrayOutputStream... arg0) {
			try {
				r = new BufferedReader(new InputStreamReader(openFileInput(schemaFilename), "utf8"),8192);
				sb = new StringBuilder();
				String line;
				while((line = r.readLine()) != null){
					sb.append(line);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return sb.toString();
		}
		
		protected void onPostExecute(String result){
			schemaJSON = result;
			try {
				setSchemaItems(schemaJSON);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			Log.e("profileBackpack", profileBackpack);

			return profileBackpack;
		}
		
		protected void onPostExecute(String profileBackpack){
			backpackList = new ArrayList<TreeMap<String, String>>();
			try {
				parseJSON(profileBackpack);

				for(int i = 0; i < items.length(); i++){
					JSONObject backpackItem = items.getJSONObject(i);
					
					defindex = backpackItem.getString(KEY_DEFINDEX);
					level = backpackItem.getString(KEY_LEVEL);
					quality = backpackItem.getString(KEY_QUALITY);
					origin = backpackItem.getString(KEY_ORIGIN);
					
					for(int j = 0; j < schemaItems.length(); j++){
						JSONObject item = schemaItems.getJSONObject(j);
						bpDefindex = item.getString(KEY_BPDEFINDEX);
						if(defindex.equals(bpDefindex)){
							bpItemName = item.getString(KEY_BPITEMNAME);
							try {
								bpDescription = item.getString(KEY_BPDESCRIPTION);
							} catch (Exception e) {
								bpDescription = "";
							}
							bpImage = item.getString(KEY_BPIMAGE);
							
							TreeMap<String, String> map = new TreeMap<String, String>();
							map.put(KEY_BPDEFINDEX, bpDefindex);
							map.put(KEY_LEVEL, level);
							map.put(KEY_QUALITY, quality);
							map.put(KEY_ORIGIN, origin);
							map.put(KEY_BPITEMNAME, bpItemName);
							map.put(KEY_BPDESCRIPTION, bpDescription);
							map.put(KEY_BPIMAGE, bpImage);
							
							backpackList.add(map);
							break;
						}
					}
									
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			backpackStatus.setVisibility(View.INVISIBLE);
			ListView list=(ListView)findViewById(R.id.backpackList);
			BackpackListAdapter adapter=new BackpackListAdapter(Backpack.this, backpackList);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View view, int pos, long id){
					ImageView viewImageAvatar = (ImageView)view.findViewById(R.id.backpackItemIcon);
					Drawable viewDrawableAvatar = viewImageAvatar.getDrawable();
					TreeMap<String, String> map = backpackList.get(pos);
					
					Context mContext = Backpack.this;
					final Dialog bpDialog = new Dialog(mContext);

					bpDialog.setContentView(R.layout.backpackdialog);
					bpDialog.setTitle(map.get(Backpack.KEY_BPITEMNAME));

					ImageView bpIcon = (ImageView) bpDialog.findViewById(R.id.bpIcon);
					TextView bpItem = (TextView) bpDialog.findViewById(R.id.bpItemName);
					TextView bpDescription = (TextView) bpDialog.findViewById(R.id.bpDescription);
					TextView bpLevel = (TextView) bpDialog.findViewById(R.id.bpLevel);
					TextView bpQuality = (TextView) bpDialog.findViewById(R.id.bpQuality);
					TextView bpOrigin = (TextView) bpDialog.findViewById(R.id.bpOrigin);
					Button bpButtonDialogCancel = (Button) bpDialog.findViewById(R.id.bpDialogCancel);
					
					bpIcon.setImageDrawable(viewDrawableAvatar);
					bpItem.setText(map.get(Backpack.KEY_BPITEMNAME));
					bpDescription.setText(map.get(Backpack.KEY_BPDESCRIPTION));
					bpLevel.setText(map.get(Backpack.KEY_LEVEL));
					bpQuality.setText(map.get(Backpack.KEY_QUALITY));
					bpOrigin.setText(map.get(Backpack.KEY_ORIGIN));
					bpButtonDialogCancel.setText("Return to list");
					
					bpButtonDialogCancel.setOnClickListener(new View.OnClickListener() {			
						@Override
						public void onClick(View v) {
							bpDialog.dismiss();
						}
					});
					
					bpDialog.show();
				}
			});
		}
	}
	
	public void parseJSON(String result) throws JSONException {
		JSONObject jsonObject = new JSONObject(result);				
		JSONObject response = jsonObject.getJSONObject("result");
		items = response.getJSONArray("items");
	}
	
	public void setSchemaItems(String result) throws JSONException{
		JSONObject jsonObject = new JSONObject(result);
		JSONObject response = jsonObject.getJSONObject("result");
		schemaItems = response.getJSONArray("items");
	}
	
}