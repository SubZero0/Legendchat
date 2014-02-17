package br.com.devpaulo.legendchat.messages;

import java.io.File;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageManager {
	private static HashMap<String,String> msgs = new  HashMap<String,String>();
	public MessageManager() {
	}
	
	public void loadMessages(File f) {
		msgs.clear();
		YamlConfiguration msglist = YamlConfiguration.loadConfiguration(f);
		for(String n : msglist.getConfigurationSection("").getKeys(false))
			msgs.put(n.toLowerCase(), msglist.getString(n));
	}
	
	public String getMessage(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msgs.get(msg.toLowerCase()));
	}
	
}
