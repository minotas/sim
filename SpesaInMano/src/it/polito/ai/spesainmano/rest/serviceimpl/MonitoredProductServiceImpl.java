package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.MonitoredProductDAO;
import it.polito.ai.spesainmano.DAOImp.MonitoredProductDAOImp;
import it.polito.ai.spesainmano.model.MonitoredProduct;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
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

	@Override
	public List<MonitoredProduct> getMonitoredProducts(User u) {
		MonitoredProductDAO monitoredProductDao = new MonitoredProductDAOImp();
		List<MonitoredProduct> monitoredProducts;
		try {
			monitoredProducts = monitoredProductDao.getMonitoredProducts(u);
			if(monitoredProducts.size() == 0){
				throw new CustomNotFoundException("You don't monitor any product");
			}
			else return monitoredProducts;
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
		
	}

	@Override
	public void deleteMonitoredProducts(List<MonitoredProduct> mpList, User u) {
		MonitoredProductDAO monitoredProductDao = new MonitoredProductDAOImp();
		try {
			monitoredProductDao.deleteMonitoredProducts(mpList, u);
			return;
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
		
	}

}
