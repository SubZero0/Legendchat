package br.com.devpaulo.legendchat.censor;

import java.util.ArrayList;
import java.util.List;

public class CensorManager {
	private List<String> words = new ArrayList<String>();
	public CensorManager() {
	}
	
	public void loadCensoredWords(List<String> l) {
		words.clear();
		for(String word : l)
			words.add(word.toLowerCase());
	}
	
	public void addCensoredWord(String word) {
		if(!words.contains(word.toLowerCase()))
			words.add(word.toLowerCase());
	}
	
	public void removeCensoredWord(String word) {
		if(words.contains(word.toLowerCase()))
			words.remove(word.toLowerCase());
	}
	
	public boolean hasCensoredWord(String word) {
		return words.contains(word.toLowerCase());
	}
	
	public List<String> getAllCensoredWords() {
		List<String> l = new ArrayList<String>();
		l.addAll(words);
		return l;
	}
	
	public String censorFunction(String text) {
		for(String word : getAllCensoredWords())
			if(text.contains(word)) {
				String stars = "";
				for(int i=0;i<word.length();i++)
					stars+="*";
				text = text.replace(word, stars);
			}
		return text;
	}
	
	
}
