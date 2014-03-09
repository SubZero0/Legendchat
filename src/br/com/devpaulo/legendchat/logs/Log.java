package br.com.devpaulo.legendchat.logs;

import java.util.Date;

public class Log {
	private Date date = null;
	private String msg = null;
	public Log(Date d, String m) {
		date=d;
		msg=m;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public void setDate(Date d) {
		date=d;
	}
	
	public void setMessage(String m) {
		msg=m;
	}
}
