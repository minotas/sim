package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.CategoryDAO;
import it.polito.ai.spesainmano.DAO.CategoryDAOImpl;
import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.rest.service.CategoryService;

public class CategoryServiceImpl implements CategoryService {

	@Override
	public List<Category> getCategories() throws SQLException {
		CategoryDAO cDao = new CategoryDAOImpl();
  	    return cDao.getCategories();
	}

}
