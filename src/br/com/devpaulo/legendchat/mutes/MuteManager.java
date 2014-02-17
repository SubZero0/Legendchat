package br.com.devpaulo.legendchat.mutes;

import java.util.HashMap;

public class MuteManager {
	private static HashMap<String,Mute> mutes = new HashMap<String,Mute>();
	private static boolean muteall = false;
	public MuteManager() {
	}
	
	public void mutePlayer(String name, int time) {
		name = name.toLowerCase();
		if(isPlayerMuted(name)) {
			getPlayerMute(name).mute(time);
		}
		else {
			Mute m = new Mute(name);
			mutes.put(name, m);
			m.mute(time);
		}
	}
	
	public Mute getPlayerMute(String name) {
		name = name.toLowerCase();
		if(isPlayerMuted(name))
			return mutes.get(name);
		return null;
	}
	
	public int getPlayerMuteTimeLeft(String name) {
		name = name.toLowerCase();
		if(isPlayerMuted(name))
			return mutes.get(name).getTimeLeft();
		return 0;
	}
	
	public void unmutePlayer(String name) {
		name = name.toLowerCase();
		if(!isPlayerMuted(name))
			return;
		getPlayerMute(name).unmute();
		mutes.remove(name);
	}
	
	public void removePlayerMute(String name) {
		name = name.toLowerCase();
		if(!isPlayerMuted(name))
			return;
		mutes.remove(name);
	}
	
	public boolean isPlayerMuted(String name) {
		return mutes.containsKey(name.toLowerCase());
	}
	
	public boolean isServerMuted() {
		return muteall;
	}
	
	public void muteServer() {
		muteall=true;
	}
	
	public void unmuteServer() {
		muteall=false;
	}

}
