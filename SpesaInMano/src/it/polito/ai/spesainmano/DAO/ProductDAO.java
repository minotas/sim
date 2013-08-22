package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;

import java.sql.SQLException;
import java.util.List;


public interface ProductDAO {

	public Product insert(Product p) throws SQLException;
	
	public Product getProductByBarcode(String barcode) throws SQLException;

	public List<Product> getProductsByProductType(int productTypeId) throws SQLException;

	public Product getProduct(int productId) throws SQLException;

	public List<Price> getSimilarProductPrices(Product product, int supermarketId) throws SQLException;
	
}
