package it.polito.ai.spesainmano.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SupermarketListPrice {

	int id_supermarket;
	String name;
	float total;
	int products_found;
	float latitude;
	float longitude;
	
	
	
	public int getId_supermarket() {
		return id_supermarket;
	}
	public void setId_supermarket(int id_supermarket) {
		this.id_supermarket = id_supermarket;
	}
	public float getTotal() {
		return total;
	}
	public void setTotal(float total) {
		this.total = total;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getProducts_found() {
		return products_found;
	}
	public void setProducts_found(int products_found) {
		this.products_found = products_found;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

}
