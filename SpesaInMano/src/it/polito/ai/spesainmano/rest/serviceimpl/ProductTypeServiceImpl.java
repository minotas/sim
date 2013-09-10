package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;
import it.polito.ai.spesainmano.DAO.ProductTypeDAO;
import it.polito.ai.spesainmano.DAOImp.ProductTypeDAOImpl;
import it.polito.ai.spesainmano.model.ProductType;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.ProductTypeService;

/**
 * Defines the functions related with the Product Types in the business logic
 * @version 1.0
 */
public class ProductTypeServiceImpl implements ProductTypeService {

	/**
	 * Obtains all the product types of a given Category
	 * @param categoryId The id of the category
	 * @return A list of ProductType Objects containing all the information about the Product types
	 * @throws CustomServiceUnavailable Generated when the service is not available
	 */
	@Override
	public List<ProductType> getProductTypeByCategory(int categoryId) throws CustomServiceUnavailableException  {
		
		ProductTypeDAO ptDao = new ProductTypeDAOImpl();
	
		try {
			return ptDao.getProductTypesByCategory(categoryId);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}
}