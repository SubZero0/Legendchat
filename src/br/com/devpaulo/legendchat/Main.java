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

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.Channel;
import br.com.devpaulo.legendchat.commands.Commands;
import br.com.devpaulo.legendchat.listeners.Listeners;
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
		getLogger().info("Enabling Legendchat (V"+getDescription().getVersion()+") - Author: SubZero0");
		
		getServer().getPluginCommand("legendchat").setExecutor(new Commands());
		getServer().getPluginCommand("channel").setExecutor(new Commands());
		getServer().getPluginCommand("tell").setExecutor(new Commands());
		getServer().getPluginCommand("reply").setExecutor(new Commands());
		getServer().getPluginCommand("afk").setExecutor(new Commands());
		getServer().getPluginCommand("ignore").setExecutor(new Commands());
		getServer().getPluginManager().registerEvents(new Listeners(), this);
		
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
				e.printStackTrace();
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
		
		try {File file2 = new File(getDataFolder(),"language_br.yml");if(!file2.exists()) {saveResource("language_br.yml",false);getLogger().info("Saved language_br.yml");}}
		catch(Exception e) {}
		try {File file2 = new File(getDataFolder(),"language_en.yml");if(!file2.exists()) {saveResource("language_en.yml",false);getLogger().info("Saved language_en.yml");}}
		catch(Exception e) {}
		
		File channels = new File(getDataFolder(),"channels");
		if(!channels.exists()) {
			channels.mkdir();
			Legendchat.getChannelManager().createChannel(new Channel("global","g","{default}","GRAY",true,0,true,0,0,true));
			Legendchat.getChannelManager().createChannel(new Channel("local","l","{default}","YELLOW",true,60,false,0,0,true));
			Legendchat.getChannelManager().createChannel(new Channel("bungeecord","b","{bungeecord}","LIGHTPURPLE",true,0,false,0,0,true));
		}
		
		Legendchat.getChannelManager().loadChannels();
		
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
		
		if(getConfig().getBoolean("bungeecord.use"))
			if(Legendchat.getChannelManager().existsChannel(getConfig().getString("bungeecord.channel")))
				bungeeActive=true;
		
		language=getConfig().getString("language").trim();
		File lang = new File(getDataFolder(),"language_"+language+".yml");
		if(!lang.exists()) {
			lang = new File(getDataFolder(),"language_en.yml");
			language="en";
		}
		
		Legendchat.getMessageManager().loadMessages(lang);
		
		Legendchat.load();
		
		for(Player p : getServer().getOnlinePlayers())
			Legendchat.getPlayerManager().setPlayerChannel(p, Legendchat.getDefaultChannel(), false);
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
			Channel c = Legendchat.getBungeecordChannel();
			if(c!=null)
				c.sendBungeecordMessage(tags, msg);
		}
    }
}
