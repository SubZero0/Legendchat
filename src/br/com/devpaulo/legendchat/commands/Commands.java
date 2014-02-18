package br.com.devpaulo.legendchat.commands;

import java.io.File;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import br.com.devpaulo.legendchat.Main;
import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.Channel;
import br.com.devpaulo.legendchat.channels.ChannelManager;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ignore")) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(args.length==0) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/ignore <player>"));
				return true;
			}
			Player p = Bukkit.getPlayer(args[0]);
			if(p==null) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
				return true;
			}
			if(p==(Player)sender) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error9"));
				return true;
			}
			if(Legendchat.getIgnoreManager().hasPlayerIgnoredPlayer((Player)sender, p.getName())) {
				Legendchat.getIgnoreManager().playerUnignorePlayer((Player)sender, p.getName());
				sender.sendMessage(Legendchat.getMessageManager().getMessage("message15").replace("@player", p.getName()));
			}
			else {
				if(p.hasPermission("legendchat.block.ignore")) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("error10"));
					return true;
				}
				Legendchat.getIgnoreManager().playerIgnorePlayer((Player)sender, p.getName());
				sender.sendMessage(Legendchat.getMessageManager().getMessage("message14").replace("@player", p.getName()));
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("tell")) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(sender.hasPermission("legendchat.block.tell")&&!sender.hasPermission("legendchat.admin")) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
				return true;
			}
			if(args.length==0) {
				if(Legendchat.getPrivateMessageManager().isPlayerTellLocked((Player)sender)) {
					Legendchat.getPrivateMessageManager().unlockPlayerTell((Player)sender);
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message11"));
				}
				else
					sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tell <player> ["+Legendchat.getMessageManager().getMessage("message")+"]"));
				return true;
			}
			Player to = Bukkit.getPlayer(args[0]);
			if(to==null) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
				return true;
			}
			if(to==(Player)sender) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error9"));
				return true;
			}
			if(args.length==1) {
				if(Legendchat.getPrivateMessageManager().isPlayerTellLocked((Player)sender)&&Legendchat.getPrivateMessageManager().getPlayerLockedTellWith((Player)sender)==to) {
					Legendchat.getPrivateMessageManager().unlockPlayerTell((Player)sender);
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message11"));
				}
				else {
					if(sender.hasPermission("legendchat.block.locktell")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(Legendchat.getPrivateMessageManager().isAfk(to)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_1"));
						String mot = Legendchat.getPrivateMessageManager().getPlayerAfkMotive(to);
						if(mot!=null)
							if(mot.length()>0)
								sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_2").replace("@motive", mot));
						return true;
					}
					Legendchat.getPrivateMessageManager().lockPlayerTell((Player)sender, to);
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message10").replace("@player", to.getName()));
				}
			}
			else {
				if(Legendchat.getPrivateMessageManager().isAfk(to)) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_1"));
					String mot = Legendchat.getPrivateMessageManager().getPlayerAfkMotive(to);
					if(mot!=null)
						if(mot.length()>0)
							sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_2").replace("@motive", mot));
					return true;
				}
				String msg = "";
				for(int i=1;i<args.length;i++) {
					if(msg.length()==0)
						msg=args[i];
					else
						msg+=" "+args[i];
				}
				Legendchat.getPrivateMessageManager().tellPlayer((Player)sender, to,msg);
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("reply")) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(sender.hasPermission("legendchat.block.reply")&&!sender.hasPermission("legendchat.admin")) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
				return true;
			}
			if(args.length==0) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "(/r)eply <"+Legendchat.getMessageManager().getMessage("message")+">"));
				return true;
			}
			if(!Legendchat.getPrivateMessageManager().playerHasReply((Player)sender)) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error1"));
				return true;
			}
			Player sendto = Legendchat.getPrivateMessageManager().getPlayerReply((Player)sender);
			if(Legendchat.getPrivateMessageManager().isAfk(sendto)) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_1"));
				String mot = Legendchat.getPrivateMessageManager().getPlayerAfkMotive(sendto);
				if(mot!=null)
					if(mot.length()>0)
						sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_2").replace("@motive", mot));
				return true;
			}
			String msg = "";
			for(int i=0;i<args.length;i++) {
				if(msg.length()==0)
					msg=args[i];
				else
					msg+=" "+args[i];
			}
			Legendchat.getPrivateMessageManager().replyPlayer((Player)sender, msg);
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("afk")) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(sender.hasPermission("legendchat.block.afk")&&!sender.hasPermission("legendchat.admin")) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
				return true;
			}
			if(Legendchat.getPrivateMessageManager().isAfk((Player)sender)&&args.length==0) {
				Legendchat.getPrivateMessageManager().removeAfk((Player)sender);
				sender.sendMessage(Legendchat.getMessageManager().getMessage("message13"));
			}
			else {
				String mot = "";
				if(args.length>0)
					for(int i=0;i<args.length;i++) {
						if(mot.length()==0)
							mot=args[i];
						else
							mot=" "+args[i];
					}
				if(mot.length()>0)
					if(sender.hasPermission("legendchat.block.afkmotive")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
				Legendchat.getPrivateMessageManager().setAfk((Player)sender,mot);
				sender.sendMessage(Legendchat.getMessageManager().getMessage("message12"));
				if(mot.length()==0)
					sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/afk ["+Legendchat.getMessageManager().getMessage("motive")+"]"));
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("channel")) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(args.length==0) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "(/ch)annel <"+Legendchat.getMessageManager().getMessage("channel")+">"));
			}
			else {
				Channel c = null;
				ChannelManager cm = Legendchat.getChannelManager();
				c=cm.getChannelByName(args[0].toLowerCase());
				if(c==null)
					c=cm.getChannelByNickname(args[0].toLowerCase());
				if(c==null) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("error4"));
					return true;
				}
				Legendchat.getPlayerManager().setPlayerChannel((Player)sender, c, true);
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("legendchat")) {
			if(args.length==0) {
				if(!hasAnyPermission(sender)) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
					return true;
				}
				sendHelp(sender);
			}
			else {
				if(args[0].equalsIgnoreCase("reload")) {
					if(!sender.hasPermission("legendchat.admin.reload")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					Plugin lc = Bukkit.getPluginManager().getPlugin("Legendchat");
					lc.reloadConfig();
					Legendchat.getChannelManager().loadChannels();
					Legendchat.getMessageManager().loadMessages(new File(lc.getDataFolder(),"language_"+lc.getConfig().getString("language")+".yml"));
					Main.bungeeActive=false;
					if(lc.getConfig().getBoolean("bungeecord.use"))
						if(Legendchat.getChannelManager().existsChannel(lc.getConfig().getString("bungeecord.channel")))
							Main.bungeeActive=true;
					Legendchat.load();
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message2"));
					return true;
				}
				if(args[0].equalsIgnoreCase("channel")) {
					if(!sender.hasPermission("legendchat.admin.channel")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(args.length<3) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/lc channel <create/delete> <channel-name>"));
						return true;
					}
					if(args[1].equalsIgnoreCase("create")) {
						Channel c = Legendchat.getChannelManager().getChannelByName(args[2].toLowerCase());
						if(c!=null) {
							sender.sendMessage(Legendchat.getMessageManager().getMessage("error7"));
							return true;
						}
						sender.sendMessage(Legendchat.getMessageManager().getMessage("message3").replace("@channel", args[2]));
						Legendchat.getChannelManager().createChannel(new Channel(WordUtils.capitalizeFully(args[2]),Character.toString(args[2].charAt(0)).toLowerCase(),"{default}","GRAY",true,0,true,0,0,false));
					}
					else if(args[1].equalsIgnoreCase("delete")) {
						Channel c = Legendchat.getChannelManager().getChannelByName(args[2].toLowerCase());
						if(c==null) {
							sender.sendMessage(Legendchat.getMessageManager().getMessage("error4"));
							return true;
						}
						sender.sendMessage(Legendchat.getMessageManager().getMessage("message4").replace("@channel", c.getName()));
						Legendchat.getChannelManager().deleteChannel(c);
					}
					else {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/lc channel <create/delete> <channel-name>"));
					}
					return true;
				}
				else if(args[0].equalsIgnoreCase("spy")) {
					if(!sender.hasPermission("legendchat.admin.spy")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(sender==Bukkit.getConsoleSender())
						return false;
					Player player = (Player)sender;
					boolean spy = Legendchat.getPlayerManager().isSpy(player);
					if(!spy) {
						Legendchat.getPlayerManager().addSpy(player);
						sender.sendMessage(Legendchat.getMessageManager().getMessage("message5"));
					}
					else {
						Legendchat.getPlayerManager().removeSpy(player);
						sender.sendMessage(Legendchat.getMessageManager().getMessage("message6"));
					}
					return true;
				}
				else if(args[0].equalsIgnoreCase("hide")) {
					if(!sender.hasPermission("legendchat.admin.hide")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(sender==Bukkit.getConsoleSender())
						return false;
					Player player = (Player)sender;
					boolean hidden = Legendchat.getPlayerManager().isPlayerHiddenFromRecipients(player);
					if(!hidden) {
						Legendchat.getPlayerManager().hidePlayerFromRecipients(player);
						sender.sendMessage(Legendchat.getMessageManager().getMessage("message7"));
					}
					else {
						Legendchat.getPlayerManager().showPlayerToRecipients(player);
						sender.sendMessage(Legendchat.getMessageManager().getMessage("message8"));
					}
					return true;
				}
				else if(args[0].equalsIgnoreCase("mute")) {
					if(!sender.hasPermission("legendchat.admin.mute")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/lc mute <player> [time {minutes}]"));
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					if(p==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
						return true;
					}
					int time = 0;
					if(args.length>2) {
						try {
							time=Integer.parseInt(args[2]);
						}
						catch(Exception e) {
							sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_error1"));
							return true;
						}
					}
					if(time<0) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_error1"));
						return true;
					}
					if(Legendchat.getMuteManager().isPlayerMuted(p.getName())) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_error2"));
						return true;
					}
					Legendchat.getMuteManager().mutePlayer(p.getName(),time);
					if(time!=0) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_msg3").replace("@player", p.getName()).replace("@time", Integer.toString(time)));
						p.sendMessage(Legendchat.getMessageManager().getMessage("mute_msg4").replace("@player", sender.getName()).replace("@time", Integer.toString(time)));
					}
					else {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_msg1").replace("@player", p.getName()));
						p.sendMessage(Legendchat.getMessageManager().getMessage("mute_msg2").replace("@player", sender.getName()));
					}
					return true;
				}
				else if(args[0].equalsIgnoreCase("unmute")) {
					if(!sender.hasPermission("legendchat.admin.unmute")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/lc unmute <player>"));
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					if(p==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
						return true;
					}
					if(!Legendchat.getMuteManager().isPlayerMuted(p.getName())) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_error3"));
						return true;
					}
					Legendchat.getMuteManager().unmutePlayer(p.getName());
					sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_msg5").replace("@player", p.getName()));
					p.sendMessage(Legendchat.getMessageManager().getMessage("mute_msg6").replace("@player", sender.getName()));
					return true;
				}
				else if(args[0].equalsIgnoreCase("muteall")) {
					if(!sender.hasPermission("legendchat.admin.muteall")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(Legendchat.getMuteManager().isServerMuted()) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_error6"));
						return true;
					}
					Legendchat.getMuteManager().muteServer();
					Bukkit.broadcastMessage(Legendchat.getMessageManager().getMessage("mute_msg7").replace("@player", sender.getName()));
					return true;
				}
				else if(args[0].equalsIgnoreCase("unmuteall")) {
					if(!sender.hasPermission("legendchat.admin.muteall")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(!Legendchat.getMuteManager().isServerMuted()) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_error7"));
						return true;
					}
					Legendchat.getMuteManager().unmuteServer();
					Bukkit.broadcastMessage(Legendchat.getMessageManager().getMessage("mute_msg8").replace("@player", sender.getName()));
					return true;
				}
				if(!hasAnyPermission(sender)) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
					return true;
				}
				sendHelp(sender);
			}
			return true;
		}
		return false;
	}
	
	private void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD+"============== Legendchat - Command list ==============");
		if(sender.hasPermission("legendchat.admin.channel"))
			sender.sendMessage(ChatColor.YELLOW+"/lc channel <create/delete> <channel>"+ChatColor.WHITE+" - Channel manager.");
		if(sender.hasPermission("legendchat.admin.spy"))
			sender.sendMessage(ChatColor.YELLOW+"/lc spy"+ChatColor.WHITE+" - Listen to all channels.");
		if(sender.hasPermission("legendchat.admin.hide"))
			sender.sendMessage(ChatColor.YELLOW+"/lc hide"+ChatColor.WHITE+" - Hide from distance channels.");
		if(sender.hasPermission("legendchat.admin.mute"))
			sender.sendMessage(ChatColor.YELLOW+"/lc mute <player> [time {minutes}]"+ChatColor.WHITE+" - Mute a player.");
		if(sender.hasPermission("legendchat.admin.unmute"))
			sender.sendMessage(ChatColor.YELLOW+"/lc unmute <player>"+ChatColor.WHITE+" - Unmute a player.");
		if(sender.hasPermission("legendchat.admin.muteall"))
			sender.sendMessage(ChatColor.YELLOW+"/lc muteall"+ChatColor.WHITE+" - Mute all players.");
		if(sender.hasPermission("legendchat.admin.unmuteall"))
			sender.sendMessage(ChatColor.YELLOW+"/lc unmuteall"+ChatColor.WHITE+" - Unmute all players.");
		if(sender.hasPermission("legendchat.admin.reload"))
			sender.sendMessage(ChatColor.YELLOW+"/lc reload"+ChatColor.WHITE+" - Configuration and channels reload.");
		sender.sendMessage(ChatColor.GOLD+"=================== Version V"+Bukkit.getPluginManager().getPlugin("Legendchat").getDescription().getVersion()+" ===================");
	}
	
	private boolean hasAnyPermission(CommandSender sender) {
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
