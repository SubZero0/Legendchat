package br.com.devpaulo.legendchat.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
		conn.addRequestProperty("User-Agent", "Legendchat (by PauloABR)");
		BufferedReader reader = null;
		try {
			reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
		}
		catch(Exception e) {
			throw new Exception();
		}
        String response = reader.readLine();
        JSONArray array = (JSONArray) JSONValue.parse(response);
        if (array.size() > 0) {
            JSONObject latest = (JSONObject) array.get(array.size() - 1);
            v = ((String) latest.get("name")).split("\\(")[1].split("\\)")[0].replace("V", "");
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
		InputStream is = plugin.getResource(("config_template.yml").replace('\\', '/'));
		YamlConfiguration c = YamlConfiguration.loadConfiguration(is);
		for(String n : c.getConfigurationSection("").getKeys(true))
			if(!has(n))
				set(n,c.get(n));
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
	public boolean updateAndLoadLanguage(String language) {
		File f = new File(plugin.getDataFolder(),"language"+File.separator+"language_"+language+".yml");
		MessageManager m = Legendchat.getMessageManager();
		m.registerLanguageFile(f);
		m.loadMessages(f);
		InputStream is = null;
		if((is = plugin.getResource(("language"+File.separator+"language_"+language+".yml").replace('\\', '/')))==null)
			is=plugin.getResource(("language"+File.separator+"language_en.yml").replace('\\', '/'));
		YamlConfiguration c = YamlConfiguration.loadConfiguration(is);
		for(String n : c.getConfigurationSection("").getKeys(false))
			if(!m.hasMessage(n))
				addMessage(m,n,c.getString(n));
		if(updLang)
			m.loadMessages(f);
		return updLang;
	}
	
	private void addMessage(MessageManager m, String name, String msg) {
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
