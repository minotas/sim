package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import java.util.List;

public interface MonitoredSupermarketService {
	
	
	List<MonitoredSupermarket> getSupermarkets(float latitude, float longitude, int userId);

	void insertMonitoredSupermarkets(List<MonitoredSupermarket> msList, int userId);

	void deleteMonitoredSupermarkets(List<MonitoredSupermarket> msList, int userId);

	int getMonitoredSupermarketsNumber(int userId);

}
