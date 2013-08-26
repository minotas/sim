package it.polito.ai.spesainmano.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class MarketList {
	
	int id_marketlist;
	User id_user;
	Date date;
	
	
	public int getId() {
		return id_marketlist;
	}
	
	public void setId(int id) {
		this.id_marketlist = id;
	}
	
	public User getId_user() {
		return id_user;
	}
	
	public void setId_user(User id_user) {
		this.id_user = id_user;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
}
