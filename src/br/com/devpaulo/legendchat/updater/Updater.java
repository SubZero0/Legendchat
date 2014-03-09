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
		if(!has("text_to_tag")) {
			List<String> l = new ArrayList<String>();
			set("text_to_tag",l);
		}
		if(!has("log_to_file.use"))
			set("log_to_file.use",false);
		if(!has("log_to_file.time"))
			set("log_to_file.time",10);
		if(!has("send_fake_message_to_chat"))
			set("send_fake_message_to_chat",true);
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
		if(!m.hasMessage("listtc1"))
			addMessage(m,"listtc1", "&6========== Temporary channels - Command list ==========","&6========== Temporary channels - Command list ==========");
		if(!m.hasMessage("listtc2"))
			addMessage(m,"listtc2", "&e@command &f- @description","&e@command &f- @description");
		if(!m.hasMessage("listtc3"))
			addMessage(m,"listtc3", "&6====================================================","&6====================================================");
		if(!m.hasMessage("tc_list1"))
			addMessage(m,"tc_list1", "&6[Legendchat] Temporary channels list:","&6[Legendchat] Lista de canais temporarios:");
		if(!m.hasMessage("tc_list2"))
			addMessage(m,"tc_list2", "&7Channel: &f@name &7(&f@nick&7) - Leader: &f@leader","&7Canal: &f@name &7(&f@nick&7) - Lider: &f@leader");
		if(!m.hasMessage("tc_ch1"))
			addMessage(m,"tc_ch1", "&6[@channel] &a@player joined the channel.","&6[@channel] &a@player entrou no canal.");
		if(!m.hasMessage("tc_ch2"))
			addMessage(m,"tc_ch2", "&6[@channel] &a@player left the channel.","&6[@channel] &a@player saiu do canal.");
		if(!m.hasMessage("tc_ch3"))
			addMessage(m,"tc_ch3", "&6[@channel] &a@player is now a moderator!","&6[@channel] &a@player e um novo moderador!");
		if(!m.hasMessage("tc_ch4"))
			addMessage(m,"tc_ch4", "&6[@channel] &a@player was invited to the channel by @mod.","&6[@channel] &a@player foi convidado para o canal por @mod.");
		if(!m.hasMessage("tc_ch5"))
			addMessage(m,"tc_ch5", "&6[@channel] &a@player was kicked from the channel by @mod.","&6[@channel] &a@player foi kickado do canal por @mod.");
		if(!m.hasMessage("tc_ch6"))
			addMessage(m,"tc_ch6", "&6[@channel] &a@player is the new channel leader!","&6[@channel] &a@player e o novo lider do canal!");
		if(!m.hasMessage("tc_ch7"))
			addMessage(m,"tc_ch7", "&6[@channel] &aChannel color changed!","&6[@channel] &aCor do canal mudada!");
		if(!m.hasMessage("tc_ch8"))
			addMessage(m,"tc_ch8", "&6[@channel] &aModerators: &f@mods","&6[@channel] &aModeradores: &f@mods");
		if(!m.hasMessage("tc_ch9"))
			addMessage(m,"tc_ch9", "&6[@channel] &aLeader: &f@leader","&6[@channel] &aLider: &f@leader");
		if(!m.hasMessage("tc_ch10"))
			addMessage(m,"tc_ch10", "&6[@channel] &a@player is now a normal member.","&6[@channel] &a@player e agora um membro normal.");
		if(!m.hasMessage("tc_ch11"))
			addMessage(m,"tc_ch11", "&6[@channel] &aMembers: &f@members","&6[@channel] &aMembros: &f@members");
		if(!m.hasMessage("tc_msg1"))
			addMessage(m,"tc_msg1", "&6[Legendchat] &cTemporary channel @channel deleted!","&6[Legendchat] &cCanal temporario @channel deletado!");
		if(!m.hasMessage("tc_msg2"))
			addMessage(m,"tc_msg2", "&6[Legendchat] &aTemporary channel @channel created!","&6[Legendchat] &aTCanal temporario @channel criado!");
		if(!m.hasMessage("tc_msg3"))
			addMessage(m,"tc_msg3", "&6[Legendchat] &aYou were invited to join the temporary channel @channel by @player!","&6[Legendchat] &aVoce foi convidado para entrar no canal temporario @channel por @player!");
		if(!m.hasMessage("tc_msg4"))
			addMessage(m,"tc_msg4", "&6[Legendchat] &aTemporary channel @channel deleted by @player.","&6[Legendchat] &aCanal temporario @channel deletado por @player.");
		if(!m.hasMessage("tc_msg5_1"))
			addMessage(m,"tc_msg5_1", "&6[Legendchat] My temporary channels:","&6[Legendchat] Meus canais temporarios:");
		if(!m.hasMessage("tc_msg5_2"))
			addMessage(m,"tc_msg5_2", "&7Channel: &f@name &7(&f@nick&7) - My rank: &f@rank","&7Canal: &f@name &7(&f@nick&7) - Meu rank: &f@rank");
		if(!m.hasMessage("tc_r1"))
			addMessage(m,"tc_r1", "Leader", "Lider");
		if(!m.hasMessage("tc_r2"))
			addMessage(m,"tc_r2", "Moderator", "Moderador");
		if(!m.hasMessage("tc_r3"))
			addMessage(m,"tc_r3", "Member", "Membro");
		if(!m.hasMessage("tc_error1"))
			addMessage(m,"tc_error1", "&6[Legendchat] &cName already exists!","&6[Legendchat] &cNome ja existente!");
		if(!m.hasMessage("tc_error2"))
			addMessage(m,"tc_error2", "&6[Legendchat] &cNickname already exists!","&6[Legendchat] &cApelido ja existente!");
		if(!m.hasMessage("tc_error3"))
			addMessage(m,"tc_error3", "&6[Legendchat] &cYou do not have permission.","&6[Legendchat] &cVoce nao tem permissao.");
		if(!m.hasMessage("tc_error4"))
			addMessage(m,"tc_error4", "&6[Legendchat] &cTemporary channel not found.","&6[Legendchat] &cCanal temporario nao encontrado.");
		if(!m.hasMessage("tc_error5"))
			addMessage(m,"tc_error5", "&6[Legendchat] &cYou need to be the leader to do this.","&6[Legendchat] &cVoce precisa ser o lider para fazer isso.");
		if(!m.hasMessage("tc_error6"))
			addMessage(m,"tc_error6", "&6[Legendchat] &cColor code not found.","&6[Legendchat] &cCodigo de cor nao encontrado.");
		if(!m.hasMessage("tc_error7"))
			addMessage(m,"tc_error7", "&6[Legendchat] &cYou were not invited.","&6[Legendchat] &cVoce nao foi convidado.");
		if(!m.hasMessage("tc_error8"))
			addMessage(m,"tc_error8", "&6[Legendchat] &cYou are not in this temporary channel.","&6[Legendchat] &cVoce nao esta nesse canal temporario.");
		if(!m.hasMessage("tc_error9"))
			addMessage(m,"tc_error9", "&6[Legendchat] &cTemporary channel with the maximum amount of players.","&6[Legendchat] &cCanal temporario com o maximo possivel de jogadores.");
		if(!m.hasMessage("tc_error10"))
			addMessage(m,"tc_error10", "&6[Legendchat] &cYou have entered the maximum number of temporary channels per player.","&6[Legendchat] &cVoce ja entrou no maximo de canais temporarios por jogador.");
		if(!m.hasMessage("tc_error11"))
			addMessage(m,"tc_error11", "&6[Legendchat] &cMaximum size of the name: @max","&6[Legendchat] &cTamanho maximo do nome: @max");
		if(!m.hasMessage("tc_error12"))
			addMessage(m,"tc_error12", "&6[Legendchat] &cMaximum size of the nickname: @max","&6[Legendchat] &cTamanho maximo do apelido: @max");
		if(!m.hasMessage("tc_error13"))
			addMessage(m,"tc_error13", "&6[Legendchat] &cThis player is already a moderator.","&6[Legendchat] &cEsse jogador ja e um moderador.");
		if(!m.hasMessage("tc_error14"))
			addMessage(m,"tc_error14", "&6[Legendchat] &cThis player is not in the channel.","&6[Legendchat] &cEsse jogador nao esta no canal.");
		if(!m.hasMessage("tc_error15"))
			addMessage(m,"tc_error15", "&6[Legendchat] &cTemporary channel with the maximum amount of moderators.","&6[Legendchat] &cCanal temporario com o maximo possivel de moderadores.");
		if(!m.hasMessage("tc_error16"))
			addMessage(m,"tc_error16", "&6[Legendchat] &cThis player is already in the channel.","&6[Legendchat] &cEsse jogador ja esta no canal.");
		if(!m.hasMessage("tc_error17"))
			addMessage(m,"tc_error17", "&6[Legendchat] &cThis player was already invited to the channel.","&6[Legendchat] &cEsse jogador ja foi convidado para este canal.");
		if(!m.hasMessage("tc_error18"))
			addMessage(m,"tc_error18", "&6[Legendchat] &cYou can not invite players to this channel.","&6[Legendchat] &cVoce nao pode convidar jogadores para este canal.");
		if(!m.hasMessage("tc_error19"))
			addMessage(m,"tc_error19", "&6[Legendchat] &cYou can not kick players from this channel.","&6[Legendchat] &cVoce nao pode kickar jogadores deste canal.");
		if(!m.hasMessage("tc_error20"))
			addMessage(m,"tc_error20", "&6[Legendchat] &cYou can not kick this player.","&6[Legendchat] &cVoce nao pode kickar esse jogador.");
		if(!m.hasMessage("tc_error21"))
			addMessage(m,"tc_error21", "&6[Legendchat] &cTemporary channel not found.","&6[Legendchat] &cCanal temporario nao encontrado.");
		if(!m.hasMessage("tc_error22"))
			addMessage(m,"tc_error22", "&6[Legendchat] &cName blacklisted.","&6[Legendchat] &cNome na lista negra.");
		if(!m.hasMessage("tc_error23"))
			addMessage(m,"tc_error23", "&6[Legendchat] &cNickname blacklisted.","&6[Legendchat] &cApelido na lista negra.");
		if(!m.hasMessage("tc_error24"))
			addMessage(m,"tc_error24", "&6[Legendchat] &cColor code blacklisted.","&6[Legendchat] &cCodigo de cor na lista negra.");
		if(!m.hasMessage("tc_error25"))
			addMessage(m,"tc_error25", "&6[Legendchat] &cThis player is not a moderator.","&6[Legendchat] &cEsse jogador nao e um moderador.");
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
