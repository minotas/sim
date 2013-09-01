package it.polito.ai.spesainmano.rest.resource;

import java.util.List;
import java.util.Map;

import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.CategoryService;
import it.polito.ai.spesainmano.rest.serviceimpl.CategoryServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * Receives the requests related to the categories
 * @version 1.0
 */
@Path("/category")
public class CategoryResource {

	private CategoryService categoryService;
	
	/**
	 * Manages the get requests  to get all the categories - Requires user authentication
	 * @return A list of categories Objects containing all the information about the categories.
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public List<Category> getCategories(@Context HttpHeaders hh) throws CustomUnauthorizedException, CustomServiceUnavailableException{
	
		Map<String, Cookie> pathParams = hh.getCookies();
	
		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		categoryService = new CategoryServiceImpl();
		return categoryService.getCategories();
	}
}
