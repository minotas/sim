package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;
import it.polito.ai.spesainmano.DAO.ProductDAO;
import it.polito.ai.spesainmano.DAO.ProductDAOImp;
import it.polito.ai.spesainmano.DAO.ProductTypeDAO;
import it.polito.ai.spesainmano.DAO.ProductTypeDAOImpl;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.service.ProductService;

public class ProductServiceImpl implements ProductService{

	@Override
	public Product create(Product p) throws SQLException {
		ProductTypeDAO ptDao = new ProductTypeDAOImpl();
		p.getId_product_type().setId_product_type(ptDao.getIdByName(p.getId_product_type().getName()));
		ProductDAO productDao = new ProductDAOImp();
        return productDao.insert(p);
	}

	@Override
	public Product getProductByBarcode(String barcode) throws SQLException {
		ProductDAO productDao = new ProductDAOImp();
		return productDao.getProductByBarcode(barcode);
	}

	@Override
	public List<Product> getProductByProductType(int productTypeId) throws SQLException {
		ProductDAO productDao = new ProductDAOImp();
		return productDao.getProductsByProductType(productTypeId);
	}
	
}
