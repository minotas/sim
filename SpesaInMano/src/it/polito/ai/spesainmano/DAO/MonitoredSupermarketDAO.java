package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.MonitoredSupermarket;

import java.sql.SQLException;

public interface MonitoredSupermarketDAO {
	
	public MonitoredSupermarket insert(MonitoredSupermarket ms) throws SQLException;

}
