package br.com.devpaulo.legendchat.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.devpaulo.legendchat.Main;
import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.types.Channel;

@SuppressWarnings("deprecation")
public class Listeners_old implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	private void onJoin(PlayerJoinEvent e) {
		Legendchat.getPlayerManager().setPlayerFocusedChannel(e.getPlayer(), Legendchat.getDefaultChannel(), false);
		if(hasAnyPermission(e.getPlayer())) {
    		final Player p = e.getPlayer();
    		Bukkit.getServer().getScheduler().runTaskLater(Legendchat.getPlugin(), new Runnable() {
    			public void run() {
    				if(Main.need_update!=null) {
    					p.sendMessage(ChatColor.GOLD+"[Legendchat] "+ChatColor.WHITE+"New update avaible: "+ChatColor.AQUA+"V"+Main.need_update+"!");
    					p.sendMessage(ChatColor.GOLD+"Download: "+ChatColor.WHITE+"http://dev.bukkit.org/bukkit-plugins/legendchat/");
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
		Legendchat.getTemporaryChannelManager().playerDisconnect(e.getPlayer());
		Legendchat.getAfkManager().playerDisconnect(e.getPlayer());
	}
	
	@EventHandler
	private void onKick(PlayerKickEvent e) {
		Legendchat.getPlayerManager().playerDisconnect(e.getPlayer());
		Legendchat.getPrivateMessageManager().playerDisconnect(e.getPlayer());
		Legendchat.getIgnoreManager().playerDisconnect(e.getPlayer());
		Legendchat.getTemporaryChannelManager().playerDisconnect(e.getPlayer());
		Legendchat.getAfkManager().playerDisconnect(e.getPlayer());
	}
	
	private static HashMap<PlayerChatEvent,Boolean> chats = new HashMap<PlayerChatEvent,Boolean>();
	
	public static HashMap<PlayerChatEvent, Boolean> getChats() {
		HashMap<PlayerChatEvent,Boolean> clone = new HashMap<PlayerChatEvent,Boolean>();
		clone.putAll(chats);
		return clone;
	}

	public static void addFakeChat(PlayerChatEvent e, Boolean b) {
		if(!chats.containsKey(e))
			chats.put(e, b);
	}
	
	public static void removeFakeChat(PlayerChatEvent e) {
		if(chats.containsKey(e))
			chats.remove(e);
	}
	
	public static boolean hasFakeChat(PlayerChatEvent e) {
		return chats.containsKey(e);
	}
	
	public static boolean getFakeChat(PlayerChatEvent e) {
		if(chats.containsKey(e))
			return chats.get(e);
		return true;
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
	private void onChat(PlayerChatEvent e) {
		HashMap<String,String> ttt = Legendchat.textToTag();
		if(ttt.size()>0) {
			String new_format = "°1º°";
			int i=2;
			for(String n : ttt.keySet()) {
				new_format+=ttt.get(n)+ChatColor.RESET+"°"+i+"º°";
				i++;
			}
			e.setFormat(e.getFormat()+" "+new_format);
		}
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	private void onChat2(PlayerChatEvent e) {
		if(e.getMessage()!=null&&!chats.containsKey(e)&&!e.isCancelled()) {
			Legendchat.getAfkManager().removeAfk(e.getPlayer());
			if(Legendchat.getPrivateMessageManager().isPlayerTellLocked(e.getPlayer())) {
				Legendchat.getPrivateMessageManager().tellPlayer(e.getPlayer(), null, e.getMessage());
			}
			else {
				if(Legendchat.getPlayerManager().isPlayerFocusedInAnyChannel(e.getPlayer()))
					Legendchat.getPlayerManager().getPlayerFocusedChannel(e.getPlayer()).sendMessage(e.getPlayer(), e.getMessage(), e.getFormat(), e.isCancelled());
				else
					e.getPlayer().sendMessage(Legendchat.getMessageManager().getMessage("error1"));
			}
		}
		else if(chats.containsKey(e)) {
			chats.remove(e);
			chats.put(e, e.isCancelled());
		}
		e.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	private void onChat(PlayerCommandPreprocessEvent e) {
		boolean block = false;
		if(Legendchat.blockShortcutsWhenCancelled())
			if(e.isCancelled())
				block=true;
		if(!block) {
			for(Channel c : Legendchat.getChannelManager().getChannels()) {
				String lowered_msg = e.getMessage().toLowerCase();
				if(c.isShortcutAllowed()) {
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
							c.sendMessage(e.getPlayer(), message);
							e.setCancelled(true);
						}
					}
					if(lowered_msg.startsWith("/"+c.getName().toLowerCase())) {
						if(e.getMessage().length()==("/"+c.getName()).length()) {
							e.getPlayer().sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/"+c.getName().toLowerCase()+" <"+Legendchat.getMessageManager().getMessage("message")+">"));
							e.setCancelled(true);
						}
						else if(lowered_msg.startsWith("/"+c.getName().toLowerCase()+" ")) {
							String message = "";
							String[] split = e.getMessage().split(" ");
							for(int i=1;i<split.length;i++) {
								if(message.length()==0)
									message=split[i];
								else
									message+=" "+split[i];
							}
							c.sendMessage(e.getPlayer(), message);
							e.setCancelled(true);
						}
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
