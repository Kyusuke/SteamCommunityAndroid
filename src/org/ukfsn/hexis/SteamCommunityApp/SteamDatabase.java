package org.ukfsn.hexis.SteamCommunityApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SteamDatabase extends SQLiteOpenHelper{

	private static final String dbName = "steamDB";
	private static final int dbVersion = 1;
	private SQLiteDatabase db;
	
	//Table information for storing favourite Steam profiles
	private static final String favouriteTable = "Favourites";
	private static final String favID = "FavouritesID";
	private static final String favVanityName = "FavouritesVanityName";
	private static final String favSteamID = "FavouritesSteamID";
	
	//Table information for storing recently browsed Steam profiles
	private static final String historyTable = "History";
	private static final String hisID = "HistoryID";
	private static final String hisVanityName = "HistoryVanityName";
	private static final String hisSteamID = "HistorySteamID";
	
	public SteamDatabase(Context context) {
		super(context, dbName, null, dbVersion);
	}

	public void onCreate(SQLiteDatabase dbCreate){
		dbCreate.execSQL("CREATE TABLE " +favouriteTable+ " (" +favID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +favVanityName+ " TEXT, " +favSteamID+ " INTEGER)");
		dbCreate.execSQL("CREATE TABLE " +historyTable+ " (" +hisID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +hisVanityName+ " TEXT, " +hisSteamID+ " INTEGER)");
	}
	
	public void onUpgrade(SQLiteDatabase dbUpdate, int oldVersion, int newVersion){
		dbUpdate.execSQL("DROP TABLE IF EXISTS " +favouriteTable);
		dbUpdate.execSQL("DROP TABLE IF EXISTS " +historyTable);
		onCreate(dbUpdate);
	}
	
	public void open(){
		db = this.getWritableDatabase();
	}
	
	public void close(){
		db.close();
	}
	
	void addFavProfile(String getFavVanityName, String getFavSteamID){
		ContentValues cv = new ContentValues();
		
		cv.put(favVanityName, getFavVanityName);
		cv.put(favSteamID, getFavSteamID);
		db.insert(favouriteTable, null, cv);
	}
	
	public void deleteFavProfile(String getFavId){
		db.delete(favouriteTable, favID+ " = " +getFavId, null);
	}
	
	public Cursor getFavList(){
		Cursor favCursor = db.query(favouriteTable, 
				new String[]{favID+ " AS _id", favVanityName,favSteamID}, 
				null, null, null, null, favID);
		return favCursor;
	}
	
	public Cursor getFavProfile(String steamID){
		Cursor favCursor = db.query(favouriteTable, null, 
				favSteamID+ " = " +steamID, null, null, null, null);
		return favCursor;
	}
	
	void addHisProfile(DBProfiles his){
		ContentValues cv = new ContentValues();
		
		cv.put(hisVanityName, his.getFavVanityName());
		cv.put(hisSteamID, his.getFavSteamID());
		db.insert(historyTable, null, cv);
	}
	
	public void deleteHisProfile(DBProfiles his){
		db.delete(historyTable, hisSteamID+ " = " + (String.valueOf(his.getHisID())), null);
	}
}
