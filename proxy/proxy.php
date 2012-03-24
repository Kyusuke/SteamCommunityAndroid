<?php
//error_reporting(E_ALL ^ E_NOTICE);

$filename = "key";
$handle = fopen($filename, "r");
$key = fread($handle, filesize($filename));
fclose($handle);

$mode=$_GET["mode"];
$steamid=$_GET["steamid"];

$ch = curl_init();

curl_setopt($ch, CURLOPT_HEADER, 0);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
curl_setopt($ch, CURLOPT_TIMEOUT, 5);

if (is_numeric($steamid) && strlen($steamid) == 17){
	}
else{
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_URL, "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=".$key."&vanityurl=".$steamid."&format=json");
	$vanity = curl_exec($ch);
	$vanity = json_decode($vanity, true);
	$steamid = $vanity['response']['steamid'];
	curl_close($ch);
	$ch = curl_init();
	}
	
if ($mode == 'profile'){
	header("Content-type: application/json");
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_URL, "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=".$key."&steamids=".$steamid."&format=json");
	$api = curl_exec($ch);
	$api = json_decode($api, true);
	
	//No profile exception
	if(empty($api['response']['players'])){
		$api['response']['players'][0]['steamid'] = "0";
		$api = json_encode($api, JSON_PRETTY_PRINT);
		echo $api;
	}
	else{
		$chxml = curl_init();
		curl_setopt($chxml, CURLOPT_HEADER, 0);
		curl_setopt($chxml, CURLOPT_FOLLOWLOCATION, 1);
		curl_setopt($chxml, CURLOPT_TIMEOUT, 5);
		curl_setopt($chxml, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($chxml, CURLOPT_URL, "http://steamcommunity.com/profiles/".$steamid."/?xml=1");
		$xml = curl_exec($chxml);
		curl_close($chxml);
		$xml = (array) simplexml_load_string($xml,'SimpleXMLElement', LIBXML_NOCDATA);
		
		$profile = array_merge($api, $xml);
		$profile = json_encode($profile, JSON_PRETTY_PRINT);
		echo $profile;
	}
}
elseif ($mode == 'friends'){
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_URL, "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=".$key."&steamid=".$steamid."&relationship=friend&format=json");
	$friends = curl_exec($ch);
	$friends = json_decode($friends, true);
	$friendcount = count($friends['friendslist']['friends']);
	$friendpart = ceil($friendcount / 100);
	for($i=0; $i < $friendpart; $i++){
		$friendlistcount = $friendcount - (100 * $i);
		if ($friendlistcount > 100){
			$count = 100;
		}
		else{
			$count = $friendlistcount;
		}
		for($l=0 + (100 * $i); $l < $count + (100 * $i); $l++){
			$friendstring[$i] = $friendstring[$i].$friends['friendslist']['friends'][$l]['steamid'].",";
		}
	}
	for($i=0; $i < $friendpart; $i++){
		$fprofiles = curl_init();
		curl_setopt($fprofiles, CURLOPT_HEADER, 0);
		curl_setopt($fprofiles, CURLOPT_FOLLOWLOCATION, 1);
		curl_setopt($fprofiles, CURLOPT_TIMEOUT, 5);
		curl_setopt($fprofiles, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($fprofiles, CURLOPT_URL, "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=".$key."&steamids=".$friendstring[$i]."&format=json");
		$fdata[$i] = curl_exec($fprofiles);
		$fdata[$i] = json_decode($fdata[$i], true);
		curl_close($fprofiles);
	}
	for($i=0; $i < $friendpart; $i++){
		$fcount = count($fdata[$i]['response']['players']);
		for($j=0; $j < $fcount; $j++){
			$fid = $fdata[$i]['response']['players'][$j]['steamid'];
			for($k=0; $k < $friendcount; $k++){
				if($fid == $friends['friendslist']['friends'][$k]['steamid'])
				{
					$fstep = (int)($j + ($i * 100));
					$foutput['summary'][$fstep]['steamid'] = $fdata[$i]['response']['players'][$j]['steamid'];
					$foutput['summary'][$fstep]['friend_since'] = $friends['friendslist']['friends'][$k]['friend_since'];
					$foutput['summary'][$fstep]['personaname'] = $fdata[$i]['response']['players'][$j]['personaname'];
					$foutput['summary'][$fstep]['lastlogoff'] = $fdata[$i]['response']['players'][$j]['lastlogoff'];
					$foutput['summary'][$fstep]['personastate'] = $fdata[$i]['response']['players'][$j]['personastate'];
					$foutput['summary'][$fstep]['avatar'] = $fdata[$i]['response']['players'][$j]['avatar'];
					break 1;
				}
			}
		}
	}
	$jsoncounting = count($foutput['summary']);
	
	//If not PHP v5.4.0 or later, remove JSON_PRETTY_PRINT
	$foutput = json_encode($foutput, JSON_PRETTY_PRINT);
	header("Content-type: application/json");
	echo $foutput;

	}
elseif ($mode == 'backpack'){
	header("Content-type: application/json");
	curl_setopt($ch, CURLOPT_URL, "http://api.steampowered.com/IEconItems_440/GetPlayerItems/v0001/?key=".$key."&steamid=".$steamid."&format=json");
	curl_exec($ch);
	}
	
// TODO: Remember to add in the item schema
curl_close($ch);
?> 
