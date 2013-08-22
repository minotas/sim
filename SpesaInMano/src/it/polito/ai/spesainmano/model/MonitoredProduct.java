package it.polito.ai.spesainmano.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MonitoredProduct {
	
	User id_user;
	Product id_product;
	
	
	public User getId_user() {
		return id_user;
	}
	
	public void setId_user(User id_user) {
		this.id_user = id_user;
	}
	
	public Product getId_product() {
		return id_product;
	}
	
	public void setId_product(Product id_product) {
		this.id_product = id_product;
	}

}
