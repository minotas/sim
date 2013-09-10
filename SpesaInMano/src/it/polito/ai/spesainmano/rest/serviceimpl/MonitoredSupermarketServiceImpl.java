package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;
import it.polito.ai.spesainmano.DAO.MonitoredSupermarketDAO;
import it.polito.ai.spesainmano.DAOImp.MonitoredSupermarketDAOImp;
import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.MonitoredSupermarketService;

public class MonitoredSupermarketServiceImpl implements MonitoredSupermarketService{

	/**
	 * Implements the logic required to obtain the near supermarkets and an indication about if each supermarket is monitored or not
	 * @param msList A list of MonitoredSupermarket Object to be added containing all the required information
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 */
	public List<MonitoredSupermarket> getSupermarkets(float latitude, float longitude, int userId) throws CustomServiceUnavailableException{
		MonitoredSupermarketDAO monitoredSupermarketDao = new MonitoredSupermarketDAOImp();
        try {
			return monitoredSupermarketDao.getSupermarkets(latitude, longitude, userId);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}

	/**
	 * Implements the logic required to add a set of monitored supermarkets by an user
	 * @param msList A list of MonitoredSupermarket Object to be added containing all the required information
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 */
	@Override
	public void insertMonitoredSupermarkets(List<MonitoredSupermarket> msList, int userId) throws CustomServiceUnavailableException{
		MonitoredSupermarketDAO monitoredSupermarketDao = new MonitoredSupermarketDAOImp();
		try {
			monitoredSupermarketDao.insert(msList, userId);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
		return;
	}

	/**
	 * Implements the logic required to delete a set of monitored supermarkets by an user
	 * @param msList A list of MonitoredSupermarket Object to be deleted containing all the required information
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 */
	@Override
	public void deleteMonitoredSupermarkets(List<MonitoredSupermarket> msList, int userId) throws CustomServiceUnavailableException {
		MonitoredSupermarketDAO monitoredSupermarketDao = new MonitoredSupermarketDAOImp();
        try {
			monitoredSupermarketDao.delete(msList, userId);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
        return;
		
	}

	/**
	 * Implements the logic required to get the number of monitored supermarkets by an user
	 * @return The number of monitored Supermarkets
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	*/	
	@Override
	public int getMonitoredSupermarketsNumber(int userId) throws CustomServiceUnavailableException{
		MonitoredSupermarketDAO monitoredSupermarketDao = new MonitoredSupermarketDAOImp();
        try {
			return monitoredSupermarketDao.getMonitoredSupermarketsNumber(userId);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}

}
