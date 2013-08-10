package it.polito.ai.spesainmano.model;

import java.util.Date;

public class Price {
	
	int id_price;
	User id_user;
	Supermarket id_supermarket;
	Product id_product;
	Date date;
	float price;
	String type;
	
	
	public int getId_price() {
		return id_price;
	}
	
	public void setId_price(int id_price) {
		this.id_price = id_price;
	}
	
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
	
	public Product getId_product() {
		return id_product;
	}
	
	public void setId_product(Product id_product) {
		this.id_product = id_product;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
