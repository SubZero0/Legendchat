package br.com.devpaulo.legendchat.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.messages.MessageManager;

public class Updater {
	private String version = "";
	public Updater(String v) {
		version=v;
	}
	public Updater() {
	}
	
	public String CheckNewVersion() throws Exception {
		String v = "";
		URL url = new URL("https://api.curseforge.com/servermods/files?projectIds=74494");
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("User-Agent", "Legendchat (by PauloABR)");
		final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = reader.readLine();
        JSONArray array = (JSONArray) JSONValue.parse(response);
        if (array.size() > 0) {
            JSONObject latest = (JSONObject) array.get(array.size() - 1);
            v = ((String) latest.get("name")).split("\\(")[1].split("\\)")[0].replace("V", "");
			/*
			// Get the version's link
			String versionLink = (String) latest.get(API_LINK_VALUE);                   - POSSO PEGAR O LINK
			// Get the version's release type
			String versionType = (String) latest.get(API_RELEASE_TYPE_VALUE);           - POSSO VER SE É RELEASE
			// Get the version's game version
			String versionGameVersion = (String) latest.get(API_GAME_VERSION_VALUE);    - POSSO PEGAR A VERSÃO
			*/
        }
		else
			return null;
		boolean f = false;
		if(!version.equals(v)) {
			String[] v_obtained = v.split("\\.");
			String[] v_here = version.split("\\.");
			
			boolean draw = true;
			for(int i=0;i<(v_obtained.length>v_here.length?v_here.length:v_obtained.length);i++) {
				int n_obtained = Integer.parseInt(v_obtained[i]);
				int n_here = Integer.parseInt(v_here[i]);
				
				if(n_obtained>n_here) {
					f=true;
					break;
				}
				if(n_obtained<n_here) {
					draw=false;
					break;
				}
			}
			
			if(draw&&v_obtained.length>v_here.length)
				f=true;
		}
		
		String r = (f?v:null);
		return r;
	}
	
	private boolean updConfig = false;
	private Plugin plugin = Bukkit.getPluginManager().getPlugin("Legendchat");
	public boolean updateConfig() {
		if(!has("censor.use"))
			set("censor.use",true);
		if(!has("censor.censored_words")) {
			List<String> l = new ArrayList<String>();
			set("censor.censored_words",l);
		}
		if(updConfig)
			plugin.saveConfig();
		return updConfig;
	}
	
	private boolean has(String s) {
		return plugin.getConfig().contains(s);
	}
	
	private void set(String s,Object obj) {
		plugin.getConfig().set(s, obj);
		updConfig=true;
	}
	
	private boolean updLang = false;
	public boolean updateAndLoadLanguage(File f) {
		MessageManager m = Legendchat.getMessageManager();
		m.registerLanguageFile(f);
		m.loadMessages(f);
		if(!m.hasMessage("message16"))
			addMessage(m,"message16", "&6[Legendchat] &aYou changed @player's channel to @channel.","&6[Legendchat] &aVoce mudou o canal de @player para @channel.");
		if(!m.hasMessage("message17"))
			addMessage(m,"message17", "&6[Legendchat] &a@player changed your channel to @channel.","&6[Legendchat] &a@player mudou seu canal para @channel.");
		if(!m.hasMessage("error12"))
			addMessage(m,"error12", "&6[Legendchat] &cFocus in the channel before sending messages!","&6[Legendchat] &cEntre no canal antes de mandar mensagens.");
		if(!m.hasMessage("listcmd1"))
			addMessage(m,"listcmd1", "&6============== Legendchat - Command list ==============","&6============== Legendchat - Command list ==============");
		if(!m.hasMessage("listcmd2"))
			addMessage(m,"listcmd2", "&e@command &f- @description","&e@command &f- @description");
		if(!m.hasMessage("listcmd3"))
			addMessage(m,"listcmd3", "&6=================== Version V@version ===================","&6=================== Version V@version ===================");
		if(updLang)
			m.loadMessages(f);
		return updLang;
	}
	
	private void addMessage(MessageManager m, String name, String msg, String msg2) {
		if(Legendchat.getLanguage().equalsIgnoreCase("br"))
			m.addMessageToFile(name, msg2);
		else
			m.addMessageToFile(name, msg);
		updLang=true;
	}
	
	public boolean updateChannels() {
		boolean upd = false;
		for (File channel : new File(Bukkit.getPluginManager().getPlugin("Legendchat").getDataFolder(),"channels").listFiles()) {
			YamlConfiguration channel2 = YamlConfiguration.loadConfiguration(channel);
			if(!channel2.contains("needFocus")) {
				channel2.set("needFocus", false);
				upd=true;
			}
			try {
				channel2.save(channel);
			} catch (IOException e) {e.printStackTrace();}
		}
		return upd;
	}
}
