package it.polito.ai.spesainmano.rest.resource;


import java.util.List;
import java.util.Map;

import it.polito.ai.spesainmano.model.ProductType;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.ProductTypeService;
import it.polito.ai.spesainmano.rest.serviceimpl.ProductTypeServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * Receives the requests related to the Product Types
 * @version 1.0
 */
@Path("category/{categoryId}/productType")
public class ProductTypeResource {

	private ProductTypeService productTypeService;

	/**
	 * Manages the get requests about getting all the Product Types of a Category - Requires authentication
	 * 
	 * @param categoryId The id of the Category to obtain its product types
	 * @return a List of Product Types Objects including all the their information.
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	*/
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<ProductType> getProductTypesByCategory(@PathParam("categoryId") int categoryId, @Context HttpHeaders hh){
	
		Map<String, Cookie> pathParams = hh.getCookies();
	
		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		productTypeService = new ProductTypeServiceImpl();
		return productTypeService.getProductTypeByCategory(categoryId);
		
	}
}