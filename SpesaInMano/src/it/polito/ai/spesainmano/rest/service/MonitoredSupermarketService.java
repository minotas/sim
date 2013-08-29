package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.model.Supermarket;

import java.sql.SQLException;
import java.util.List;

public interface MonitoredSupermarketService {
	
	MonitoredSupermarket insert(MonitoredSupermarket ms) throws SQLException;

	List<MonitoredSupermarket> getSupermarkets(float latitude, float longitude, int userId);

	void insertMonitoredSupermarkets(List<MonitoredSupermarket> msList, int userId);

}
