package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;

import it.polito.ai.spesainmano.DAO.MonitoredProductDAO;
import it.polito.ai.spesainmano.DAO.MonitoredProductDAOImp;
import it.polito.ai.spesainmano.model.MonitoredProduct;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.MonitoredProductService;

public class MonitoredProductServiceImpl implements MonitoredProductService{

	@Override
	public MonitoredProduct insert(MonitoredProduct mp) throws CustomBadRequestException, CustomServiceUnavailableException {
		MonitoredProductDAO monitoredProductDao = new MonitoredProductDAOImp();
		try {
			return monitoredProductDao.insert(mp);
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				throw new CustomBadRequestException("This product is already being monitored!");
			} else
				throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
	}

}
