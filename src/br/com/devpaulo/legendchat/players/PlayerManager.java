package br.com.devpaulo.legendchat.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.Channel;

public class PlayerManager {
	private static HashMap<Player,Channel> players = new HashMap<Player,Channel>();
	private static List<Player> spys = new ArrayList<Player>();
	private static List<Player> hidden = new ArrayList<Player>();
	public PlayerManager() {
	}
	
	public void playerDisconnect(Player p) {
		setPlayerChannel(p,null,false);
		removeSpy(p);
		showPlayerToRecipients(p);
	}
	
	public void setPlayerChannel(Player p, Channel c, boolean msg) {
		if(Legendchat.getDefaultChannel()!=c&&c!=null)
			if(!p.hasPermission("legendchat.channel."+c.getName()+".focus")&&!p.hasPermission("legendchat.admin")) {
				if(msg)
					p.sendMessage(Legendchat.getMessageManager().getMessage("error5"));
				return;
			}
		if(isPlayerInAnyChannel(p))
			players.remove(p);
		if(c!=null)
			players.put(p, c);
		if(msg)
			p.sendMessage(Legendchat.getMessageManager().getMessage("message1").replace("@channel", c.getName()));
	}
	
	public Channel getPlayerChannel(Player p) {
		if(isPlayerInAnyChannel(p))
			return players.get(p);
		return null;
	}
	
	public List<Player> getPlayersInChannel(Channel c) {
		List<Player> l = new ArrayList<Player>();
		for(Player p : players.keySet())
			if(players.get(p)==c)
				l.add(p);
		return l;
	}
	
	public boolean isPlayerInAnyChannel(Player p) {
		return players.containsKey(p);
	}
	
	public void addSpy(Player p) {
		if(!isSpy(p))
			spys.add(p);
	}
	
	public void removeSpy(Player p) {
		if(isSpy(p))
			spys.remove(p);
	}
	
	public boolean isSpy(Player p) {
		return spys.contains(p);
	}
	
	public List<Player> getSpys() {
		List<Player> l = new ArrayList<Player>();
		l.addAll(spys);
		return l;
	}
	
	public void hidePlayerFromRecipients(Player p) {
		if(!isPlayerHiddenFromRecipients(p))
			hidden.add(p);
	}
	
	public void showPlayerToRecipients(Player p) {
		if(isPlayerHiddenFromRecipients(p))
			hidden.remove(p);
	}
	
	public boolean isPlayerHiddenFromRecipients(Player p) {
		return hidden.contains(p);
	}
	
	public List<Player> getHiddenPlayers() {
		List<Player> l = new ArrayList<Player>();
		l.addAll(hidden);
		return l;
	}
	
}
