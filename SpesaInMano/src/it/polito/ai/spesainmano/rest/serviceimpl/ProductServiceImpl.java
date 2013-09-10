package it.polito.ai.spesainmano.rest.serviceimpl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import javax.imageio.ImageIO;
import sun.misc.BASE64Decoder;
import it.polito.ai.spesainmano.DAO.ProductDAO;
import it.polito.ai.spesainmano.DAO.ProductTypeDAO;
import it.polito.ai.spesainmano.DAOImp.ProductDAOImp;
import it.polito.ai.spesainmano.DAOImp.ProductTypeDAOImpl;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.ProductService;

/**
 * Defines the functions related with the products in the business logic
 * @version 1.0
 */
public class ProductServiceImpl implements ProductService {

	/**
	 * Validates the information received to create a new product
	 * @param product An object containing the information of the new product
	 * @throws CustomBadRequestException Generated when the information received is not valid or the product has been added
	 * 		   before
	 */
	@Override
	public void validate(Product p) throws CustomBadRequestException {
		if(p.getName() == null || p.getName().equals("") || p.getBarcode() == null || p.getBarcode().equals("") || p.getMeasure_unit() == null ||
				p.getMeasure_unit().equals("") || p.getQuantity() == null || p.getQuantity().equals("")|| p.getBrand() == null || p.getBrand().equals("")){
			throw new CustomBadRequestException("Incomplete Information about the product");
		}

		String eanCode = p.getBarcode();
		String ValidChars = "0123456789";
		char digit;
		for (int i = 0; i < eanCode.length(); i++) { 
			digit = eanCode.charAt(i); 
			if (ValidChars.indexOf(digit) == -1) {
				throw new CustomBadRequestException("Invalid Barcode");

			}
		}

		// Add five 0 if the code has only 8 digits
		if (eanCode.length() == 8 ) {
			eanCode = "00000" + eanCode;
		}
		// Check for 13 digits otherwise
		else if (eanCode.length() != 13) {
			throw new CustomBadRequestException("Invalid Barcode");
		}

		// Get the check number
		int originalCheck = Integer.parseInt(eanCode.substring(eanCode.length() - 1));
		eanCode = eanCode.substring(0, eanCode.length() - 1);

		// Add even numbers together
		int even = Integer.parseInt((eanCode.substring(1, 2))) + 
				Integer.parseInt((eanCode.substring(3, 4)))  + 
				Integer.parseInt((eanCode.substring(5, 6)))  + 
				Integer.parseInt((eanCode.substring(7, 8)))  + 
				Integer.parseInt((eanCode.substring(9, 10)))  + 
				Integer.parseInt((eanCode.substring(11, 12))) ;
		// Multiply this result by 3
		even *= 3;

		// Add odd numbers together
		int odd = Integer.parseInt((eanCode.substring(0, 1)))  + 
				Integer.parseInt((eanCode.substring(2, 3)))  + 
				Integer.parseInt((eanCode.substring(4, 5)))  + 
				Integer.parseInt((eanCode.substring(6, 7)))  + 
				Integer.parseInt((eanCode.substring(8, 9)))  + 
				Integer.parseInt((eanCode.substring(10, 11))) ;

		// Add two totals together
		int total = even + odd;

		// Calculate the checksum
		// Divide total by 10 and store the remainder
		int checksum = total % 10;
		// If result is not 0 then take away 10
		if (checksum != 0) {
			checksum = 10 - checksum;
		}

		// Return the result
		if (checksum != originalCheck) {
			throw new CustomBadRequestException("Invalid Barcode");
		}

		if(Float.parseFloat(p.getQuantity()) <= 0){
			throw new CustomBadRequestException("Invalid Quantity");
		}


	}

