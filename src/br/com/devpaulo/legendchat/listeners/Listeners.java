package br.com.devpaulo.legendchat.listeners;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.devpaulo.legendchat.Main;
import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.Channel;

public class Listeners implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	private void onJoin(PlayerJoinEvent e) {
		Legendchat.getPlayerManager().setPlayerChannel(e.getPlayer(), Legendchat.getDefaultChannel(), false);
		if(hasAnyPermission(e.getPlayer())) {
    		final Player p = e.getPlayer();
    		Bukkit.getServer().getScheduler().runTaskLater(Legendchat.getPlugin(), new Runnable() {
    			public void run() {
    				if(Main.need_update!=null) {
    					p.sendMessage(ChatColor.GOLD+"[Legendchat] "+ChatColor.WHITE+"New update avaible: "+ChatColor.AQUA+"V"+Main.need_update+"!");
    					p.sendMessage(ChatColor.GOLD+"Download: "+ChatColor.WHITE+"http://dev.bukkit.org/server-mods/vipzero/");
    				}
    			}
    		}, 60L);
    	}
	}
	
	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		Legendchat.getPlayerManager().playerDisconnect(e.getPlayer());
		Legendchat.getPrivateMessageManager().playerDisconnect(e.getPlayer());
		Legendchat.getIgnoreManager().playerDisconnect(e.getPlayer());
	}
	
	@EventHandler
	private void onKick(PlayerKickEvent e) {
		Legendchat.getPlayerManager().playerDisconnect(e.getPlayer());
		Legendchat.getPrivateMessageManager().playerDisconnect(e.getPlayer());
		Legendchat.getIgnoreManager().playerDisconnect(e.getPlayer());
	}
	
	private HashMap<AsyncPlayerChatEvent,Boolean> chats = new HashMap<AsyncPlayerChatEvent,Boolean>();
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onChat(AsyncPlayerChatEvent e) {
		if(e.getMessage()!=null&&!chats.containsKey(e)&&!e.isCancelled()) {
			Legendchat.getPrivateMessageManager().removeAfk(e.getPlayer());
			if(Legendchat.getPrivateMessageManager().isPlayerTellLocked(e.getPlayer())) {
				Legendchat.getPrivateMessageManager().tellPlayer(e.getPlayer(), null, e.getMessage());
			}
			else {
				if(Legendchat.getPlayerManager().isPlayerInAnyChannel(e.getPlayer()))
					Legendchat.getPlayerManager().getPlayerChannel(e.getPlayer()).sendMessage(e.getPlayer(), e.getMessage(), e.getFormat(), e.isCancelled());
				else
					e.getPlayer().sendMessage(Legendchat.getMessageManager().getMessage("error1"));
			}
		}
		else if(e.getMessage()!=null) {
			chats.remove(e);
			chats.put(e, e.isCancelled());
		}
		e.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onChat(PlayerCommandPreprocessEvent e) {
		boolean block = false;
		if(Legendchat.blockShortcutsWhenCancelled())
			if(e.isCancelled())
				block=true;
		if(!block) {
			for(Channel c : Legendchat.getChannelManager().getChannels()) {
				String lowered_msg = e.getMessage().toLowerCase();
				if(c.isShortcutAllowed())
					if(lowered_msg.startsWith("/"+c.getNickname().toLowerCase())) {
						if(e.getMessage().length()==("/"+c.getNickname()).length()) {
							e.getPlayer().sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/"+c.getNickname().toLowerCase()+" <"+Legendchat.getMessageManager().getMessage("message")+">"));
							e.setCancelled(true);
						}
						else if(lowered_msg.startsWith("/"+c.getNickname().toLowerCase()+" ")) {
							String message = "";
							String[] split = e.getMessage().split(" ");
							for(int i=1;i<split.length;i++) {
								if(message.length()==0)
									message=split[i];
								else
									message+=" "+split[i];
							}
							HashSet<Player> p = new HashSet<Player>();
							p.add(e.getPlayer());
							AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, e.getPlayer(), "legendchat", p);
							chats.put(event, false);
							Bukkit.getPluginManager().callEvent(event);
							c.sendMessage(e.getPlayer(), message, event.getFormat(), chats.get(event));
							chats.remove(event);
							e.setCancelled(true);
						}
					}
			}
		}
	}
	
	private boolean hasAnyPermission(Player sender) {
		if(sender.hasPermission("legendchat.admin.channel"))
			return true;
		if(sender.hasPermission("legendchat.admin.spy"))
			return true;
		if(sender.hasPermission("legendchat.admin.hide"))
			return true;
		if(sender.hasPermission("legendchat.admin.mute"))
			return true;
		if(sender.hasPermission("legendchat.admin.unmute"))
			return true;
		if(sender.hasPermission("legendchat.admin.muteall"))
			return true;
		if(sender.hasPermission("legendchat.admin.unmuteall"))
			return true;
		if(sender.hasPermission("legendchat.admin.reload"))
			return true;
		return false;
	}
}
