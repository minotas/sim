package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Product;

import java.sql.SQLException;

public interface ProductService {

	Product create(Product p) throws SQLException;
	
	Product getProduct (String barcode) throws SQLException;
	
}
