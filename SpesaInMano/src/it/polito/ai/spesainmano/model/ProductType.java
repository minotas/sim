package it.polito.ai.spesainmano.model;


public class ProductType {
	
	int id_product_type;
	String name;
	String presentation;
	Category id_category;
	
	
	public int getId_product_type() {
		return id_product_type;
	}
	
	public void setId_product_type(int id_product_type) {
		this.id_product_type = id_product_type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPresentation() {
		return presentation;
	}
	
	public void setPresentation(String presentation) {
		this.presentation = presentation;
	}
	
	public Category getId_category() {
		return id_category;
	}
	
	public void setId_category(Category id_category) {
		this.id_category = id_category;
	}
	
}
