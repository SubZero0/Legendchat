package br.com.devpaulo.legendchat.configurations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import br.com.devpaulo.legendchat.api.Legendchat;

public class TemporaryChannelConfig {
	private String format = "";
	private String color = "";
	private String color2 = "";
	private boolean shortcut = false;
	private boolean focus = false;
	private double distance = 0;
	private boolean crossworlds = false;
	private double cost = 0;
	private boolean show_cost_msg = false;
	private int delay = 0;
	private int max_joins = 0;
	private int max_admin = 0;
	private int max_joins_c = 0;
	private int max_mods = 0;
	private int max_name_size = 15;
	private int max_nick_size = 5;
	private boolean mod_can_kick = false;
	private boolean mod_can_invite = false;
	private boolean enabled = true;
	private List<String> blocked_names = new ArrayList<String>();
	private List<String> blocked_colors = new ArrayList<String>();
	
	public boolean isTemporaryChannelsEnabled() {
		return enabled;
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
	
	public boolean isFocusNeeded() {
		return focus;
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
	
	public int getMaxJoinsPerPlayer() {
		return max_joins;
	}
	
	public int getMaxAdminPerPlayer() {
		return max_admin;
	}
	
	public int getMaxJoinsPerChannel() {
		return max_joins_c;
	}
	
	public int getMaxModeratorsPerChannel() {
		return max_mods;
	}
	
	public boolean moderatorsCanKick() {
		return mod_can_kick;
	}
	
	public boolean moderatorsCanInvite() {
		return mod_can_invite;
	}
	
	public int getMaxChannelNameLength() {
		return max_name_size;
	}
	
	public int getMaxChannelNicknameLength() {
		return max_nick_size;
	}
	
	public List<String> getBlockedNames() {
		return blocked_names;
	}
	
	public List<String> getBlockedColors() {
		return blocked_colors;
	}
	
	public void loadConfig() {
		File f = new File(Legendchat.getPlugin().getDataFolder(),"temporary_channels.yml");
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		enabled=c.getBoolean("enabled");
		format=c.getString("format");
		String color3 = c.getString("color");
		color=translateStringColor(color3);
		color2=color3.toLowerCase();
		shortcut=c.getBoolean("shortcutAllowed");
		focus=c.getBoolean("needFocus");
		distance=c.getDouble("distance");
		crossworlds=c.getBoolean("crossworlds");
		cost=c.getDouble("costPerMessage");
		show_cost_msg=c.getBoolean("showCostMessage");
		delay=c.getInt("delayPerMessage");
		max_joins=c.getInt("maxJoinsPerPlayer");
		max_admin=c.getInt("maxAdminPerPlayer");
		max_joins_c=c.getInt("maxJoinsPerChannel");
		max_mods=c.getInt("maxModeratorsPerChannel");
		mod_can_kick=c.getBoolean("moderator.canKick");
		mod_can_invite=c.getBoolean("moderator.canInvite");
		max_name_size=c.getInt("maxChannelNameLength");
		max_nick_size=c.getInt("maxChannelNicknameLength");
		blocked_names.clear();
		for(String n : c.getStringList("blocked_names"))
			blocked_names.add(n.toLowerCase());
		blocked_colors.clear();
		for(String n : c.getStringList("blocked_colors"))
			blocked_colors.add(n.toLowerCase());
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
