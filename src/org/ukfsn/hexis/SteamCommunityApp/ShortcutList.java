package org.ukfsn.hexis.SteamCommunityApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShortcutList extends Activity{
	
	Net net = new Net();
	SteamDatabase steamDatabase = new SteamDatabase(this);
	
	final Context context = this;
	String jsonString = null;
	TextView emptyText;
	ListView list;
	
	Cursor c;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shortcutlist);
		
		TextView emptyText = (TextView)findViewById(R.id.shortcutEmpty);
		emptyText.setVisibility(View.INVISIBLE);
		steamDatabase.open();
		c = steamDatabase.getFavList();
		if(c != null && c.getCount() > 0){			
			final SimpleCursorAdapter favAdapter = new SimpleCursorAdapter(this, 
					R.layout.shortcutlistview, 
					c, 
					new String[]{c.getColumnName(1),c.getColumnName(2)}, 
					new int[]{R.id.shortcutVanityName, R.id.shortcutSteamID});
			list = (ListView)findViewById(R.id.shortcutList);
			list.setAdapter(favAdapter);
			list.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View view, int pos, long id){
					c.moveToPosition(pos);
					new profileJSON().execute(c.getString(2));
				}
			});
			list.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id){
					c.moveToPosition(pos);

					AlertDialog.Builder delBuilder = new AlertDialog.Builder(context);
					delBuilder
						.setTitle("Confirm deletion")
						.setMessage("Do you want to delete " +c.getString(1)+ "?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								steamDatabase.open();
								steamDatabase.deleteFavProfile(c.getString(0));
								c = steamDatabase.getFavList();
								favAdapter.changeCursor(c);
								steamDatabase.close();	
								
								CharSequence profileDel = getString(R.string.shortcutDel);
								int duration = Toast.LENGTH_SHORT;
								Toast profileDelToast = Toast.makeText(context, profileDel, duration);
								profileDelToast.show();
							}
						})
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
					AlertDialog delAlert = delBuilder.create();
					delAlert.show();
					return true;
				}
				
			});
		}
		else{
			emptyText.setVisibility(View.VISIBLE);
		}
		steamDatabase.close();
	
		
	}

	class profileJSON extends AsyncTask<String, Integer, String>{
		
		ProgressBar shortcutProgress = (ProgressBar)findViewById(R.id.shortcutProgress);
		
		protected void onPreExecute(){
			shortcutProgress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected String doInBackground(String... steamid){
			String profileJSON = net.getData("http://picpit.net/kyu/proxy.php?mode=profile&steamid="+steamid[0]);
			
			return profileJSON;
		}
		
		protected void onPostExecute(String profileJSON){
			shortcutProgress.setVisibility(View.INVISIBLE);
			Intent toProfile = new Intent();
			toProfile.setClass(ShortcutList.this,Profile.class);
			toProfile.putExtra("profileJSON", profileJSON);
    		startActivity(toProfile);
		}
	}
}
