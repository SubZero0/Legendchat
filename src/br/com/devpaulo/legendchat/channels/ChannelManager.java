package br.com.devpaulo.legendchat.channels;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.api.Legendchat;

public class ChannelManager {
	private static HashMap<String,Channel> canais = new HashMap<String,Channel>();
	public ChannelManager() {
	}
	
	public void createChannel(Channel c) {
		if(existsChannel(c.getName()))
			return;
		canais.put(c.getName().toLowerCase(), c);
		File channel = new File(Bukkit.getPluginManager().getPlugin("Legendchat").getDataFolder(),"channels"+File.separator+c.getName().toLowerCase()+".yml");
		if(!channel.exists()) {
			try {channel.createNewFile();} catch(Exception e) {}
			YamlConfiguration channel2 = YamlConfiguration.loadConfiguration(channel);
			channel2.set("name", c.getName());
			channel2.set("nickname", c.getNickname());
			channel2.set("format", c.getFormat());
			channel2.set("color", c.getStringColor());
			channel2.set("shortcutAllowed", c.isShortcutAllowed());
			channel2.set("distance", c.getMaxDistance());
			channel2.set("crossworlds", c.isCrossworlds());
			channel2.set("delayPerMessage", c.getDelayPerMessage());
			channel2.set("costPerMessage", c.getCostPerMessage());
			channel2.set("showCostMessage", c.showCostMessage());
			try {channel2.save(channel);} catch (Exception e) {}
		}
	}
	
	public void deleteChannel(Channel c) {
		if(!existsChannel(c.getName()))
			return;
		for(Player p : c.getPlayersInChannel())
			Legendchat.getPlayerManager().setPlayerChannel(p, Legendchat.getDefaultChannel(), false);
		canais.remove(c.getName().toLowerCase());
		new File(Bukkit.getPluginManager().getPlugin("Legendchat").getDataFolder(),"channels"+File.separator+c.getName().toLowerCase()+".yml").delete();
	}
	
	public Channel getChannelByName(String name) {
		name = name.toLowerCase();
		if(existsChannel(name))
			return canais.get(name);
		return null;
	}
	
	public Channel getChannelByNickname(String nickname) {
		for(Channel c : getChannels())
			if(c.getNickname().equalsIgnoreCase(nickname))
				return c;
		return null;
	}
	
	public boolean existsChannel(String name) {
		return canais.containsKey(name.toLowerCase());
	}
	
	public List<Channel> getChannels() {
		List<Channel> c = new ArrayList<Channel>();
		c.addAll(canais.values());
		return c;
	}
	
	public void loadChannels() {
		canais.clear();
		for (File channel : new File(Bukkit.getPluginManager().getPlugin("Legendchat").getDataFolder(),"channels").listFiles()) {
	        if(!channel.getName().toLowerCase().equals(channel.getName()))
	        	channel.renameTo(new File(Bukkit.getPluginManager().getPlugin("Legendchat").getDataFolder(),"channels"+File.separator+channel.getName().toLowerCase()));
	        loadChannel(channel);
	    }
		for(Player p : Bukkit.getOnlinePlayers())
			Legendchat.getPlayerManager().setPlayerChannel(p, Legendchat.getDefaultChannel(), false);
	}
	
	private void loadChannel(File channel) {
		YamlConfiguration channel2 = YamlConfiguration.loadConfiguration(channel);
		createChannel(new Channel(channel2.getString("name"),channel2.getString("nickname"),channel2.getString("format"),channel2.getString("color"),channel2.getBoolean("shortcutAllowed"),channel2.getDouble("distance"),channel2.getBoolean("crossworlds"),channel2.getInt("delayPerMessage"),channel2.getDouble("costPerMessage"),channel2.getBoolean("showCostMessage")));
	}
	
}
