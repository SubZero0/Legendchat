package br.com.devpaulo.legendchat.censor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CensorManager {
	private HashMap<String,String> words = new HashMap<String,String>();
	public CensorManager() {
	}
	
	public void loadCensoredWords(List<String> l) {
		words.clear();
		for(String word : l) {
			if(word.contains(";"))
				words.put(word.split(";")[0].toLowerCase(),word.split(";")[1]);
			else
				words.put(word.split(";")[0].toLowerCase(),"");
		}
	}
	
	public void addCensoredWord(String word, String replace) {
		if(replace==null)
			replace="";
		if(!words.containsKey(word.toLowerCase()))
			words.put(word.toLowerCase(),replace);
	}
	
	public void removeCensoredWord(String word) {
		if(words.containsKey(word.toLowerCase()))
			words.remove(word.toLowerCase());
	}
	
	public boolean hasCensoredWord(String word) {
		return words.containsKey(word.toLowerCase());
	}
	
	public List<String> getAllCensoredWords() {
		List<String> l = new ArrayList<String>();
		l.addAll(words.keySet());
		return l;
	}
	
	public String getReplacementFor(String word) {
		if(words.containsKey(word.toLowerCase())) {
			String a = words.get(word.toLowerCase());
			if(a.length()==0) {
				for(int i=0;i<word.length();i++)
					a+="*";
			}
			return a;
		}
		return null;
	}
	
	public String censorFunction(String text) {
		String ftext = text.toLowerCase();
		for(String word : getAllCensoredWords())
			if(ftext.contains(word)) {
				text = text.replaceAll("(?i)"+word, getReplacementFor(word));
			}
		return text;
	}
	
	
}
