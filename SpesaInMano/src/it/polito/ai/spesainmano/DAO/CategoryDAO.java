package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Category;

import java.sql.SQLException;
import java.util.List;

public interface CategoryDAO {

	public List<Category> getCategories() throws SQLException;
}
