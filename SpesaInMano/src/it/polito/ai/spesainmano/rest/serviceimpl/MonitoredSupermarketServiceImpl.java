package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;

import it.polito.ai.spesainmano.DAO.MonitoredSupermarketDAO;
import it.polito.ai.spesainmano.DAO.MonitoredSupermarketDAOImp;
import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.rest.service.MonitoredSupermarketService;

public class MonitoredSupermarketServiceImpl implements MonitoredSupermarketService{

	@Override
	public MonitoredSupermarket insert(MonitoredSupermarket ms) throws SQLException {
		MonitoredSupermarketDAO monitoredSupermarketDao = new MonitoredSupermarketDAOImp();
        return monitoredSupermarketDao.insert(ms);
	}

}