	/**
	 * Implements the logic required to create a new product
	 * @param product An object containing the information of the new product
	 * @return a Product object containing the information of the corresponding product, including the id assigned
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomBadRequestException Generated when the information received is not valid or the product has been added
	 * 		   before
	 */
	@Override
	public Product create(Product p) throws CustomServiceUnavailableException, CustomBadRequestException {
		ProductTypeDAO ptDao = new ProductTypeDAOImpl();
		try {
			p.getId_product_type().setId_product_type(
					ptDao.getIdByName(p.getId_product_type().getName()));
			ProductDAO productDao = new ProductDAOImp();
			Product product = productDao.insert(p);
			if (!(p.getImage() == null)) {
				DecodeImage(p.getImage(), product.getId_product());
			}
			else{
				copyDefaultImage(product.getId_product());
			}
			return product;
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				throw new CustomBadRequestException("This product already exist!");
			} else
				throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}

	/**
	 * Copies the default image when the product does not contain one
	 * @param id The id of the new product, used to assign the name of the copy
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 */
	private void copyDefaultImage(int id) throws CustomServiceUnavailableException {

		InputStream inStream = null;
		OutputStream outStream = null;

		try{

			File afile =new File("undefined.jpg");
			File bfile =new File( String.valueOf(id) + ".jpg");
			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);
			byte[] buffer = new byte[1024];
			int length;
		
			while ((length = inStream.read(buffer)) > 0){
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();
		}catch(IOException e){
			throw new CustomServiceUnavailableException("Internal error in the application");
		}

	}

	/**
	 * Decodes the image received  as a String in the creation request
	 * @param encodedString The string cointaining the image encoded in base64
	 * @param id The id of the new product, used to assign the name of the copy
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 */		
	public void DecodeImage(String encodedString, int id) throws CustomServiceUnavailableException {

		String filename = String.valueOf(id) + ".jpg";
		String finalfile = "";
		finalfile = filename;
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
			throw new CustomServiceUnavailableException("Internal error in the application");
		}

	}

	/**
	 * Implements the logic required to found a product by its barcode
	 * @param barcode The barcode of the product
	 * @return a Product object containing the information of the corresponding product
	 * @throws CustomBadRequestException Generated when the barcode is empty
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when there isn't any product with this barcode
	 */
	@Override
	public Product getProductByBarcode(String barcode) throws CustomBadRequestException, CustomServiceUnavailableException, CustomNotFoundException{

		if (barcode.equals("")) {
			throw new CustomBadRequestException("Please insert a barcode");
		}

		ProductDAO productDao = new ProductDAOImp();
		Product p;

		try {
			p = productDao.getProductByBarcode(barcode);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service unavailable");
		}

		if (p == null) {
			throw new CustomNotFoundException("There isn't any product with this barcode");
		}
		return p;
	}

	/**
	 * Implements the logic required to found the products that belong to a product type
	 * @param productTypeId
	 * @return a List of Product objects containing the information of the products
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 */
	@Override
	public List<Product> getProductByProductType(int productTypeId) throws CustomServiceUnavailableException{

		ProductDAO productDao = new ProductDAOImp();
		List<Product> products;

		try {
			products = productDao.getProductsByProductType(productTypeId);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException(e.getMessage());
		}

		return products;
	}

	
	/**
	 * Implements the logic required to found the similar product in a specific supermarket of the given product
	 * @param productId The id of the product
	 * @param supermarketId The id of the supermarket in which will be done the search 
	 * @return a List of Price objects containing the information of the similar products in the supermarket
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when the productId received does not corresponds to any product 
	 * 		   registered or when there aren't similar product in the supermarket
	 */
	@Override
	public List<Price> getSimilarProducst(int productId, int supermarketId)  throws CustomNotFoundException, CustomServiceUnavailableException{
		ProductDAO productDao = new ProductDAOImp();
		Product product;
		
		try {
			product = productDao.getProduct(productId);
			
			if(product == null){
				throw new CustomNotFoundException("Product not found");
			}
			List<Price> prices = productDao.getSimilarProductPrices(product, supermarketId);
			
			if(prices.size() == 0){
				throw new CustomNotFoundException("There isn't any similar product in this supermaket");
			}
			return prices;
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}
}