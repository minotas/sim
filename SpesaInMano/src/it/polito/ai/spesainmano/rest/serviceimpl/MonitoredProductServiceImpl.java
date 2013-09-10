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

/**
 * Defines the functions related with the MonitoredProduct in the business logic
 * @version 1.0
 */
public class MonitoredProductServiceImpl implements MonitoredProductService{

	/**
	 * Implements the logic required to add a new monitored Product
	 * @param mp A mpObject containing the information required to add the new monitored product
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomBadRequestException Generated when the product is already monitored by the user
	 */
	@Override
	public MonitoredProduct insert(MonitoredProduct mp) throws CustomBadRequestException, CustomServiceUnavailableException {
		MonitoredProductDAO monitoredProductDao = new MonitoredProductDAOImp();
		try {
			return monitoredProductDao.insert(mp);
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				throw new CustomBadRequestException("This product is already being monitored!");
			} else
				throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}

	/**
	 * Implements the logic required to delete a set of Monitored Products
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 */
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
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
		
	}

	/**
	 * Implements the logic required to obtain the Monitored Product by an user
	 * @return A list of MonitoredProducts objects 
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomBadRequestException Generated when the product is already monitored by the user
	 */
	@Override
	public void deleteMonitoredProducts(List<MonitoredProduct> mpList, User u) {
		MonitoredProductDAO monitoredProductDao = new MonitoredProductDAOImp();
		try {
			monitoredProductDao.deleteMonitoredProducts(mpList, u);
			return;
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
		
	}

}
