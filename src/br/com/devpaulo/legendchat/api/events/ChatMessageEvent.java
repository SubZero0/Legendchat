package br.com.devpaulo.legendchat.api.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.Channel;

public class ChatMessageEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private String message = "";
	private String format = "";
	private Player sender = null;
	private Channel ch = null;
	private String base_format = "";
	private Set<Player> recipients = new HashSet<Player>();
	private boolean cancelled = false;
	private HashMap<String,String> tags = new HashMap<String,String>();
	public ChatMessageEvent(Channel ch, Player sender, String message, String format, String base_format, Set<Player> recipients, HashMap<String,String> tags, boolean cancelled) {
		this.sender=sender;
		this.message=message;
		this.recipients.addAll(recipients);
		this.tags.putAll(tags);
		this.cancelled=cancelled;
		this.ch=ch;
		this.base_format=base_format;
		this.format=ChatColor.translateAlternateColorCodes('&', format);
		for(int i=0;i<format.length();i++)
			if(format.charAt(i)=='{') {
				String tag = format.substring(i+1).split("}")[0].toLowerCase();
				if(!tag.equals("msg"))
					if(!this.tags.containsKey(tag))
						this.tags.put(tag, "");
			}
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		if(message==null)
			this.message = "";
		else
			this.message = message;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		if(format!=null)
			this.format = format;
	}
	
	public Player getSender() {
		return sender;
	}
	
	public void setSender(Player sender) {
		if(sender!=null)
			this.sender = sender;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public Set<Player> getRecipients() {
		return recipients;
	}
	
	public Channel getChannel() {
		return ch;
	}
	
	public String getBaseFormat() {
		return base_format;
	}
	
	public String baseFormatToFormat(String base_format) {
		return Legendchat.format(base_format);
	}
	
	public List<String> getTags() {
		List<String> l = new ArrayList<String>();
		l.addAll(tags.keySet());
		return l;
	}
	
	public boolean setTagValue(String tag, String value) {
		if(tag==null)
			return false;
		tag = tag.toLowerCase();
		if(!tags.containsKey(tag))
			return false;
		tags.remove(tag);
		tags.put(tag, (value==null?"":value));
		return true;
	}
	
	public String getTagValue(String tag) {
		if(tag==null)
			return null;
		tag = tag.toLowerCase();
		if(!tags.containsKey(tag))
			return null;
		return tags.get(tag);
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
