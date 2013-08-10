package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductService {

	Product create(Product p) throws SQLException;
	
	Product getProductByBarcode (String barcode) throws SQLException;

	List<Product> getProductByProductType(int productTypeId) throws SQLException;
	
}
