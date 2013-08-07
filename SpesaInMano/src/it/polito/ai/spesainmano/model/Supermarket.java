package it.polito.ai.spesainmano.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Supermarket {

	int id_supermarket;
	String name;
	float longitude;
	float latitude;
	
	
	public int getId_supermarket() {
		return id_supermarket;
	}
	
	public void setId_supermarket(int id_supermarket) {
		this.id_supermarket = id_supermarket;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public float getLongitude() {
		return longitude;
	}
	
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	public float getLatitude() {
		return latitude;
	}
	
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	
}
