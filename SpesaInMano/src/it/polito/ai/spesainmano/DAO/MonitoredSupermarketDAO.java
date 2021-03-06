package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.model.Supermarket;
import java.sql.SQLException;
import java.util.List;

public interface MonitoredSupermarketDAO {
	
	//public MonitoredSupermarket insert(MonitoredSupermarket ms) throws SQLException;

	public List<Supermarket> getMonitoredSupermarkets(int idUser) throws SQLException;

	public List<MonitoredSupermarket> getSupermarkets(float latitude, float longitude, int userId) throws SQLException;

	public void insert(List<MonitoredSupermarket> msList, int idUser) throws SQLException;

	public void delete(List<MonitoredSupermarket> msList, int idUser) throws SQLException;

	public int getMonitoredSupermarketsNumber(int userId) throws SQLException;
	
	
	

}
