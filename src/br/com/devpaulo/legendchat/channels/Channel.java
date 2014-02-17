package br.com.devpaulo.legendchat.channels;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.Main;
import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.api.events.BungeecordChatMessageEvent;
import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import br.com.devpaulo.legendchat.utils.Utils;

public class Channel {
	private String name = "";
	private String nick = "";
	private String format = "";
	private String color = "";
	private String color2 = "";
	private boolean shortcut = false;
	private double distance = 0;
	private boolean crossworlds = false;
	private double cost = 0;
	private boolean show_cost_msg = false;
	private int delay = 0;
	public Channel(String name, String nick, String format, String color, boolean shortcut, double distance, boolean crossworlds, int delay, double cost,boolean show_cost_msg) {
		this.name=name;
		this.nick=nick;
		this.format=format;
		this.color=translateStringColor(color);
		color2=color;
		this.shortcut=shortcut;
		this.distance=distance;
		this.crossworlds=crossworlds;
		this.cost=cost;
		this.show_cost_msg=show_cost_msg;
		this.delay=delay;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNickname() {
		return nick;
	}
	
	public String getFormat() {
		return format;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getStringColor() {
		return color2;
	}
	
	public boolean isShortcutAllowed() {
		return shortcut;
	}
	
	public boolean isCrossworlds() {
		return crossworlds;
	}
	
	public double getMaxDistance() {
		return distance;
	}
	
	public double getMessageCost() {
		return cost;
	}
	public double getCostPerMessage() {
		return cost;
	}
	
	public boolean showCostMessage() {
		return show_cost_msg;
	}
	
	public int getDelayPerMessage() {
		return delay;
	}
	
	public List<Player> getPlayersInChannel() {
		return Legendchat.getPlayerManager().getPlayersInChannel(this);
	}
	
	public void sendMessage(Player sender, String message, String bukkit_format, boolean cancelled) {
		if(!sender.hasPermission("legendchat.channel."+getName().toLowerCase()+".chat")&&!sender.hasPermission("legendchat.admin")) {
			sender.sendMessage(Legendchat.getMessageManager().getMessage("error2"));
			return;
		}
		if(sender.hasPermission("legendchat.channel."+getName().toLowerCase()+".blockwrite")&&!sender.hasPermission("legendchat.admin")) {
			sender.sendMessage(Legendchat.getMessageManager().getMessage("error2"));
			return;
		}
		int delay = Legendchat.getDelayManager().getPlayerDelayFromChannel(sender.getName(), this);
		if(delay>0) {
			sender.sendMessage(Legendchat.getMessageManager().getMessage("error11").replace("@time", Integer.toString(delay)));
			return;
		}
		if(Legendchat.getMuteManager().isPlayerMuted(sender.getName())) {
			int time = Legendchat.getMuteManager().getPlayerMuteTimeLeft(sender.getName());
			if(time==0)
				sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_error4"));
			else
				sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_error5").replace("@time", Integer.toString(time)));
			return;
		}
		if(Legendchat.getMuteManager().isServerMuted()) {
			sender.sendMessage(Legendchat.getMessageManager().getMessage("mute_error8"));
			return;
		}
		Set<Player> recipients = new HashSet<Player>();
		for(Player p : Bukkit.getOnlinePlayers())
			if(p.hasPermission("legendchat.channel."+getName().toLowerCase()+".chat")||p.hasPermission("legendchat.admin"))
				recipients.add(p);
		Set<Player> recipients2 = new HashSet<Player>();
		recipients2.addAll(recipients);
		if(getMaxDistance()!=0) {
			for(Player p : recipients2) {
				if(sender.getWorld()!=p.getWorld())
					recipients.remove(p);
				else if(sender.getLocation().distance(p.getLocation())>getMaxDistance())
					recipients.remove(p);
			}
			recipients2.clear();
		}
		else {
			recipients2.addAll(recipients);
			if(!isCrossworlds())
				for(Player p : recipients2)
					if(sender.getWorld()!=p.getWorld())
						recipients.remove(p);
			recipients2.clear();
		}
		recipients2.addAll(recipients);
		for(Player p : recipients2)
			if(Legendchat.getIgnoreManager().hasPlayerIgnoredPlayer(p, sender.getName()))
				recipients.remove(p);
		recipients2.clear();
		boolean gastou = false;
		if(!Main.block_econ&&getMessageCost()>0) {
			if(!sender.hasPermission("legendchat.channel."+getName().toLowerCase()+".free")&&!sender.hasPermission("legendchat.admin")) {
				if(Main.econ.getBalance(sender.getName())<getMessageCost()) {
					sender.sendMessage(Legendchat.getMessageManager().getMessage("error3").replace("@price", Double.toString(getMessageCost())));
					return;
				}
				Main.econ.withdrawPlayer(sender.getName(), getMessageCost());
				gastou=true;
			}
		}
		String n_format_p_p = "";
		String n_format_p = "";
		String n_format_s = "";
		if(bukkit_format.contains("<")&&bukkit_format.contains(">")&&bukkit_format.contains("%1$s")) {
			n_format_p_p = bukkit_format.split("<")[0];
			String[] n_format = bukkit_format.split("<")[1].split(">")[0].split("%1$s");
			if(n_format.length>0)
				n_format_p = n_format[0].replace("%1$s", "");
			if(n_format.length>1)
				n_format_s = n_format[1];
		}
		HashMap<String,String> tags = new HashMap<String,String>();
		tags.put("name", getName());
		tags.put("nick", getNickname());
		tags.put("color", getColor());
		tags.put("sender", sender.getDisplayName());
		tags.put("plainsender", sender.getName());
		tags.put("world", sender.getWorld().getName());
		tags.put("bprefix", (Legendchat.forceRemoveDoubleSpacesFromBukkit()?Utils.removeDoubleSpaces(n_format_p_p):n_format_p_p));
		tags.put("bprefix2", (Legendchat.forceRemoveDoubleSpacesFromBukkit()?Utils.removeDoubleSpaces(n_format_p):n_format_p));
		tags.put("bsuffix", (Legendchat.forceRemoveDoubleSpacesFromBukkit()?Utils.removeDoubleSpaces(n_format_s):n_format_s));
		tags.put("server", Legendchat.getMessageManager().getMessage("bungeecord_server"));
		if(!Main.block_chat) {
			tags.put("prefix", Main.chat.getPlayerPrefix(sender));
			tags.put("suffix", Main.chat.getPlayerSuffix(sender));
			tags.put("groupprefix", Main.chat.getGroupPrefix(sender.getWorld(), Main.chat.getPrimaryGroup(sender)));
			tags.put("groupsuffix", Main.chat.getGroupSuffix(sender.getWorld(), Main.chat.getPrimaryGroup(sender)));
		}
		ChatMessageEvent e = new ChatMessageEvent(this,sender,message,Legendchat.format(getFormat()),getFormat(),recipients,tags,cancelled);
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCancelled())
			return;
		sender = e.getSender();
		message = e.getMessage();
		String completa = e.getFormat();
		if(Legendchat.blockRepeatedTags()) {
			if(completa.contains("prefix")&&completa.contains("groupprefix"))
				if(e.getTagValue("prefix").equals(e.getTagValue("groupprefix")))
					e.setTagValue("prefix", "");
			if(completa.contains("suffix")&&completa.contains("groupsuffix"))
				if(e.getTagValue("suffix").equals(e.getTagValue("groupsuffix")))
					e.setTagValue("suffix", "");
		}
		for(String n : e.getTags())
			completa = completa.replace("{"+n+"}", ChatColor.translateAlternateColorCodes('&', e.getTagValue(n)));
		completa = completa.replace("{msg}", Utils.translateAlternateChatColorsWithPermission(sender, message));
		
		for(Player p : e.getRecipients())
			p.sendMessage(completa);
		
		if(getDelayPerMessage()>0&&!sender.hasPermission("legendchat.channel."+getName().toLowerCase()+".nodelay")&&!sender.hasPermission("legendchat.admin"))
			Legendchat.getDelayManager().addPlayerDelay(sender.getName(), this);
		
		if(getMaxDistance()!=0)
			if(Legendchat.showNoOneHearsYou()) {
				boolean show = false;
				if(e.getRecipients().size()==0)
					show=true;
				else if(e.getRecipients().size()==1&&e.getRecipients().contains(sender))
					show=true;
				else {
					show=true;
					for(Player p : e.getRecipients())
						if(p!=sender&&!Legendchat.getPlayerManager().isPlayerHiddenFromRecipients(p)) {
							show=false;
							break;
						}
				}
				if(show)
					sender.sendMessage(Legendchat.getMessageManager().getMessage("special"));
			}
		
		for(Player p : Legendchat.getPlayerManager().getSpys())
			if(!e.getRecipients().contains(p))
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', Legendchat.getFormat("spy").replace("{msg}", ChatColor.stripColor(completa))));
		
		if(gastou)
			if(showCostMessage())
				sender.sendMessage(Legendchat.getMessageManager().getMessage("message9").replace("@money", Double.toString(getCostPerMessage())));
		
		if(Legendchat.logToBukkit())
			Bukkit.getConsoleSender().sendMessage(completa);
		
		if(Legendchat.isBungeecordActive()) {
			if(Legendchat.getBungeecordChannel()==this) {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				try {
					HashMap<String,String> tags_packet = new HashMap<String,String>();
					for(String tag_packet : e.getTags())
						tags_packet.put(tag_packet,e.getTagValue(tag_packet));
					out.writeUTF(tags_packet.toString());
					out.writeUTF(Utils.translateAlternateChatColorsWithPermission(sender, message));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				sender.sendPluginMessage(Bukkit.getPluginManager().getPlugin("Legendchat"), "Legendchat", b.toByteArray());
			}
		}
	}
	
	public void sendBungeecordMessage(HashMap<String,String> tags, String message) {
		Set<Player> recipients = new HashSet<Player>();
		for(Player p : Bukkit.getOnlinePlayers())
			if(p.hasPermission("legendchat.channel."+getName().toLowerCase()+".chat")||p.hasPermission("legendchat.admin"))
				recipients.add(p);
		BungeecordChatMessageEvent e = new BungeecordChatMessageEvent(this,message,Legendchat.format(getFormat()),getFormat(),recipients,tags,false);
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCancelled())
			return;
		String completa = e.getFormat();
		if(Legendchat.blockRepeatedTags()) {
			if(completa.contains("prefix")&&completa.contains("groupprefix"))
				if(e.getTagValue("prefix").equals(e.getTagValue("groupprefix")))
					e.setTagValue("prefix", "");
			if(completa.contains("suffix")&&completa.contains("groupsuffix"))
				if(e.getTagValue("suffix").equals(e.getTagValue("groupsuffix")))
					e.setTagValue("suffix", "");
		}
		for(String n : e.getTags())
			completa = completa.replace("{"+n+"}", ChatColor.translateAlternateColorCodes('&', e.getTagValue(n)));
		completa = completa.replace("{msg}", message);
		
		for(Player p : e.getRecipients())
			p.sendMessage(completa);
		
		if(Legendchat.logToBukkit())
			Bukkit.getConsoleSender().sendMessage(completa);
	}
	
	private String translateStringColor(String color) {
		switch(color.toLowerCase()) {
			case "black": {return "§0";}
			case "darkblue": {return "§1";}
			case "darkgreen": {return "§2";}
			case "darkaqua": {return "§3";}
			case "darkred": {return "§4";}
			case "darkpurple": {return "§5";}
			case "gold": {return "§6";}
			case "gray": {return "§7";}
			case "darkgray": {return "§8";}
			case "blue": {return "§9";}
			case "green": {return "§a";}
			case "aqua": {return "§b";}
			case "red": {return "§c";}
			case "lightpurple": {return "§d";}
			case "yellow": {return "§e";}
			default: {return "§f";}
		}
	}
}
