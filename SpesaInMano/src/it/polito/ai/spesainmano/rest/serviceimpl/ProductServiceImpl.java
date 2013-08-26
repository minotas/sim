package it.polito.ai.spesainmano.rest.serviceimpl;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;

import com.sun.jersey.core.util.Base64;

import it.polito.ai.spesainmano.DAO.ProductDAO;
import it.polito.ai.spesainmano.DAO.ProductDAOImp;
import it.polito.ai.spesainmano.DAO.ProductTypeDAO;
import it.polito.ai.spesainmano.DAO.ProductTypeDAOImpl;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.ProductService;

public class ProductServiceImpl implements ProductService {

	@Override
	public void validate(Product p) throws CustomBadRequestException {
		if (p.getName().equals("") || p.getBarcode().equals("")	|| p.getBrand().equals("") || p.getMeasure_unit().equals("")|| p.getQuantity().equals("")) {
			throw new CustomBadRequestException("Incomplete Information about the product");
		}
	}
	
	@Override
	public Product create(Product p) throws CustomServiceUnavailableException,
			CustomBadRequestException {
		ProductTypeDAO ptDao = new ProductTypeDAOImpl();
		try {
			p.getId_product_type().setId_product_type(
					ptDao.getIdByName(p.getId_product_type().getName()));
			ProductDAO productDao = new ProductDAOImp();
			Product product = productDao.insert(p);
			if (!(p.getImage() == null)) {
				DecodeImage(p.getImage(), product.getId_product());
			}
			return product;
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				throw new CustomBadRequestException("This product already exist!");
			} else
				throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
	}

	public void DecodeImage(String encodedString, int id) {

		String filename = String.valueOf(id) + ".jpg";
		String finalfile = "";
		String workingDir = System.getProperty("user.dir");

		finalfile = workingDir + File.separator + filename;
		BufferedImage image = null;
		byte[] imageByte;
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			imageByte = decoder.decodeBuffer(encodedString);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
			image = ImageIO.read(bis);
			bis.close();
			ImageIO.write(image, "jpg", new File(finalfile));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Product getProductByBarcode(String barcode) {

		if (barcode.equals("")) {
			throw new CustomBadRequestException("Please insert a barcode");
		}

		ProductDAO productDao = new ProductDAOImp();
		Product p;

		try {
			p = productDao.getProductByBarcode(barcode);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}

		if (p == null) {
			throw new CustomNotFoundException("There isn't any product with this barcode");
		}
		return p;
	}

	@Override
	public List<Product> getProductByProductType(int productTypeId) {

		ProductDAO productDao = new ProductDAOImp();
		List<Product> products;

		try {

			products = productDao.getProductsByProductType(productTypeId);

		} catch (SQLException e) {

			throw new CustomServiceUnavailableException(e.getMessage());

		}

		return products;
	}

	@Override
	public List<Price> getSimilarProducst(int productId, int supermarketId) {
		ProductDAO productDao = new ProductDAOImp();
		Product product;
		try {
			product = productDao.getProduct(productId);
			List<Price> prices = productDao.getSimilarProductPrices(product, supermarketId);
			if(prices.size() == 0){
				throw new CustomNotFoundException("There isn't any similar product in this supermaket");
			}
			return prices;
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}

		
	}

}
