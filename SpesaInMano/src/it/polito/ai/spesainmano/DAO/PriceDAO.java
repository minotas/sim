package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Price;

import java.sql.SQLException;
import java.util.List;

public interface PriceDAO {

	public int insert(Price p) throws SQLException;
	
	public float[] checkPrice (Price p) throws SQLException;

	public List<Price> getProductPriceInNearSupermarkets(Price p) throws SQLException;
	
}
