package br.com.devpaulo.legendchat.ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.channels.types.Channel;

public class IgnoreManager {
	private HashMap<Player,List<String>> ignoreList =  new HashMap<Player,List<String>>();
	private HashMap<Player,List<Channel>> ignoreList2 =  new HashMap<Player,List<Channel>>();
	public IgnoreManager() {
	}
	
	public void playerIgnorePlayer(Player who, String ignored) {
		if(hasPlayerIgnoredPlayer(who,ignored))
			return;
		List<String> ignorados = new ArrayList<String>();
		if(playerHasIgnoredPlayersList(who)) {
			ignorados.addAll(ignoreList.get(who));
			ignoreList.remove(who);
		}
		ignorados.add(ignored.toLowerCase());
		ignoreList.put(who, ignorados);
	}
	
	public void playerUnignorePlayer(Player who, String ignored) {
		if(!hasPlayerIgnoredPlayer(who,ignored))
			return;
		List<String> ignorados = ignoreList.get(who);
		ignorados.remove(ignored.toLowerCase());
		ignoreList.remove(who);
		if(ignorados.size()>0)
			ignoreList.put(who, ignorados);
	}
	
	public boolean hasPlayerIgnoredPlayer(Player who, String ignored) {
		if(playerHasIgnoredPlayersList(who))
			return ignoreList.get(who).contains(ignored.toLowerCase());
		return false;
	}
	
	public void playerIgnoreChannel(Player who, Channel ignored) {
		if(hasPlayerIgnoredChannel(who,ignored))
			return;
		List<Channel> ignorados = new ArrayList<Channel>();
		if(playerHasIgnoredChannelsList(who)) {
			ignorados.addAll(ignoreList2.get(who));
			ignoreList2.remove(who);
		}
		ignorados.add(ignored);
		ignoreList2.put(who, ignorados);
	}
	
	public void playerUnignoreChannel(Player who, Channel c) {
		if(!hasPlayerIgnoredChannel(who,c))
			return;
		List<Channel> ignorados = ignoreList2.get(who);
		ignorados.remove(c);
		ignoreList2.remove(who);
		if(ignorados.size()>0)
			ignoreList2.put(who, ignorados);
	}
	
	public boolean hasPlayerIgnoredChannel(Player who, Channel ignored) {
		if(playerHasIgnoredChannelsList(who))
			return ignoreList2.get(who).contains(ignored);
		return false;
	}
	
	public void playerDisconnect(Player p) {
		if(playerHasIgnoredPlayersList(p))
			ignoreList.remove(p);
		if(playerHasIgnoredChannelsList(p))
			ignoreList2.remove(p);
	}
	
	public boolean playerHasIgnoredPlayersList(Player p) {
		return ignoreList.containsKey(p);
	}
	
	public List<String> getPlayerIgnoredPlayersList(Player p) {
		if(playerHasIgnoredPlayersList(p))
			return ignoreList.get(p);
		return null;
	}
	
	public boolean playerHasIgnoredChannelsList(Player p) {
		return ignoreList2.containsKey(p);
	}
	
	public List<Channel> getPlayerIgnoredChannelsList(Player p) {
		if(playerHasIgnoredChannelsList(p))
			return ignoreList2.get(p);
		return null;
	}
}
