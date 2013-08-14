package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.MonitoredSupermarket;

import java.sql.SQLException;

public interface MonitoredSupermarketService {
	
	MonitoredSupermarket insert(MonitoredSupermarket ms) throws SQLException;

}
