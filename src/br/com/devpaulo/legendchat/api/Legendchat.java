package br.com.devpaulo.legendchat.api;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import br.com.devpaulo.legendchat.Main;
import br.com.devpaulo.legendchat.channels.Channel;
import br.com.devpaulo.legendchat.channels.ChannelManager;
import br.com.devpaulo.legendchat.delays.DelayManager;
import br.com.devpaulo.legendchat.ignore.IgnoreManager;
import br.com.devpaulo.legendchat.messages.MessageManager;
import br.com.devpaulo.legendchat.mutes.MuteManager;
import br.com.devpaulo.legendchat.players.PlayerManager;
import br.com.devpaulo.legendchat.privatemessages.PrivateMessageManager;

public class Legendchat {
	private static boolean logToBukkit = false;
	private static boolean blockRepeatedTags = false;
	private static boolean showNoOneHearsYou = false;
	private static boolean forceRemoveDoubleSpacesFromBukkit = false;
	private static boolean blockShortcutsWhenCancelled = false;
	private static boolean isBungeecordActive = false;
	private static Channel defaultChannel = null;
	private static Channel bungeecordChannel = null;
	private static Plugin plugin = null;
	private static HashMap<String,String> formats = new HashMap<String,String>();
	private static HashMap<String,String> pm_formats = new HashMap<String,String>();
	private static String language = "en";
	
	public static ChannelManager getChannelManager() {
		return new ChannelManager();
	}
	
	public static PlayerManager getPlayerManager() {
		return new PlayerManager();
	}
	
	public static MessageManager getMessageManager() {
		return new MessageManager();
	}
	
	public static IgnoreManager getIgnoreManager() {
		return new IgnoreManager();
	}
	
	public static PrivateMessageManager getPrivateMessageManager() {
		return new PrivateMessageManager();
	}
	
	public static DelayManager getDelayManager() {
		return new DelayManager();
	}
	
	public static MuteManager getMuteManager() {
		return new MuteManager();
	}
	
	public static Channel getDefaultChannel() {
		return defaultChannel;
	}
	
	public static boolean logToBukkit() {
		return logToBukkit;
	}
	
	public static boolean blockRepeatedTags() {
		return blockRepeatedTags;
	}
	
	public static boolean showNoOneHearsYou() {
		return showNoOneHearsYou;
	}
	
	public static boolean forceRemoveDoubleSpacesFromBukkit() {
		return forceRemoveDoubleSpacesFromBukkit;
	}
	
	public static boolean blockShortcutsWhenCancelled() {
		return blockShortcutsWhenCancelled;
	}
	
	public static boolean isBungeecordActive() {
		return isBungeecordActive;
	}
	
	public static Channel getBungeecordChannel() {
		return bungeecordChannel;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static String format(String msg) {
		for(String f : formats.keySet())
			msg = msg.replace("{"+f+"}", formats.get(f));
		return msg;
	}
	
	public static String getFormat(String base_format) {
		return formats.get(base_format.toLowerCase());
	}
	
	public static String getPrivateMessageFormat(String format) {
		return pm_formats.get(format.toLowerCase());
	}
	
	public static String getLanguage() {
		return language;
	}
	
	public static void load() {
		FileConfiguration fc = Bukkit.getPluginManager().getPlugin("Legendchat").getConfig();
		defaultChannel=Legendchat.getChannelManager().getChannelByName(fc.getString("default_channel").toLowerCase());
		logToBukkit=fc.getBoolean("log_to_bukkit");
		blockRepeatedTags=fc.getBoolean("block_repeated_tags");
		showNoOneHearsYou=fc.getBoolean("show_no_one_hears_you");
		forceRemoveDoubleSpacesFromBukkit=fc.getBoolean("force_remove_double_spaces_from_bukkit");
		blockShortcutsWhenCancelled=fc.getBoolean("block_shortcuts_when_cancelled");
		isBungeecordActive=Main.bungeeActive;
		bungeecordChannel=getChannelManager().getChannelByName(fc.getString("bungeecord.channel"));
		plugin=Bukkit.getPluginManager().getPlugin("Legendchat");
		language=Main.language;
		formats.clear();
		pm_formats.clear();
		for(String f : fc.getConfigurationSection("format").getKeys(false))
			formats.put(f.toLowerCase(), fc.getString("format."+f));
		for(String f : fc.getConfigurationSection("private_message_format").getKeys(false))
			pm_formats.put(f.toLowerCase(), fc.getString("private_message_format."+f));
	}
}
