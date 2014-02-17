package br.com.devpaulo.legendchat.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {
	public Utils() {
	}
	
	public static String translateAlternateChatColorsWithPermission(Player p, String msg) {
		if(msg.contains("&0")&&(p.hasPermission("legendchat.color.black")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&0", ChatColor.BLACK.toString());}
		if(msg.contains("&1")&&(p.hasPermission("legendchat.color.darkblue")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&1", ChatColor.DARK_BLUE.toString());}
		if(msg.contains("&2")&&(p.hasPermission("legendchat.color.darkgreen")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&2", ChatColor.DARK_GREEN.toString());}
		if(msg.contains("&3")&&(p.hasPermission("legendchat.color.darkaqua")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&3", ChatColor.DARK_AQUA.toString());}
		if(msg.contains("&4")&&(p.hasPermission("legendchat.color.darkred")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&4", ChatColor.DARK_RED.toString());}
		if(msg.contains("&5")&&(p.hasPermission("legendchat.color.darkpurple")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&5", ChatColor.DARK_PURPLE.toString());}
		if(msg.contains("&6")&&(p.hasPermission("legendchat.color.gold")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&6", ChatColor.GOLD.toString());}
		if(msg.contains("&7")&&(p.hasPermission("legendchat.color.gray")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&7", ChatColor.GRAY.toString());}
		if(msg.contains("&8")&&(p.hasPermission("legendchat.color.darkgray")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&8", ChatColor.DARK_GRAY.toString());}
		if(msg.contains("&9")&&(p.hasPermission("legendchat.color.blue")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&9", ChatColor.BLUE.toString());}
		if(msg.contains("&a")&&(p.hasPermission("legendchat.color.green")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&a", ChatColor.GREEN.toString());}
		if(msg.contains("&b")&&(p.hasPermission("legendchat.color.aqua")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&b", ChatColor.AQUA.toString());}
		if(msg.contains("&c")&&(p.hasPermission("legendchat.color.red")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&c", ChatColor.RED.toString());}
		if(msg.contains("&d")&&(p.hasPermission("legendchat.color.lightpurple")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&d", ChatColor.LIGHT_PURPLE.toString());}
		if(msg.contains("&e")&&(p.hasPermission("legendchat.color.yellow")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&e", ChatColor.YELLOW.toString());}
		if(msg.contains("&f")&&(p.hasPermission("legendchat.color.white")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&f", ChatColor.WHITE.toString());}
		if(msg.contains("&k")&&(p.hasPermission("legendchat.color.obfuscated")||p.hasPermission("legendchat.color.obfuscate")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&k", ChatColor.MAGIC.toString());}
		if(msg.contains("&l")&&(p.hasPermission("legendchat.color.bold")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&l", ChatColor.BOLD.toString());}
		if(msg.contains("&m")&&(p.hasPermission("legendchat.color.strikethrough")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&m", ChatColor.STRIKETHROUGH.toString());}
		if(msg.contains("&n")&&(p.hasPermission("legendchat.color.underline")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&0n", ChatColor.UNDERLINE.toString());}
		if(msg.contains("&o")&&(p.hasPermission("legendchat.color.italic")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&o", ChatColor.ITALIC.toString());}
		if(msg.contains("&r")&&(p.hasPermission("legendchat.color.reset")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&r", ChatColor.RESET.toString());}
		if(msg.contains("&A")&&(p.hasPermission("legendchat.color.green")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&A", ChatColor.GREEN.toString());}
		if(msg.contains("&B")&&(p.hasPermission("legendchat.color.aqua")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&B", ChatColor.AQUA.toString());}
		if(msg.contains("&C")&&(p.hasPermission("legendchat.color.red")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&C", ChatColor.RED.toString());}
		if(msg.contains("&D")&&(p.hasPermission("legendchat.color.lightpurple")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&D", ChatColor.LIGHT_PURPLE.toString());}
		if(msg.contains("&E")&&(p.hasPermission("legendchat.color.yellow")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&E", ChatColor.YELLOW.toString());}
		if(msg.contains("&F")&&(p.hasPermission("legendchat.color.white")||p.hasPermission("legendchat.color.allcolors")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&F", ChatColor.WHITE.toString());}
		if(msg.contains("&K")&&(p.hasPermission("legendchat.color.obfuscated")||p.hasPermission("legendchat.color.obfuscate")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&K", ChatColor.MAGIC.toString());}
		if(msg.contains("&L")&&(p.hasPermission("legendchat.color.bold")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&L", ChatColor.BOLD.toString());}
		if(msg.contains("&M")&&(p.hasPermission("legendchat.color.strikethrough")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&M", ChatColor.STRIKETHROUGH.toString());}
		if(msg.contains("&N")&&(p.hasPermission("legendchat.color.underline")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&N", ChatColor.UNDERLINE.toString());}
		if(msg.contains("&O")&&(p.hasPermission("legendchat.color.italic")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&O", ChatColor.ITALIC.toString());}
		if(msg.contains("&R")&&(p.hasPermission("legendchat.color.reset")||p.hasPermission("legendchat.color.allformats")||p.hasPermission("legendchat.admin"))) {msg=msg.replace("&R", ChatColor.RESET.toString());}
		return msg;
	}
	
	public static String removeDoubleSpaces(String msg) {
		String msg_c = "";
		for(int i=0;i<msg.length();i++) {
			if(i>0) {
				if(Character.compare(msg.charAt(i), msg.charAt(i-1))==0&&Character.compare(msg.charAt(i), ' ')==0)
					continue;
				else
					msg_c+=msg.charAt(i);
			}
			else
				msg_c+=msg.charAt(i);
		}
		return (msg_c.equals(" ")?"":msg_c);
	}
}
