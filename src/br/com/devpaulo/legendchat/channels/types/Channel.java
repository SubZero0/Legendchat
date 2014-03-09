package br.com.devpaulo.legendchat.channels.types;

import java.util.List;

import org.bukkit.entity.Player;

public interface Channel {
	
	public String getName();
	
	public String getNickname();
	
	public String getFormat();
	
	public String getColor();
	
	public String getStringColor();
	
	public boolean isShortcutAllowed();
	
	public boolean isFocusNeeded();
	
	public boolean isCrossworlds();
	
	public double getMaxDistance();
	
	public double getMessageCost();
	public double getCostPerMessage();
	
	public boolean showCostMessage();
	
	public int getDelayPerMessage();
	
	public List<Player> getPlayersFocusedInChannel();
	
	public List<Player> getPlayersWhoCanSeeChannel();
	
	public void sendMessage(Player sender, String message);
	
	public void sendMessage(Player sender, String message, String bukkit_format, boolean cancelled);
}
