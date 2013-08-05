package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnathorizedException;
import it.polito.ai.spesainmano.rest.service.ProductService;
import it.polito.ai.spesainmano.rest.serviceimpl.ProductServiceImpl;

import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/product")
@Produces({ MediaType.APPLICATION_JSON })
public class ProductResource {

	private ProductService productService;

	@POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Product create(Product p, @Context HttpHeaders hh)
    {
		Map<String, Cookie> pathParams = hh.getCookies();
		if(!pathParams.containsKey("name")){
			  throw new CustomUnathorizedException("The user isn't logged in");
		}
       
 	   if(p.getName().equals("") || p.getBarcode().equals("")|| p.getBrand().equals("")  || p.getMeasure_unit().equals("") || p.getQuantity().equals("") || p.getImage().equals("")){
 		 throw new RuntimeException("Incomplete Information about the product");  
 	   }
 	   
 	   try {   
 	   productService= new ProductServiceImpl();
       return productService.create(p);
			
 	   } catch (SQLException e) {
 		    if (e.getErrorCode() == 1062){
 		    	throw new CustomBadRequestException("This product already exist!"); 
 		    }
 		    else
 		    	throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
           
    }

	@GET
	@Path("/{barcode}")
	@Produces({ MediaType.APPLICATION_JSON})
	public Product getProductByBarcode(@PathParam("barcode") String barcode, @Context HttpHeaders hh) {
		
		Map<String, Cookie> pathParams = hh.getCookies();
		if(!pathParams.containsKey("name")){
			  throw new CustomUnathorizedException("The user isn't logged in");
		}
		
		if (barcode.equals("")) {
			throw new RuntimeException("Please insert a barcode");
		}

		try {
			productService = new ProductServiceImpl();
			Product p = productService.getProductByBarcode(barcode);
			if (p == null) {
				throw new CustomNotFoundException("There isn't any product with this barcode");
			}
			return p;

		} catch (SQLException e) {
			throw new CustomServiceUnavailableException(
					"Server received an invalid response from upstream server");
		}

	}

}
