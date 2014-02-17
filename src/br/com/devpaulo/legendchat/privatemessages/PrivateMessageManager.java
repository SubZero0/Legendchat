package br.com.devpaulo.legendchat.privatemessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.api.events.PrivateMessageEvent;

public class PrivateMessageManager {
	private static HashMap<Player,Player> telling = new HashMap<Player,Player>();
	private static HashMap<Player,Player> reply = new HashMap<Player,Player>();
	private static HashMap<Player,String> afk = new HashMap<Player,String>();
	
	public void tellPlayer(Player from, Player to, String msg) {
		if(to==null) {
			if(!isPlayerTellLocked(from))
				return;
			to=telling.get(from);
		}
		removeAfk(from);
		if(isAfk(to)) {
			from.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_1"));
			String mot = getPlayerAfkMotive(to);
			if(mot!=null)
				from.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_2").replace("@motive", mot));
			return;
		}
		PrivateMessageEvent e = new PrivateMessageEvent(from,to,msg);
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCancelled())
			return;
		from=e.getSender();
		to=e.getReceiver();
		msg=e.getMessage();
		
		boolean ignored = false;
		if(Legendchat.getIgnoreManager().hasPlayerIgnoredPlayer(to, from.getName()))
			ignored=true;
		
		if(!ignored)
			setPlayerReply(to,from);
		
		from.sendMessage(ChatColor.translateAlternateColorCodes('&', Legendchat.getPrivateMessageFormat("send")).replace("{sender}", from.getName()).replace("{receiver}", to.getName()).replace("{msg}", msg));
		if(!ignored)
			to.sendMessage(ChatColor.translateAlternateColorCodes('&', Legendchat.getPrivateMessageFormat("receive")).replace("{sender}", from.getName()).replace("{receiver}", to.getName()).replace("{msg}", msg));
		
		for(Player p : Legendchat.getPlayerManager().getSpys())
			if((p!=from&&p!=to)||(ignored&&p==to))
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', Legendchat.getPrivateMessageFormat("spy").replace("{sender}", from.getName()).replace("{receiver}", to.getName()).replace("{ignored}", (ignored?Legendchat.getMessageManager().getMessage("ignored"):""))).replace("{msg}", msg));
	}
	
	public void replyPlayer(Player from, String msg) {
		if(!playerHasReply(from)) {
			from.sendMessage(Legendchat.getMessageManager().getMessage("pm_error1"));
			return;
		}
		tellPlayer(from,getPlayerReply(from),msg);
	}
	
	public void lockPlayerTell(Player from, Player to) {
		unlockPlayerTell(from);
		telling.put(from,to);
	}
	
	public void unlockPlayerTell(Player p) {
		if(isPlayerTellLocked(p))
			telling.remove(p);
	}
	
	public boolean isPlayerTellLocked(Player p) {
		return telling.containsKey(p);
	}
	
	public Player getPlayerLockedTellWith(Player p) {
		if(isPlayerTellLocked(p))
			return telling.get(p);
		return null;
	}
	
	public List<Player> getAllTellLockedPlayers() {
		List<Player> l = new ArrayList<Player>();
		l.addAll(telling.keySet());
		return l;
	}
	
	public void setPlayerReply(Player to, Player from) {
		if(playerHasReply(to))
			reply.remove(to);
		reply.put(to,from);
	}
	
	public Player getPlayerReply(Player p) {
		if(!playerHasReply(p))
			return null;
		return reply.get(p);
	}
	
	public boolean playerHasReply(Player p) {
		return reply.containsKey(p);
	}
	
	public List<Player> getAllPlayersWithReply() {
		List<Player> l = new ArrayList<Player>();
		l.addAll(reply.keySet());
		return l;
	}
	
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
		unlockPlayerTell(p);
		if(reply.containsKey(p))
			reply.remove(p);
		List<Player> lista = new ArrayList<Player>();
		for(Player p2 : getAllTellLockedPlayers())
			if(telling.get(p2)==p)
				lista.add(p2);
		for(Player p3: lista)
			telling.remove(p3);
		lista.clear();
		for(Player p2 : getAllPlayersWithReply())
			if(reply.get(p2)==p)
				lista.add(p2);
		for(Player p3: lista)
			reply.remove(p3);
	}
}
