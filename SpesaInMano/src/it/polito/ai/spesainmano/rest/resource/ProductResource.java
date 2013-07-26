package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.ProductService;
import it.polito.ai.spesainmano.rest.serviceimpl.ProductServiceImpl;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/product")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ProductResource {
	
	private ProductService productService;
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Product create(Product p)
    {
 	   if(p.getName().equals("") || p.getBarcode().equals("")|| p.getBrand().equals("")  || p.getMeasure_unit().equals("") || p.getQuantity().equals("") || p.getImage().equals("")){
 		 throw new RuntimeException("Incomplete Information about the product");  
 	   }
 	   
 	   try {   
 	   productService= new ProductServiceImpl();
        return productService.create(p);
			
 	   } catch (SQLException e) {
 		    if (e.getErrorCode()==1062){
 		    	throw new CustomBadRequestException("This product already exist!"); 
 		    }
 		    else
 		    	throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
           
    }

}
