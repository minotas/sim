package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.ProductType;

import java.sql.SQLException;
import java.util.List;

public interface ProductTypeDAO {
	
	public List<ProductType> getProductTypesByCategory(int categoryId) throws SQLException;

	public int getIdByName(String name) throws SQLException; 

}
