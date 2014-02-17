package br.com.devpaulo.legendchat.mutes;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import br.com.devpaulo.legendchat.api.Legendchat;

public class Mute {
	private String name = "";
	private int time = 0;
	private BukkitTask timer = null;
	public Mute(String name) {
		this.name=name.toLowerCase();
	}
	
	public void mute(int time2) {
		if(timer!=null)
			timer.cancel();
		time=time2;
		if(time>0)
			timer = Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("Legendchat"), new Runnable() {
				public void run() {
					if(time>1)
						time-=1;
					else
						unmute();
				}
			}, 1200, 1200);
	}
	
	public void unmute() {
		if(timer!=null)
			timer.cancel();
		time=0;
		Legendchat.getMuteManager().removePlayerMute(name);
	}
	
	public int getTimeLeft() {
		return time;
	}
	
	public String getPlayerName() {
		return name;
	}

}
