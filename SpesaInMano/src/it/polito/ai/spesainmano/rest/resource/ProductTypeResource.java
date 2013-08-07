package it.polito.ai.spesainmano.rest.resource;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import it.polito.ai.spesainmano.model.ProductType;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
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

@Path("/productType")
public class ProductTypeResource {

	private ProductTypeService pts;
	
	@GET
	@Path("/{categoryId}")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<ProductType> getProductTypesByCategory(@PathParam("categoryId") String categoryId, @Context HttpHeaders hh){
		Map<String, Cookie> pathParams = hh.getCookies();
		if(!pathParams.containsKey("id_user")){
			  throw new CustomUnathorizedException("The user isn't logged in");
		}
			pts = new ProductTypeServiceImpl();
			try {
				return pts.getProductTypeByCategory(categoryId);
			} catch (SQLException e) {
				throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
			}
			catch(NumberFormatException nfe){
				throw new CustomBadRequestException("The category's id is not a number");
			}
		
	}
}
