package br.com.devpaulo.legendchat.channels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.types.Channel;
import br.com.devpaulo.legendchat.channels.types.TemporaryChannel;
import br.com.devpaulo.legendchat.configurations.TemporaryChannelConfig;

public class TemporaryChannelManager {
	public TemporaryChannelManager() {
	}
	
	public void createTempChannel(Player leader, String channel_name, String channel_nickname) {
		if(!canCreateTempChannel(channel_name,channel_nickname))
			return;
		TemporaryChannelConfig c = Legendchat.getConfigManager().getTemporaryChannelConfig();
		createTempChannel(new TemporaryChannel(channel_name,channel_nickname,c.getFormat(),c.getStringColor(),c.isShortcutAllowed(),c.isFocusNeeded(),c.getMaxDistance(),c.isCrossworlds(),c.getDelayPerMessage(),c.getCostPerMessage(),c.showCostMessage(),leader,c.moderatorsCanKick(),c.moderatorsCanInvite()));
	}
	
	public void createTempChannel(TemporaryChannel c) {
		if(!canCreateTempChannel(c.getName(),c.getNickname()))
			return;
		Legendchat.getChannelManager().createChannel(c);
	}
	
	public void deleteTempChannel(TemporaryChannel c) {
		c.user_list().clear();
		c.moderator_list().clear();
		c.leader_set(null);
		Legendchat.getChannelManager().deleteChannel(c);
	}
	
	public boolean canCreateTempChannel(String name, String nickname) {
		if(Legendchat.getChannelManager().getChannelByName(name)!=null||Legendchat.getChannelManager().getChannelByNickname(nickname)!=null||Legendchat.getConfigManager().getTemporaryChannelConfig().getBlockedNames().contains(name)||Legendchat.getConfigManager().getTemporaryChannelConfig().getBlockedNames().contains(nickname))
			return false;
		return true;
	}
	
	public boolean existsTempChannel(String name) {
		if(!Legendchat.getChannelManager().existsChannel(name))
			return false;
		if(Legendchat.getChannelManager().getChannelByName(name) instanceof TemporaryChannel)
			return true;
		return false;
	}
	
	public TemporaryChannel getTempChannelByName(String name) {
		Channel c = Legendchat.getChannelManager().getChannelByName(name);
		if(c==null)
			return null;
		if(c instanceof TemporaryChannel)
			return (TemporaryChannel) c;
		return null;
	}
	
	public TemporaryChannel getTempChannelByNickname(String name) {
		Channel c = Legendchat.getChannelManager().getChannelByNickname(name);
		if(c==null)
			return null;
		if(c instanceof TemporaryChannel)
			return (TemporaryChannel) c;
		return null;
	}
	
	public TemporaryChannel getTempChannelByNameOrNickname(String name_or_nickname) {
		TemporaryChannel r = null;
		r=getTempChannelByName(name_or_nickname);
		if(r==null)
			r=getTempChannelByNickname(name_or_nickname);
		return r;
	}
	
	public List<TemporaryChannel> getAllTempChannels() {
		List<TemporaryChannel> l = new ArrayList<TemporaryChannel>();
		for(Channel c : Legendchat.getChannelManager().getChannels())
			if(c instanceof TemporaryChannel)
				l.add((TemporaryChannel) c);
		return l;
	}
	
	public List<TemporaryChannel> getPlayerTempChannels(Player p) {
		List<TemporaryChannel> l = new ArrayList<TemporaryChannel>();
		for(TemporaryChannel c : getAllTempChannels())
			if(c.user_list().contains(p))
				l.add(c);
		return l;
	}
	
	public List<TemporaryChannel> getPlayerTempChannelsInvites(Player p) {
		List<TemporaryChannel> l = new ArrayList<TemporaryChannel>();
		for(TemporaryChannel c : getAllTempChannels())
			if(c.invite_list().contains(p))
				l.add(c);
		return l;
	}
	
	public List<TemporaryChannel> getPlayerTempChannelsAdmin(Player p) {
		List<TemporaryChannel> l = new ArrayList<TemporaryChannel>();
		for(TemporaryChannel c : getAllTempChannels())
			if(c.leader_get()==p)
				l.add(c);
		return l;
	}
	
	public void playerDisconnect(Player p) {
		for(TemporaryChannel c : getPlayerTempChannels(p)) {
			c.invite_remove(p);
			c.user_remove(p);
		}
	}
}
