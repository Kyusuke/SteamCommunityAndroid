package org.ukfsn.hexis.SteamCommunityApp;

public class DBProfiles {
	
	int favID;
	String favVanityName;
	int favSteamID;
	
	int hisID;
	String hisVanityName;
	int hisSteamID;
	
	public void setFavouriteProfile(String favVanityName, int favSteamID){
		this.favVanityName = favVanityName;
		this.favSteamID = favSteamID;
	}
	
	public void setFavID(int favID){
		this.favID = favID;
	}
	
	public int getFavID(){
		return this.favID;
	}
	
	public String getFavVanityName(){
		return this.favVanityName;
	}
	
	public int getFavSteamID(){
		return this.favSteamID;
	}
	
	public void setHistoryProfile(String hisVanityName, int hisSteamID){
		this.hisVanityName = hisVanityName;
		this.hisSteamID = hisSteamID;
	}
	
	public void setHisID(int hisID){
		this.hisID = hisID;
	}
	
	public int getHisID(){
		return this.hisID;
	}
	
	public String getHisVanityName(){
		return this.hisVanityName;
	}
	
	public int getHisSteamID(){
		return this.hisSteamID;
	}
}
