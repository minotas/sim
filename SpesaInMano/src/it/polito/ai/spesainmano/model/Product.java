package it.polito.ai.spesainmano.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Product {
	
	int id_product;
	String name;
	String barcode;
	String brand;
	String quantity;
	String measure_unit;
	ProductType id_product_type;
	String image;
	
	
	public int getId_product() {
		return id_product;
	}
	
	public void setId_product(int id_product) {
		this.id_product = id_product;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBarcode() {
		return barcode;
	}
	
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	public String getBrand() {
		return brand;
	}
	
	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	public String getQuantity() {
		return quantity;
	}
	
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	public String getMeasure_unit() {
		return measure_unit;
	}
	
	public void setMeasure_unit(String measure_unit) {
		this.measure_unit = measure_unit;
	}
	
	public ProductType getId_product_type() {
		return id_product_type;
	}
	
	public void setId_product_type(ProductType id_product_type) {
		this.id_product_type = id_product_type;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
}
