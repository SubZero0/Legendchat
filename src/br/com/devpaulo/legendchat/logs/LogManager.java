package br.com.devpaulo.legendchat.logs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;

import br.com.devpaulo.legendchat.api.Legendchat;

public class LogManager {
	private List<Log> log = new ArrayList<Log>();
	public LogManager() {
	}
	
	public void startSavingScheduler() {
		Bukkit.getScheduler().runTaskTimer(Legendchat.getPlugin(), new Runnable() {
			public void run() {
				saveLog();
			}
		}, Legendchat.getLogToFileTime()*1200, Legendchat.getLogToFileTime()*1200);
	}
	
	public void addLogToCache(Log l) {
		if(!log.contains(l))
			log.add(l);
	}
	
	public void addLogToCache(Date d, String m) {
		log.add(new Log(d,m));
	}
	
	public void addLogToCache(String m) {
		log.add(new Log(new Date(),m));
	}
	
	public void removeLogFromCache(Log l) {
		if(log.contains(l))
			log.remove(l);
	}
	
	public List<Log> getLogCache() {
		List<Log> log2 = new ArrayList<Log>();
		log2.addAll(log);
		return log2;
	}
	
	public void saveLog() {
		final List<Log> saving_log = getLogCache();
		if(saving_log.size()==0)
			return;
		log.clear();
		new Executor(saving_log).start();
	}
	
	private class Executor extends Thread {
		private List<Log> saving_log = null;
		public Executor(List<Log> l) {
			saving_log=l;
		}
		
		public void run() {
			File f2 = new File(Legendchat.getPlugin().getDataFolder(),"logs");
			if(!f2.exists())
				f2.mkdir();
			HashMap<String,List<Log>> date_log = new HashMap<String,List<Log>>();
			for(Log l : saving_log) {
				String n = getFileName(l.getDate());
				File f = new File(Legendchat.getPlugin().getDataFolder()+File.separator+"logs",n);
				if(!f.exists()) {
					try {f.createNewFile();} catch (Exception e) {}
				}
				if(date_log.containsKey(n))
					date_log.get(n).add(l);
				else {
					List<Log> ll = new ArrayList<Log>();
					ll.add(l);
					date_log.put(n, ll);
				}
			}
			for(String n : date_log.keySet()) {
				File f = new File(Legendchat.getPlugin().getDataFolder()+File.separator+"logs",n);
				BufferedWriter  writer = null;
				try {
					writer = new BufferedWriter(new FileWriter(f, true));
					for(Log l : date_log.get(n)) {
						writer.write(formatLine(l.getDate(),l.getMessage()));
						writer.newLine();
					}
				}
				catch(Exception e) {}
				finally {try {writer.close();} catch (Exception e) {}}
			}
		}
	}
	
	private String getFileName(Date d) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(d)+".txt";
	}
	
	private String formatLine(Date d, String l) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return "["+df.format(d)+"] "+l;
	}
}
