package br.com.devpaulo.legendchat;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.Files;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.types.BungeecordChannel;
import br.com.devpaulo.legendchat.channels.types.PermanentChannel;
import br.com.devpaulo.legendchat.commands.Commands;
import br.com.devpaulo.legendchat.listeners.Listeners;
import br.com.devpaulo.legendchat.listeners.Listeners_old;
import br.com.devpaulo.legendchat.updater.Updater;

public class Main extends JavaPlugin implements PluginMessageListener {
	public static Permission perms = null;
	public static Economy econ = null;
	public static Chat chat = null;
	public static boolean block_econ = false;
	public static boolean block_perms = false;
	public static boolean block_chat = false;
	public static boolean bungeeActive = false;
	public static String language = "en";
	public static String need_update = null;
	
	@Override
    public void onEnable() {
		getLogger().info("Legendchat (V"+getDescription().getVersion()+") - Author: SubZero0");
		Legendchat.load(false);
		
		getServer().getPluginCommand("legendchat").setExecutor(new Commands());
		getServer().getPluginCommand("channel").setExecutor(new Commands());
		getServer().getPluginCommand("tell").setExecutor(new Commands());
		getServer().getPluginCommand("reply").setExecutor(new Commands());
		getServer().getPluginCommand("afk").setExecutor(new Commands());
		getServer().getPluginCommand("ignore").setExecutor(new Commands());
		getServer().getPluginCommand("tempchannel").setExecutor(new Commands());
		getServer().getPluginCommand("mute").setExecutor(new Commands());
		
		if(getConfig().getBoolean("use_async_chat_event",true))
			getServer().getPluginManager().registerEvents(new Listeners(), this);
		else
			getServer().getPluginManager().registerEvents(new Listeners_old(), this);
		
		getServer().getMessenger().registerOutgoingPluginChannel(this, "Legendchat");
        getServer().getMessenger().registerIncomingPluginChannel(this, "Legendchat", this);
		
		boolean check_update = true;
		if(getConfig().contains("check_for_updates"))
			if(!getConfig().getBoolean("check_for_updates"))
				check_update=false;
		if(check_update) {
			getLogger().info("Checking for updates...");
			try {
				Updater vup = new Updater(getDescription().getVersion());
				String vup_r = vup.CheckNewVersion();
				if(vup_r==null)
					getLogger().info("No updates found.");
				else {
					getLogger().info("New update avaible: V"+vup_r+"!");
					getLogger().info("Download: http://dev.bukkit.org/bukkit-plugins/legendchat/");
					need_update=vup_r;
				}
			}
			catch(Exception e) {
				getLogger().info("Error when checking for updates!");
			}
		}
		
		File file = new File(getDataFolder(),"config.yml");
		if(!file.exists()) {
			try {
				saveResource("config_template.yml",false);
				File file2 = new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
			}
			catch(Exception e) {}
		}
		reloadConfig();
		
		if(new Updater().updateConfig())
			getLogger().info("Configuration file updated!");
		
		new File(getDataFolder(),"language").mkdir();
		for(File f : getDataFolder().listFiles())
			if(f.getName().startsWith("language_"))
				try {Files.move(new File(getDataFolder(),f.getName()), new File(getDataFolder(),"language"+File.separator+f.getName()));} catch(Exception e) {}
		
		try {if(!new File(getDataFolder(),"language"+File.separator+"language_br.yml").exists()) {saveResource("language"+File.separator+"language_br.yml",false);getLogger().info("Saved language_br.yml");}}
		catch(Exception e) {}
		try {if(!new File(getDataFolder(),"language"+File.separator+"language_en.yml").exists()) {saveResource("language"+File.separator+"language_en.yml",false);getLogger().info("Saved language_en.yml");}}
		catch(Exception e) {}
		try {if(!new File(getDataFolder(),"language"+File.separator+"language_cn.yml").exists()) {saveResource("language"+File.separator+"language_cn.yml",false);getLogger().info("Saved language_cn.yml");}}
		catch(Exception e) {}
		try {if(!new File(getDataFolder(),"temporary_channels.yml").exists()) {saveResource("temporary_channels.yml",false);getLogger().info("Saved temporary_channels.yml");}}
		catch(Exception e) {}
		
		File channels = new File(getDataFolder(),"channels");
		if(!channels.exists()) {
			channels.mkdir();
			Legendchat.getChannelManager().createPermanentChannel(new PermanentChannel("global","g","{default}","GRAY",true,false,0,true,0,0,true));
			Legendchat.getChannelManager().createPermanentChannel(new PermanentChannel("local","l","{default}","YELLOW",true,false,60,false,0,0,true));
			Legendchat.getChannelManager().createPermanentChannel(new BungeecordChannel("bungeecord","b","{bungeecord}","LIGHTPURPLE",true,false,0,false,0,0,true));
		}
		
		if(new Updater().updateChannels())
			getLogger().info("Channels file updated!");
		Legendchat.getChannelManager().loadChannels();
		
		language=getConfig().getString("language").trim();
		if(new Updater().updateAndLoadLanguage(language))
			getLogger().info("Language file updated!");
		
		if (!setupPermissions()) {
			getLogger().warning("Vault is not linked to any permissions plugin.");
			block_perms=true;
        }
		else {
	        getLogger().info("Hooked to Vault (Permissions).");
		}
		
		if (!setupEconomy()) {
			getLogger().warning("Vault is not linked to any economy plugin.");
			block_econ=true;
        }
		else {
	        getLogger().info("Hooked to Vault (Economy).");
		}
		
		if (!setupChat()) {
			getLogger().warning("Vault is not linked to any chat plugin.");
			block_chat=true;
        }
		else {
	        getLogger().info("Hooked to Vault (Chat).");
		}
		
		if(getConfig().getBoolean("bungeecord.use",false))
			if(Legendchat.getChannelManager().existsChannel(getConfig().getString("bungeecord.channel","bungeecord")))
				bungeeActive=true;
		
		Legendchat.load(true);
		
		for(Player p : getServer().getOnlinePlayers())
			Legendchat.getPlayerManager().setPlayerFocusedChannel(p, Legendchat.getDefaultChannel(), false);
	}
	
