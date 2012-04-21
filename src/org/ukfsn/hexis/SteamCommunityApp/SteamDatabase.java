package org.ukfsn.hexis.SteamCommunityApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SteamDatabase extends SQLiteOpenHelper{

	private static final String dbName = "steamDB";
	private static final int dbVersion = 1;
	
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

	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " +favouriteTable+ " (" +favID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +favVanityName+ " TEXT, " +favSteamID+ " INTEGER)");
		db.execSQL("CREATE TABLE " +historyTable+ " (" +hisID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +hisVanityName+ " TEXT, " +hisSteamID+ " INTEGER)");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " +favouriteTable);
		db.execSQL("DROP TABLE IF EXISTS " +historyTable);
		onCreate(db);
	}
	
	void addFavProfile(DBProfiles fav){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put(favVanityName, fav.getFavVanityName());
		cv.put(favSteamID, fav.getFavSteamID());
		db.insert(favouriteTable, null, cv);
		db.close();
	}
	
	public void deleteFavProfile(DBProfiles fav){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(favouriteTable, favSteamID+ " = " + (String.valueOf(fav.getFavID())), null);
		db.close();
	}
	
	void addHisProfile(DBProfiles his){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put(hisVanityName, his.getFavVanityName());
		cv.put(hisSteamID, his.getFavSteamID());
		db.insert(historyTable, null, cv);
		db.close();
	}
	
	public void deleteHisProfile(DBProfiles his){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(historyTable, hisSteamID+ " = " + (String.valueOf(his.getHisID())), null);
		db.close();
	}
}
