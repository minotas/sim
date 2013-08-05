package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.ProductType;

import java.sql.SQLException;
import java.util.List;

public interface ProductTypeService {

	public List<ProductType> getProductTypeByCategory(String categoryId) throws SQLException;
}
