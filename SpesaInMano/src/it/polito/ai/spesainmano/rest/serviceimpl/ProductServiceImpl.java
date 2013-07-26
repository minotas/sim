package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;

import it.polito.ai.spesainmano.DAO.ProductDAO;
import it.polito.ai.spesainmano.DAO.ProductDAOImp;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.service.ProductService;

public class ProductServiceImpl implements ProductService{

	@Override
	public Product create(Product p) throws SQLException {
		ProductDAO productDao = new ProductDAOImp();
        return productDao.insert(p);
	}

	@Override
	public Product getProduct(String barcode) throws SQLException {
		ProductDAO productDao = new ProductDAOImp();
		return productDao.getProduct(barcode);
	}
	
}
