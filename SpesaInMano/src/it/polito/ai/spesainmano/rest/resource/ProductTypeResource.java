package it.polito.ai.spesainmano.rest.resource;


import java.util.List;
import java.util.Map;
import it.polito.ai.spesainmano.model.ProductType;
import it.polito.ai.spesainmano.rest.exception.CustomUnathorizedException;
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

@Path("category/{categoryId}/productType")
public class ProductTypeResource {

	private ProductTypeService productTypeService;
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<ProductType> getProductTypesByCategory(@PathParam("categoryId") int categoryId, @Context HttpHeaders hh){
	
		Map<String, Cookie> pathParams = hh.getCookies();
	
		if(!pathParams.containsKey("id_user")){
	
			throw new CustomUnathorizedException("The user isn't logged in");
	
		}
		
		productTypeService = new ProductTypeServiceImpl();
		return productTypeService.getProductTypeByCategory(categoryId);
		
	
	}
}
