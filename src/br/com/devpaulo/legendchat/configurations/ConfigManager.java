package br.com.devpaulo.legendchat.configurations;

public class ConfigManager {
	private TemporaryChannelConfig tcc = null;
	public ConfigManager() {
		tcc=new TemporaryChannelConfig();
	}
	
	public TemporaryChannelConfig getTemporaryChannelConfig() {
		return tcc;
	}
	
	public void loadConfigs() {
		tcc.loadConfig();
	}
}
