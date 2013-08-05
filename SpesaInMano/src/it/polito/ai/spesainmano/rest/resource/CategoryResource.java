package it.polito.ai.spesainmano.rest.resource;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.CategoryService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/category")
public class CategoryResource {

	private CategoryService categoryService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Category> getCategories() {
		try {
			return categoryService.getCategories();
		} catch (SQLException e) {
		 	throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
		}
	}
}
