package br.com.devpaulo.legendchat.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.types.Channel;

public class PlayerManager {
	private HashMap<Player,Channel> players = new HashMap<Player,Channel>();
	private List<Player> spys = new ArrayList<Player>();
	private List<Player> hidden = new ArrayList<Player>();
	public PlayerManager() {
	}
	
	public void playerDisconnect(Player p) {
		setPlayerFocusedChannel(p,null,false);
		removeSpy(p);
		showPlayerToRecipients(p);
	}
	
	public void setPlayerFocusedChannel(Player p, Channel c, boolean msg) {
		if(Legendchat.getDefaultChannel()!=c&&c!=null)
			if(!p.hasPermission("legendchat.channel."+c.getName()+".focus")&&!p.hasPermission("legendchat.admin")) {
				if(msg)
					p.sendMessage(Legendchat.getMessageManager().getMessage("error5"));
				return;
			}
		if(isPlayerFocusedInAnyChannel(p))
			players.remove(p);
		if(c!=null)
			players.put(p, c);
		if(msg)
			p.sendMessage(Legendchat.getMessageManager().getMessage("message1").replace("@channel", c.getName()));
	}
	
	public Channel getPlayerFocusedChannel(Player p) {
		if(isPlayerFocusedInAnyChannel(p))
			return players.get(p);
		return null;
	}
	
	public List<Player> getPlayersFocusedInChannel(Channel c) {
		List<Player> l = new ArrayList<Player>();
		for(Player p : players.keySet())
			if(players.get(p)==c)
				l.add(p);
		return l;
	}
	
	public List<Player> getPlayersWhoCanSeeChannel(Channel c) {
		List<Player> l = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers())
			if(p.hasPermission("legendchat.channel."+c.getName().toLowerCase()+".chat"))
				l.add(p);
		return l;
	}
	
	public boolean isPlayerFocusedInAnyChannel(Player p) {
		return players.containsKey(p);
	}
	
	public boolean canPlayerSeeChannel(Player p, Channel c) {
		return p.hasPermission("legendchat.channel."+c.getName().toLowerCase()+".chat");
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
