package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.MonitoredProduct;

import java.sql.SQLException;

public interface MonitoredProductDAO {

	public MonitoredProduct insert(MonitoredProduct mp) throws SQLException;
	
}
