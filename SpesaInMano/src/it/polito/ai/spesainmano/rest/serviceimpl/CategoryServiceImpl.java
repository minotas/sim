package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.CategoryDAO;
import it.polito.ai.spesainmano.DAO.CategoryDAOImpl;
import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.CategoryService;

public class CategoryServiceImpl implements CategoryService {

	@Override
	public List<Category> getCategories() throws CustomServiceUnavailableException{
		
		CategoryDAO cDao = new CategoryDAOImpl();
  	    
		try {
		
			return cDao.getCategories();
		
		} catch (SQLException e) {
		
			throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
		
		}
	}

}
