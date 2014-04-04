package br.com.devpaulo.legendchat.channels.types;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.utils.ChannelUtils;

public class PermanentChannel implements Channel {
	private String name = "";
	private String nick = "";
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
	public PermanentChannel(String name, String nick, String format, String color, boolean shortcut, boolean focus, double distance, boolean crossworlds, int delay, double cost,boolean show_cost_msg) {
		this.name=name;
		this.nick=nick;
		this.format=format;
		this.color=ChannelUtils.translateStringColor(color);
		color2=color.toLowerCase();
		this.shortcut=shortcut;
		this.focus=focus;
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
	
	public void setNickname(String n) {
		nick=n;
	}
	
	public void setFormat(String n) {
		format=n;
	}
	
	public void setColor(ChatColor c) {
		color2=ChannelUtils.translateChatColorToStringColor(c);
		color=ChannelUtils.translateStringColor(color2);
	}
	
	public void setShortcutAllowed(boolean n) {
		shortcut=n;
	}
	
	public void setFocusNeeded(boolean n) {
		focus=n;
	}
	
	public void setCrossworlds(boolean n) {
		crossworlds=n;
	}
	
	public void setMaxDistance(double n) {
		distance=n;
	}
	
	public void setMessageCost(double n) {
		cost=n;
	}
	public void setCostPerMessage(double n) {
		cost=n;
	}
	
	public void setShowCostMessage(boolean n) {
		show_cost_msg=n;
	}
	
	public void setDelayPerMessage(int n) {
		delay=n;
	}
	
	public List<Player> getPlayersFocusedInChannel() {
		return Legendchat.getPlayerManager().getPlayersFocusedInChannel(this);
	}
	
	public List<Player> getPlayersWhoCanSeeChannel() {
		return Legendchat.getPlayerManager().getPlayersWhoCanSeeChannel(this);
	}
	
	public void sendMessage(final String message) {
		ChannelUtils.otherMessage(this, message);
	}
	
	public void sendMessage(final Player sender, final String message) {
		ChannelUtils.fakeMessage(this, sender, message);
	}
	
	public void sendMessage(Player sender, String message, String bukkit_format, boolean cancelled) {
		ChannelUtils.realMessage(this, sender, message, bukkit_format, cancelled);
	}
	
}
