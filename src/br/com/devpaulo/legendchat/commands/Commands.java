package br.com.devpaulo.legendchat.commands;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import br.com.devpaulo.legendchat.Main;
import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.ChannelManager;
import br.com.devpaulo.legendchat.channels.types.Channel;
import br.com.devpaulo.legendchat.channels.types.PermanentChannel;
import br.com.devpaulo.legendchat.channels.types.TemporaryChannel;
import br.com.devpaulo.legendchat.listeners.Listeners;
import br.com.devpaulo.legendchat.listeners.Listeners_old;
import br.com.devpaulo.legendchat.updater.Updater;

@SuppressWarnings("deprecation")
public class Commands implements CommandExecutor {
	private CommandSender console = Bukkit.getConsoleSender();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("tempchannel")) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(!Legendchat.getConfigManager().getTemporaryChannelConfig().isTemporaryChannelsEnabled())
				return false;
			if(args.length==0)
				sendHelpTempChannel(sender);
			else {
				if(args[0].equalsIgnoreCase("create")) {
					if(!sender.hasPermission("legendchat.tempchannel.manager")&&!sender.hasPermission("legendchat.admin.tempchannel")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error3"));
						return true;
					}
					if(args.length<3) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc create <name> <nickname>"));
						return true;
					}
					String name = args[1];
					String nick = args[2];
					int name_max = Legendchat.getConfigManager().getTemporaryChannelConfig().getMaxChannelNameLength();
					if(name.length()>name_max) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error11").replace("@max", Integer.toString(name_max)));
						return true;
					}
					int nick_max = Legendchat.getConfigManager().getTemporaryChannelConfig().getMaxChannelNicknameLength();
					if(nick.length()>nick_max) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error12").replace("@max", Integer.toString(nick_max)));
						return true;
					}
					if(Legendchat.getConfigManager().getTemporaryChannelConfig().getBlockedNames().contains(name.toLowerCase())) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error22"));
						return true;
					}
					if(Bukkit.getPluginCommand(name.toLowerCase())!=null)
						if(Bukkit.getPluginCommand(name.toLowerCase()).isRegistered()) {
							sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error22"));
							return true;
						}
					
					if(Legendchat.getConfigManager().getTemporaryChannelConfig().getBlockedNames().contains(nick.toLowerCase())) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error23"));
						return true;
					}
					if(Bukkit.getPluginCommand(nick.toLowerCase())!=null)
						if(Bukkit.getPluginCommand(nick.toLowerCase()).isRegistered()) {
							sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error23"));
							return true;
						}
					Set<Permission> perms = Bukkit.getPluginManager().getDefaultPermissions(true);
					if(perms.contains(Bukkit.getPluginManager().getPermission("bukkit.command."+name))) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error22"));
						return true;
					}
					if(perms.contains(Bukkit.getPluginManager().getPermission("bukkit.command."+nick))) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error23"));
						return true;
					}
					if(Legendchat.getChannelManager().getChannelByNameOrNickname(name)!=null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error1"));
						return true;
					}
					if(Legendchat.getChannelManager().getChannelByNameOrNickname(nick)!=null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error2"));
						return true;
					}
					sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_msg2").replace("@channel", name));
					Legendchat.getTemporaryChannelManager().createTempChannel((Player) sender, name, nick);
					return true;
				}
				if(args[0].equalsIgnoreCase("delete")) {
					if(!sender.hasPermission("legendchat.tempchannel.manager")&&!sender.hasPermission("legendchat.admin.tempchannel")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error3"));
						return true;
					}
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannelsAdmin((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc delete <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<2?l.get(0).getName():args[1]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(c.leader_get()!=(Player)sender) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error5"));
						return true;
					}
					String msg = Legendchat.getMessageManager().getMessage("tc_msg1").replace("@channel", c.getName());
					for(Player p : c.getPlayersWhoCanSeeChannel())
						p.sendMessage(msg);
					Legendchat.getChannelManager().deleteChannel(c);
					return true;
				}
				if(args[0].equalsIgnoreCase("color")) {
					if(!sender.hasPermission("legendchat.tempchannel.manager")&&!sender.hasPermission("legendchat.admin.tempchannel")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error3"));
						return true;
					}
					if(!sender.hasPermission("legendchat.tempchannel.color")&&!sender.hasPermission("legendchat.admin.tempchannel")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error3"));
						return true;
					}
					if(args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc color <color-code> [channel]"));
						return true;
					}
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannelsAdmin((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<3) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc color <color-code> <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<3?l.get(0).getName():args[2]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(c.leader_get()!=(Player)sender) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error5"));
						return true;
					}
					if(Legendchat.getConfigManager().getTemporaryChannelConfig().getBlockedColors().contains(args[1].toLowerCase())) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error24"));
						return true;
					}
					boolean changed=false;
					switch(args[1].toLowerCase()) {
						case "0": {c.setColorByString("black");changed=true;break;}
						case "1": {c.setColorByString("darkblue");changed=true;break;}
						case "2": {c.setColorByString("darkgreen");changed=true;break;}
						case "3": {c.setColorByString("darkaqua");changed=true;break;}
						case "4": {c.setColorByString("darkred");changed=true;break;}
						case "5": {c.setColorByString("darkpurple");changed=true;break;}
						case "6": {c.setColorByString("gold");changed=true;break;}
						case "7": {c.setColorByString("gray");changed=true;break;}
						case "8": {c.setColorByString("darkgray");changed=true;break;}
						case "9": {c.setColorByString("blue");changed=true;break;}
						case "a": {c.setColorByString("green");changed=true;break;}
						case "b": {c.setColorByString("aqua");changed=true;break;}
						case "c": {c.setColorByString("red");changed=true;break;}
						case "d": {c.setColorByString("lightpurple");changed=true;break;}
						case "e": {c.setColorByString("yellow");changed=true;break;}
						case "f": {c.setColorByString("white");changed=true;break;}
					}
					if(changed)
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_ch7").replace("@channel", c.getName()));
					else
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error6"));
					return true;
				}
				if(args[0].equalsIgnoreCase("join")) {
					if(!sender.hasPermission("legendchat.tempchannel.user")&&!sender.hasPermission("legendchat.admin.tempchannel")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error3"));
						return true;
					}
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannelsInvites((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc join <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<2?l.get(0).getName():args[1]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(!sender.hasPermission("legendchat.admin.tempchannel")&&!sender.hasPermission("legendchat.admin")) {
						if(!c.invite_list().contains((Player)sender)) {
							sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error7"));
							return true;
						}
						int max_joins_c = Legendchat.getConfigManager().getTemporaryChannelConfig().getMaxJoinsPerChannel();
						if(max_joins_c>0)
							if(c.user_list().size()>=max_joins_c) {
								sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error9"));
								return true;
							}
						int max_joins_p = Legendchat.getConfigManager().getTemporaryChannelConfig().getMaxJoinsPerPlayer();
						if(max_joins_p>0)
							if(Legendchat.getTemporaryChannelManager().getPlayerTempChannels((Player)sender).size()>=max_joins_p) {
								sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error10"));
								return true;
							}
					}
					c.user_add((Player)sender);
					String msg = Legendchat.getMessageManager().getMessage("tc_ch1").replace("@player", sender.getName()).replace("@channel", c.getName());
					for(Player p : c.getPlayersWhoCanSeeChannel())
						p.sendMessage(msg);
					return true;
				}
				if(args[0].equalsIgnoreCase("leave")) {
					if(!sender.hasPermission("legendchat.tempchannel.user")&&!sender.hasPermission("legendchat.admin.tempchannel")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error3"));
						return true;
					}
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannels((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc leave <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<2?l.get(0).getName():args[1]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(!c.user_list().contains((Player)sender)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error8"));
						return true;
					}
					String msg = Legendchat.getMessageManager().getMessage("tc_ch2").replace("@player", sender.getName()).replace("@channel", c.getName());
					for(Player p : c.getPlayersWhoCanSeeChannel())
						p.sendMessage(msg);
					c.user_remove((Player)sender);
					return true;
				}
				if(args[0].equalsIgnoreCase("mod")) {
					if(args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc mod <player> [channel]"));
						return true;
					}
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannelsAdmin((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<3) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc mod <player> <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<3?l.get(0).getName():args[2]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(c.leader_get()!=(Player)sender) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error5"));
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					if(p==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
						return true;
					}
					if(p==(Player)sender) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error9"));
						return true;
					}
					if(c.moderator_list().contains(p)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error13"));
						return true;
					}
					if(!c.user_list().contains(p)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error14"));
						return true;
					}
					int max_mods = Legendchat.getConfigManager().getTemporaryChannelConfig().getMaxModeratorsPerChannel();
					if(max_mods>0)
						if(c.moderator_list().size()>=max_mods) {
							sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error17"));
							return true;
						}
					c.moderator_add(p);
					String msg = Legendchat.getMessageManager().getMessage("tc_ch3").replace("@player", p.getName()).replace("@channel", c.getName());
					for(Player pl : c.getPlayersWhoCanSeeChannel())
						pl.sendMessage(msg);
					return true;
				}
				if(args[0].equalsIgnoreCase("member")) {
					if(args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc member <player> [channel]"));
						return true;
					}
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannelsAdmin((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<3) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc member <player> <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<3?l.get(0).getName():args[2]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(c.leader_get()!=(Player)sender) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error5"));
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					if(p==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
						return true;
					}
					if(p==(Player)sender) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error9"));
						return true;
					}
					if(!c.user_list().contains(p)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error14"));
						return true;
					}
					if(!c.moderator_list().contains(p)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error25"));
						return true;
					}
					c.moderator_remove(p);
					String msg = Legendchat.getMessageManager().getMessage("tc_ch10").replace("@player", p.getName()).replace("@channel", c.getName());
					for(Player pl : c.getPlayersWhoCanSeeChannel())
						pl.sendMessage(msg);
					return true;
				}
				if(args[0].equalsIgnoreCase("mods")) {
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannels((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc mods <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<2?l.get(0).getName():args[1]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(!c.user_list().contains((Player)sender)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error8"));
						return true;
					}
					String mods_list = "";
					for(int i=0;i<c.moderator_list().size();i++) {
						if(i==c.moderator_list().size()-1)
							mods_list+=c.moderator_list().get(i).getName();
						else
							mods_list+=c.moderator_list().get(i).getName()+", ";
					}
					sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_ch8").replace("@mods", (mods_list.length()==0?"...":mods_list)).replace("@channel", c.getName()));
					return true;
				}
				if(args[0].equalsIgnoreCase("members")) {
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannels((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc members <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<2?l.get(0).getName():args[1]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(!c.user_list().contains((Player)sender)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error8"));
						return true;
					}
					String members_list = "";
					for(int i=0;i<c.user_list().size();i++) {
						if(i==c.user_list().size()-1)
							members_list+=c.user_list().get(i).getName();
						else
							members_list+=c.user_list().get(i).getName()+", ";
					}
					sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_ch11").replace("@members", members_list).replace("@channel", c.getName()));
					return true;
				}
				if(args[0].equalsIgnoreCase("leader")) {
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannels((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc leader <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<2?l.get(0).getName():args[1]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(!c.user_list().contains((Player)sender)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error8"));
						return true;
					}
					sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_ch9").replace("@leader", c.leader_get().getName()).replace("@channel", c.getName()));
					return true;
				}
				if(args[0].equalsIgnoreCase("invite")) {
					if(args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc invite <player> [channel]"));
						return true;
					}
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannels((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<3) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc invite <player> <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<3?l.get(0).getName():args[2]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					boolean can_invite=false;
					if(c.leader_get()==(Player)sender)
						can_invite=true;
					else if(Legendchat.getConfigManager().getTemporaryChannelConfig().moderatorsCanInvite()&&c.moderator_list().contains((Player)sender))
						can_invite=true;
					if(!can_invite) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error18"));
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					if(p==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
						return true;
					}
					if(c.user_list().contains(p)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error16"));
						return true;
					}
					if(c.invite_list().contains(p)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error17"));
						return true;
					}
					c.invite_add(p);
					String msg = Legendchat.getMessageManager().getMessage("tc_ch4").replace("@player", p.getName()).replace("@channel", c.getName()).replace("@mod", sender.getName());
					for(Player pl : c.getPlayersWhoCanSeeChannel())
						pl.sendMessage(msg);
					p.sendMessage(Legendchat.getMessageManager().getMessage("tc_msg3").replace("@player", sender.getName()).replace("@channel", c.getName()));
					return true;
				}
				if(args[0].equalsIgnoreCase("kick")) {
					if(args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc kick <player> [channel]"));
						return true;
					}
					List<TemporaryChannel> l = Legendchat.getTemporaryChannelManager().getPlayerTempChannels((Player) sender);
					if(l.size()==0&&args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					if(l.size()>1&&args.length<3) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tc kick <player> <"+Legendchat.getMessageManager().getMessage("channel")+">"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname((args.length<3?l.get(0).getName():args[2]));
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					boolean can_kick=false;
					if(c.leader_get()==(Player)sender)
						can_kick=true;
					else if(Legendchat.getConfigManager().getTemporaryChannelConfig().moderatorsCanKick()&&c.moderator_list().contains((Player)sender))
						can_kick=true;
					if(!can_kick) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error19"));
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					if(p==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
						return true;
					}
					if(!c.user_list().contains(p)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error14"));
						return true;
					}
					if(p==(Player)sender) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error9"));
						return true;
					}
					if(p==c.leader_get()) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error20"));
						return true;
					}
					if(c.moderator_list().contains(p)&&(Player)sender!=c.leader_get()) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error20"));
						return true;
					}
					String msg = Legendchat.getMessageManager().getMessage("tc_ch5").replace("@player", p.getName()).replace("@channel", c.getName()).replace("@mod", sender.getName());
					for(Player pl : c.getPlayersWhoCanSeeChannel())
						pl.sendMessage(msg);
					c.user_remove(p);
					return true;
				}
				if(args[0].equalsIgnoreCase("list")) {
					int page=1;
					if(args.length>1) {
						try {
							page=Integer.parseInt(args[1]);
						}
						catch(Exception e) {
							sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error26"));
							return true;
						}
					}
					if(page<1) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error27"));
						return true;
					}
					List<TemporaryChannel> cs = Legendchat.getTemporaryChannelManager().getAllTempChannels();
					int maxpage=(int) Math.floor(cs.size()/9.0);
					if(maxpage==0)
						maxpage=1;
					if(page>maxpage) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error28").replace("@maxpage", Integer.toString(maxpage)));
						return true;
					}
					sender.sendMessage(Legendchat.getMessageManager().getMessage("tcs_list1").replace("@page", Integer.toString(page)).replace("@maxpage", Integer.toString(maxpage)));
					for(int i=page*9-9;i<page*9-1;i++) {
						if(cs.size()<=i) {
							if(i==0)
								sender.sendMessage(Legendchat.getMessageManager().getMessage("nothing"));
							break;
						}
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tcs_list2").replace("@name", cs.get(i).getName()).replace("@nick", cs.get(i).getNickname()).replace("@leader", cs.get(i).leader_get().getName()));
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("mychannels")) {
					String r1 = Legendchat.getMessageManager().getMessage("tc_r1");
					String r2 = Legendchat.getMessageManager().getMessage("tc_r2");
					String r3 = Legendchat.getMessageManager().getMessage("tc_r3");
					sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_msg5_1"));
					for(TemporaryChannel c : Legendchat.getTemporaryChannelManager().getPlayerTempChannels((Player) sender)) {
						String rank = r3;
						if(c.leader_get()==(Player)sender)
							rank=r1;
						else if(c.moderator_list().contains((Player)sender))
							rank=r2;
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_msg5_2").replace("@name", c.getName()).replace("@nick", c.getNickname()).replace("@rank", rank));
					}
					return true;
				}
				sendHelpTempChannel(sender);
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("mute")) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(args.length==0) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/mute <"+Legendchat.getMessageManager().getMessage("channel")+">"));
				if(Legendchat.getIgnoreManager().playerHasIgnoredChannelsList((Player)sender)) {
					String mlist = "";
					for(Channel c : Legendchat.getIgnoreManager().getPlayerIgnoredChannelsList((Player)sender)) {
						if(mlist.length()==0)
							mlist=c.getName();
						else
							mlist+=", "+c.getName();
					}
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message20").replace("@channels", (mlist.length()==0?"...":mlist)));
				}
				return true;
			}
			Channel c = Legendchat.getChannelManager().getChannelByNameOrNickname(args[0]);
			if(c==null) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error4"));
				return true;
			}
			if(Legendchat.getIgnoreManager().hasPlayerIgnoredChannel((Player)sender, c)) {
				Legendchat.getIgnoreManager().playerUnignoreChannel((Player)sender, c);
				sender.sendMessage(Legendchat.getMessageManager().getMessage("message19").replace("@channel", c.getName()));
			}
			else {
				if(sender.hasPermission("legendchat.channel."+c.getName().toLowerCase()+".blockmute")&&!sender.hasPermission("legendchat.admin")) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("error13"));
					return true;
				}
				if(!c.getPlayersWhoCanSeeChannel().contains((Player)sender)) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("error4"));
					return true;
				}
				Legendchat.getIgnoreManager().playerIgnoreChannel((Player)sender, c);
				sender.sendMessage(Legendchat.getMessageManager().getMessage("message18").replace("@channel", c.getName()));
			}
			return true;
		}
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
			if(sender.hasPermission("legendchat.block.tell")&&!sender.hasPermission("legendchat.admin")) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
				return true;
			}
			if(args.length==0) {
				if(Legendchat.getPrivateMessageManager().isPlayerTellLocked(sender)) {
					Legendchat.getPrivateMessageManager().unlockPlayerTell(sender);
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message11"));
				}
				else
					sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tell <player> ["+Legendchat.getMessageManager().getMessage("message")+"]"));
				return true;
			}
			CommandSender to = Bukkit.getPlayer(args[0]);
			if(to==null) {
				if(args[0].equalsIgnoreCase("console"))
					to=console;
				else {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
					return true;
				}
			}
			if(to==sender) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error9"));
				return true;
			}
			if(args.length==1) {
				if(sender==console) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/tell <player> ["+Legendchat.getMessageManager().getMessage("message")+"]"));
					return true;
				}
				if(Legendchat.getPrivateMessageManager().isPlayerTellLocked(sender)&&Legendchat.getPrivateMessageManager().getPlayerLockedTellWith(sender)==to) {
					Legendchat.getPrivateMessageManager().unlockPlayerTell(sender);
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message11"));
				}
				else {
					if(sender.hasPermission("legendchat.block.locktell")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(to!=console)
						if(Legendchat.getAfkManager().isAfk((Player)to)) {
							sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_1"));
							String mot = Legendchat.getAfkManager().getPlayerAfkMotive((Player)to);
							if(mot!=null)
								if(mot.length()>0)
									sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_2").replace("@motive", mot));
							return true;
						}
					Legendchat.getPrivateMessageManager().lockPlayerTell(sender, to);
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message10").replace("@player", to.getName()));
				}
			}
			else {
				if(to!=console)
					if(Legendchat.getAfkManager().isAfk((Player)to)) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_1"));
						String mot = Legendchat.getAfkManager().getPlayerAfkMotive((Player)to);
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
				Legendchat.getPrivateMessageManager().tellPlayer(sender, to,msg);
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("reply")) {
			if(sender.hasPermission("legendchat.block.reply")&&!sender.hasPermission("legendchat.admin")) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
				return true;
			}
			if(args.length==0) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/r <"+Legendchat.getMessageManager().getMessage("message")+">"));
				return true;
			}
			if(!Legendchat.getPrivateMessageManager().playerHasReply(sender)) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error1"));
				return true;
			}
			CommandSender sendto = Legendchat.getPrivateMessageManager().getPlayerReply(sender);
			if(sendto!=console)
				if(Legendchat.getAfkManager().isAfk((Player)sendto)) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("pm_error2_1"));
					String mot = Legendchat.getAfkManager().getPlayerAfkMotive((Player)sendto);
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
			Legendchat.getPrivateMessageManager().replyPlayer(sender, msg);
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("afk")) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(sender.hasPermission("legendchat.block.afk")&&!sender.hasPermission("legendchat.admin")) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
				return true;
			}
			if(Legendchat.getAfkManager().isAfk((Player)sender)&&args.length==0) {
				Legendchat.getAfkManager().removeAfk((Player)sender);
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
				Legendchat.getAfkManager().setAfk((Player)sender,mot);
				sender.sendMessage(Legendchat.getMessageManager().getMessage("message12"));
				if(mot.length()==0)
					sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/afk ["+Legendchat.getMessageManager().getMessage("reason")+"]"));
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("channel")) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(args.length==0) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/ch <"+Legendchat.getMessageManager().getMessage("channel")+">"));
				String mlist = "";
				for(Channel c : Legendchat.getChannelManager().getChannels()) {
					if(Legendchat.getPlayerManager().canPlayerSeeChannel((Player)sender, c)) {
						if(mlist.length()==0)
							mlist=c.getName();
						else
							mlist+=", "+c.getName();
					}
				}
				sender.sendMessage(Legendchat.getMessageManager().getMessage("message21").replace("@channels", (mlist.length()==0?Legendchat.getMessageManager().getMessage("nothing"):mlist)));
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
				if(c instanceof TemporaryChannel)
					if(!((TemporaryChannel)c).user_list().contains(sender)&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error8"));
						return true;
					}
				Legendchat.getPlayerManager().setPlayerFocusedChannel((Player)sender, c, true);
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
					if(!sender.hasPermission("legendchat.admin.reload")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					Legendchat.load(false);
					Plugin lc = Bukkit.getPluginManager().getPlugin("Legendchat");
					lc.reloadConfig();
					Legendchat.getCensorManager().loadCensoredWords(lc.getConfig().getStringList("censor.censored_words"));
					Legendchat.getChannelManager().loadChannels();
					new Updater().updateAndLoadLanguage(lc.getConfig().getString("language"));
					Main.bungeeActive=false;
					if(lc.getConfig().getBoolean("bungeecord.use"))
						if(Legendchat.getChannelManager().existsChannel(lc.getConfig().getString("bungeecord.channel")))
							Main.bungeeActive=true;
					PlayerJoinEvent.getHandlerList().unregister(lc);
					PlayerQuitEvent.getHandlerList().unregister(lc);
					PlayerKickEvent.getHandlerList().unregister(lc);
					AsyncPlayerChatEvent.getHandlerList().unregister(lc);
					PlayerCommandPreprocessEvent.getHandlerList().unregister(lc);
					try {
						Class.forName("org.bukkit.event.player.PlayerChatEvent");
						PlayerChatEvent.getHandlerList().unregister(lc);
					} catch(ClassNotFoundException e) {}
					if(lc.getConfig().getBoolean("use_async_chat_event",true))
						lc.getServer().getPluginManager().registerEvents(new Listeners(), lc);
					else
						lc.getServer().getPluginManager().registerEvents(new Listeners_old(), lc);
					Legendchat.load(true);
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message2"));
					return true;
				}
				if(args[0].equalsIgnoreCase("channel")) {
					if(!sender.hasPermission("legendchat.admin.channel")&&!sender.hasPermission("legendchat.admin")) {
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
						Legendchat.getChannelManager().createPermanentChannel(new PermanentChannel(WordUtils.capitalizeFully(args[2]),Character.toString(args[2].charAt(0)).toLowerCase(),"{default}","GRAY",true,false,0,true,0,0,false));
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
				if(args[0].equalsIgnoreCase("playerch")) {
					if(!sender.hasPermission("legendchat.admin.playerch")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(args.length<3) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/lc playerch <player> <channel-name>"));
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					if(p==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error8"));
						return true;
					}
					Channel c = null;
					ChannelManager cm = Legendchat.getChannelManager();
					c = cm.getChannelByName(args[2]);
					if(c==null)
						c = cm.getChannelByNickname(args[2]);
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error4"));
						return true;
					}
					Legendchat.getPlayerManager().setPlayerFocusedChannel(p, c, false);
					sender.sendMessage(Legendchat.getMessageManager().getMessage("message16").replace("@player", p.getName()).replace("@channel", c.getName()));
					p.sendMessage(Legendchat.getMessageManager().getMessage("message17").replace("@player", sender.getName()).replace("@channel", c.getName()));
					return true;
				}
				else if(args[0].equalsIgnoreCase("spy")) {
					if(!sender.hasPermission("legendchat.admin.spy")&&!sender.hasPermission("legendchat.admin")) {
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
					if(!sender.hasPermission("legendchat.admin.hide")&&!sender.hasPermission("legendchat.admin")) {
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
					if(!sender.hasPermission("legendchat.admin.mute")&&!sender.hasPermission("legendchat.admin")) {
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
					if(!sender.hasPermission("legendchat.admin.unmute")&&!sender.hasPermission("legendchat.admin")) {
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
					if(!sender.hasPermission("legendchat.admin.muteall")&&!sender.hasPermission("legendchat.admin")) {
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
					if(!sender.hasPermission("legendchat.admin.muteall")&&!sender.hasPermission("legendchat.admin")) {
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
				else if(args[0].equalsIgnoreCase("deltc")) {
					if(!sender.hasPermission("legendchat.admin.tempchannel")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
					if(args.length<2) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("wrongcmd").replace("@command", "/lc deltc <channel>"));
						return true;
					}
					TemporaryChannel c = Legendchat.getTemporaryChannelManager().getTempChannelByNameOrNickname(args[1]);
					if(c==null) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("tc_error4"));
						return true;
					}
					String msg = Legendchat.getMessageManager().getMessage("tc_msg4").replace("@channel", c.getName()).replace("@player", sender.getName());
					sender.sendMessage(msg);
					c.leader_get().sendMessage(msg);
					Legendchat.getTemporaryChannelManager().deleteTempChannel(c);
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
		sender.sendMessage(Legendchat.getMessageManager().getMessage("listcmd1"));
		String msg2 = Legendchat.getMessageManager().getMessage("listcmd2");
		if(sender.hasPermission("legendchat.admin.channel")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/lc channel <create/delete> <channel>").replace("@description", "Channel manager"));
		if(sender.hasPermission("legendchat.admin.playerch")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/lc playerch <player> <channel>").replace("@description", "Change player channel"));
		if(Legendchat.getConfigManager().getTemporaryChannelConfig().isTemporaryChannelsEnabled()) {
			if(sender.hasPermission("legendchat.admin.tempchannel")||sender.hasPermission("legendchat.admin"))
				sender.sendMessage(msg2.replace("@command", "/lc deltc <channel>").replace("@description", "Delete a temp channel"));
		}
		if(sender.hasPermission("legendchat.admin.spy")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/lc spy").replace("@description", "Listen to all channels"));
		if(sender.hasPermission("legendchat.admin.hide")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/lc hide").replace("@description", "Hide from distance channels"));
		if(sender.hasPermission("legendchat.admin.mute")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/lc mute <player> [time {minutes}]").replace("@description", "Mute a player"));
		if(sender.hasPermission("legendchat.admin.unmute")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/lc unmute <player>").replace("@description", "Unmute a player"));
		if(sender.hasPermission("legendchat.admin.muteall")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/lc muteall").replace("@description", "Mute all players"));
		if(sender.hasPermission("legendchat.admin.unmuteall")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/lc unmuteall").replace("@description", "Unmute all players"));
		if(sender.hasPermission("legendchat.admin.reload")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/lc reload").replace("@description", "Configuration and channels reload"));
		sender.sendMessage(Legendchat.getMessageManager().getMessage("listcmd3").replace("@version", Legendchat.getPlugin().getDescription().getVersion()));
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
		if(sender.hasPermission("legendchat.admin"))
			return true;
		return false;
	}
	
	private void sendHelpTempChannel(CommandSender sender) {
		sender.sendMessage(Legendchat.getMessageManager().getMessage("listtc1"));
		String msg2 = Legendchat.getMessageManager().getMessage("listtc2");
		if(sender.hasPermission("legendchat.tempchannel.manager")||sender.hasPermission("legendchat.tempchannel.admin")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/tc create <name> <nick>").replace("@description", "Create a temporary channel"));
		if(sender.hasPermission("legendchat.tempchannel.manager")||sender.hasPermission("legendchat.tempchannel.admin")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/tc delete [channel]").replace("@description", "Delete a temporary channel"));
		if(sender.hasPermission("legendchat.tempchannel.color")||sender.hasPermission("legendchat.tempchannel.admin")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/tc color <color-code> [channel]").replace("@description", "Change channel color"));
		if(sender.hasPermission("legendchat.tempchannel.user")||sender.hasPermission("legendchat.tempchannel.admin")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/tc join [channel]").replace("@description", "Join a temporary channel"));
		if(sender.hasPermission("legendchat.tempchannel.user")||sender.hasPermission("legendchat.tempchannel.admin")||sender.hasPermission("legendchat.admin"))
			sender.sendMessage(msg2.replace("@command", "/tc leave [channel]").replace("@description", "Leave a temporary channel"));
		sender.sendMessage(msg2.replace("@command", "/tc mod <player> [channel]").replace("@description", "Give moderator"));
		sender.sendMessage(msg2.replace("@command", "/tc member <player> [channel]").replace("@description", "Remove moderator"));
		sender.sendMessage(msg2.replace("@command", "/tc leader [channel]").replace("@description", "Show the leader"));
		sender.sendMessage(msg2.replace("@command", "/tc mods [channel]").replace("@description", "List all moderators"));
		sender.sendMessage(msg2.replace("@command", "/tc members [channel]").replace("@description", "List all members"));
		sender.sendMessage(msg2.replace("@command", "/tc list [page]").replace("@description", "List all channels"));
		sender.sendMessage(msg2.replace("@command", "/tc invite <player> [channel]").replace("@description", "Invite to channel"));
		sender.sendMessage(msg2.replace("@command", "/tc kick <player> [channel]").replace("@description", "Kick from channel"));
		sender.sendMessage(msg2.replace("@command", "/tc mychannels").replace("@description", "List your channels"));
		sender.sendMessage(Legendchat.getMessageManager().getMessage("listtc3").replace("@version", Legendchat.getPlugin().getDescription().getVersion()));
	}

}
