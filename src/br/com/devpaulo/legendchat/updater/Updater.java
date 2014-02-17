package br.com.devpaulo.legendchat.updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Updater {
	private String version;
	public Updater(String v) {
		version=v;
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
}
