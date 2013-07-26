package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Price;

import java.sql.SQLException;

public interface PriceDAO {

	public Price insert(Price p) throws SQLException;
	
	public boolean checkPrice (Price p) throws SQLException;
	
}