	private boolean setupPermissions() {
        if(getServer().getPluginManager().getPlugin("Vault")==null)
            return false;
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if(rsp==null)
            return false;
        perms = rsp.getProvider();
        return perms != null;
    }
	
	private boolean setupChat() {
        if(getServer().getPluginManager().getPlugin("Vault")==null)
        	return false;
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if(rsp==null)
            return false;
        chat = rsp.getProvider();
        return chat != null;
    }
	
	private boolean setupEconomy() {
        if(getServer().getPluginManager().getPlugin("Vault")==null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp==null)
            return false;
        econ = rsp.getProvider();
        return econ != null;
    }
	
	@Override
    public void onDisable() {
		getLogger().info("Disabling Legendchat - Author: SubZero0");
		Legendchat.getLogManager().saveLog();
	}

	@Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(Legendchat.isBungeecordActive()) {
			if(!channel.equals("Legendchat"))
				return;
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			String raw_tags = "";
			String msg = "";
			try {
				raw_tags = in.readUTF();
				msg = in.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HashMap<String,String> tags = new HashMap<String,String>();
			raw_tags = raw_tags.substring(1, raw_tags.length()-1);
			String[] pairs = raw_tags.split(",");
			for(String separated_pairs : pairs) {
				String[] pair = separated_pairs.split("=");
				tags.put(pair[0].replace(" ", ""), (pair.length==1?"":pair[1]));
			}
			this.getServer().getLogger().info("[Legendchat] Incoming message from server "+tags.get("server"));
			BungeecordChannel c = Legendchat.getBungeecordChannel();
			if(c!=null)
				c.sendBungeecordMessage(tags, msg);
		}
    }
}
