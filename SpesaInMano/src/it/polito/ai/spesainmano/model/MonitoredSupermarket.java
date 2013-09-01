package it.polito.ai.spesainmano.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MonitoredSupermarket {

	User id_user;
	Supermarket id_supermarket;
	
	
	public User getId_user() {
		return id_user;
	}
	
	public void setId_user(User id_user) {
		this.id_user = id_user;
	}
	
	public Supermarket getId_supermarket() {
		return id_supermarket;
	}
	
	public void setId_supermarket(Supermarket id_supermarket) {
		this.id_supermarket = id_supermarket;
	}

}
