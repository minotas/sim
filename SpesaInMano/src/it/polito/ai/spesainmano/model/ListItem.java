package it.polito.ai.spesainmano.model;


public class ListItem {
	
	Product id_product;
	MarketList id_marketlist;
	int quantity;

	
	public Product getId_product() {
		return id_product;
	}
	
	public void setId_product(Product id_product) {
		this.id_product = id_product;
	}
	
	public MarketList getId_marketlist() {
		return id_marketlist;
	}
	
	public void setId_marketlist(MarketList id_marketlist) {
		this.id_marketlist = id_marketlist;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
