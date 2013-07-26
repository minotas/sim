package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Product;

import java.sql.SQLException;


public interface ProductDAO {

	public Product insert(Product p) throws SQLException;
	
	public Product getProduct(String barcode) throws SQLException;
	
}
