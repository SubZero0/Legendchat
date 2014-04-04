package br.com.devpaulo.legendchat.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrivateMessageEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private CommandSender sender = null;
	private CommandSender receiver = null;
	private String message = "";
	public PrivateMessageEvent(CommandSender sender, CommandSender receiver, String message) {
		this.sender=sender;
		this.receiver=receiver;
		this.message=message;
	}
	
	public CommandSender getSender() {
		return sender;
	}

	public void setSender(CommandSender sender) {
		if(sender!=null)
			this.sender = sender;
	}

	public CommandSender getReceiver() {
		return receiver;
	}

	public void setReceiver(CommandSender receiver) {
		if(receiver!=null)
			this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		if(message==null)
			this.message="";
		else
			this.message = message;
	}

	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
