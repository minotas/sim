package it.polito.ai.spesainmano.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InsertPriceResponse {

	private int user_score;
	private int price_qualifier;
	private int id_product;
	
	public int getUser_score() {
		return user_score;
	}
	
	public void setUser_score(int user_score) {
		this.user_score = user_score;
	}
	
	public int getPrice_qualifier() {
		return price_qualifier;
	}
	
	public void setPrice_qualifier(int price_qualifier) {
		this.price_qualifier = price_qualifier;
	}

	public int getId_product() {
		return id_product;
	}

	public void setId_product(int id_product) {
		this.id_product = id_product;
	}
	
	
}
