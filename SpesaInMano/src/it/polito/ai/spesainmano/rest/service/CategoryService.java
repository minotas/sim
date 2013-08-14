package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import java.util.List;

public interface CategoryService {

	List<Category> getCategories()throws CustomServiceUnavailableException ;
	
}
