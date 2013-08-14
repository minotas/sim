package it.polito.ai.spesainmano.rest.resource;

import java.util.List;
import java.util.Map;
import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.rest.exception.CustomUnathorizedException;
import it.polito.ai.spesainmano.rest.service.CategoryService;
import it.polito.ai.spesainmano.rest.serviceimpl.CategoryServiceImpl;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/category")
public class CategoryResource {

	private CategoryService categoryService;
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public List<Category> getCategories(@Context HttpHeaders hh) {
	
		Map<String, Cookie> pathParams = hh.getCookies();
	
		if(!pathParams.containsKey("id_user")){
	
			throw new CustomUnathorizedException("The user isn't logged in");
		
		}
		
		categoryService = new CategoryServiceImpl();
		return categoryService.getCategories();
	}
}
