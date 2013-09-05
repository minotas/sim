package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import java.util.List;

public interface ProductService {

	void validate(Product p) throws CustomBadRequestException;
	
	Product create(Product p) throws CustomServiceUnavailableException, CustomBadRequestException;
	
	Product getProductByBarcode (String barcode) throws CustomBadRequestException, CustomNotFoundException, CustomServiceUnavailableException;

	List<Product> getProductByProductType(int productTypeId) throws CustomNotFoundException, CustomServiceUnavailableException;

	List<Price> getSimilarProducst(int productId, int supermarketId);

	
}
