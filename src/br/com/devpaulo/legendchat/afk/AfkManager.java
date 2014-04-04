package br.com.devpaulo.legendchat.afk;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class AfkManager {
	private HashMap<Player,String> afk = new HashMap<Player,String>();
	
	public void setAfk(Player p, String motivo) {
		removeAfk(p);
		if(motivo.equals(" ")||motivo.length()==0)
			motivo=null;
		afk.put(p, motivo);
	}
	
	public void removeAfk(Player p) {
		if(isAfk(p))
			afk.remove(p);
	}
	
	public boolean isAfk(Player p) {
		return afk.containsKey(p);
	}
	
	public String getPlayerAfkMotive(Player p) {
		if(isAfk(p))
			return afk.get(p);
		return null;
	}

	public void playerDisconnect(Player p) {
		removeAfk(p);
	}
}
