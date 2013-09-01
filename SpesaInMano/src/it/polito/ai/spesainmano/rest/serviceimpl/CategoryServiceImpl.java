package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.CategoryDAO;
import it.polito.ai.spesainmano.DAOImp.CategoryDAOImpl;
import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.CategoryService;

/**
 * Defines the functions related with the categories in the business logic
 * @version 1.0
 */
public class CategoryServiceImpl implements CategoryService {

	/**
	 * Obtains all the categories 
	 * @return A list of categories Objects containing all the information about the categories.
	 * @throws CustomServiceUnavailable Generated when the service is not available
	 */
	@Override
	public List<Category> getCategories() throws CustomServiceUnavailableException{
		
		CategoryDAO cDao = new CategoryDAOImpl();
  	    
		try {
			return cDao.getCategories();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
		}
		
	}

}
