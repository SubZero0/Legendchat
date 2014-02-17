package br.com.devpaulo.legendchat.ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

public class IgnoreManager {
	private static HashMap<Player,List<String>> ignoreList =  new HashMap<Player,List<String>>();
	public IgnoreManager() {
	}
	
	public void playerIgnorePlayer(Player who, String ignored) {
		if(hasPlayerIgnoredPlayer(who,ignored))
			return;
		List<String> ignorados = new ArrayList<String>();
		if(playerHasIgnoreList(who)) {
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
		if(playerHasIgnoreList(who))
			return ignoreList.get(who).contains(ignored.toLowerCase());
		return false;
	}
	
	public void playerDisconnect(Player p) {
		if(playerHasIgnoreList(p))
			ignoreList.remove(p);
	}
	
	public boolean playerHasIgnoreList(Player p) {
		return ignoreList.containsKey(p);
	}
	
	public List<String> getPlayerIgnoredList(Player p) {
		if(playerHasIgnoreList(p))
			return ignoreList.get(p);
		return null;
	}
}
