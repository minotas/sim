package it.polito.ai.spesainmano.rest.resource;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.model.ProductType;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.ProductTypeService;
import it.polito.ai.spesainmano.rest.serviceimpl.ProductTypeServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/product")
public class ProductTypeResource {

	private ProductTypeService pts;
	
	@GET
	@Path("/{categoryId}")
	public List<ProductType> getProductTypesByCategory(@PathParam("categoryId") String categoryId){
	
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
