package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Category;

import java.sql.SQLException;
import java.util.List;

public interface CategoryService {

	List<Category> getCategories() throws SQLException;
	
}
