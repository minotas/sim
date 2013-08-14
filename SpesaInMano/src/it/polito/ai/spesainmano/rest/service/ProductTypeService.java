package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.ProductType;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import java.util.List;

public interface ProductTypeService {

	public List<ProductType> getProductTypeByCategory(int categoryId) throws CustomServiceUnavailableException;
}
