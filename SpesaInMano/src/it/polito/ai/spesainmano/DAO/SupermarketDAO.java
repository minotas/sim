package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Supermarket;

import java.sql.SQLException;
import java.util.List;


public interface SupermarketDAO {

	public List<Supermarket> checkin(float latitude, float longitude) throws SQLException;
	
	public List<Supermarket> getMostVisitedSupermarkets(int userId) throws SQLException;
	
	
}
