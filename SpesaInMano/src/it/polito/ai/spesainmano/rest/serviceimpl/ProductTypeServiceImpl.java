package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.ProductTypeDAO;
import it.polito.ai.spesainmano.DAO.ProductTypeDAOImpl;
import it.polito.ai.spesainmano.model.ProductType;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.service.ProductTypeService;

public class ProductTypeServiceImpl implements ProductTypeService {

	@Override
	public List<ProductType> getProductTypeByCategory(String categoryId) throws NumberFormatException, SQLException  {
		ProductTypeDAO ptDao = new ProductTypeDAOImpl();
		return ptDao.getProductTypesByCategory(Integer.parseInt(categoryId));
		
	}

}
