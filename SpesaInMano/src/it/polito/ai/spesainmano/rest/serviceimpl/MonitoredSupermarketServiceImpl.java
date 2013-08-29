package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.MonitoredSupermarketDAO;
import it.polito.ai.spesainmano.DAOImp.MonitoredSupermarketDAOImp;
import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.MonitoredSupermarketService;

public class MonitoredSupermarketServiceImpl implements MonitoredSupermarketService{

	@Override
	public MonitoredSupermarket insert(MonitoredSupermarket ms) throws SQLException {
		MonitoredSupermarketDAO monitoredSupermarketDao = new MonitoredSupermarketDAOImp();
        return monitoredSupermarketDao.insert(ms);
	}

	@Override
	public List<MonitoredSupermarket> getSupermarkets(float latitude, float longitude, int userId) {
		MonitoredSupermarketDAO monitoredSupermarketDao = new MonitoredSupermarketDAOImp();
        try {
			return monitoredSupermarketDao.getSupermarkets(latitude, longitude, userId);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
	}

	@Override
	public void insertMonitoredSupermarkets(List<MonitoredSupermarket> msList, int userId) {
		
	}



}
