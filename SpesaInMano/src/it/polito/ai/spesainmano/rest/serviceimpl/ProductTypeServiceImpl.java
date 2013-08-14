package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.ProductTypeDAO;
import it.polito.ai.spesainmano.DAO.ProductTypeDAOImpl;
import it.polito.ai.spesainmano.model.ProductType;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.ProductTypeService;

public class ProductTypeServiceImpl implements ProductTypeService {

	@Override
	public List<ProductType> getProductTypeByCategory(int categoryId) throws CustomServiceUnavailableException  {
		
		ProductTypeDAO ptDao = new ProductTypeDAOImpl();
	
		try {
		
			return ptDao.getProductTypesByCategory(categoryId);
		
		} catch (SQLException e) {
		
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
			
		}
		
	}

}
