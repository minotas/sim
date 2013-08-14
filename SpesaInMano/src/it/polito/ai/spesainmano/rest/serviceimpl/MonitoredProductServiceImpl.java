package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;

import it.polito.ai.spesainmano.DAO.MonitoredProductDAO;
import it.polito.ai.spesainmano.DAO.MonitoredProductDAOImp;
import it.polito.ai.spesainmano.model.MonitoredProduct;
import it.polito.ai.spesainmano.rest.service.MonitoredProductService;

public class MonitoredProductServiceImpl implements MonitoredProductService{

	@Override
	public MonitoredProduct insert(MonitoredProduct mp) throws SQLException {
		MonitoredProductDAO monitoredProductDao = new MonitoredProductDAOImp();
        return monitoredProductDao.insert(mp);
	}

}
