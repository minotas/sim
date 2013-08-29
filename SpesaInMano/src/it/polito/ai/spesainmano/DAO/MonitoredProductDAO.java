package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.MonitoredProduct;
import it.polito.ai.spesainmano.model.User;

import java.sql.SQLException;
import java.util.List;

public interface MonitoredProductDAO {

	public MonitoredProduct insert(MonitoredProduct mp) throws SQLException;

	public List<MonitoredProduct> getMonitoredProducts(User u) throws SQLException;

	public void getDeleteMonitoredProducts(List<MonitoredProduct> mpList, User u)throws SQLException;
	
}
