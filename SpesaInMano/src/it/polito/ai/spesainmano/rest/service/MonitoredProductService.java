package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.MonitoredProduct;
import it.polito.ai.spesainmano.model.User;
import java.util.List;

public interface MonitoredProductService {
	
	MonitoredProduct insert(MonitoredProduct mp);

	List<MonitoredProduct> getMonitoredProducts(User u);

	void deleteMonitoredProducts(List<MonitoredProduct> mpList, User u);

}
