package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.PriceService;
import it.polito.ai.spesainmano.rest.serviceimpl.PriceServiceImpl;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/price")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PriceResource {
	
private PriceService priceService;
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Price create(Price p)
    {
 	   if(p.getId_user().getId_user()==0 || p.getId_supermarket().getId_supermarket()==0 || p.getId_product().getId_product()==0  || p.getPrice()==0 ){
 		 throw new RuntimeException("Incomplete Information about the product");  
 	   }
 	   
 	   try {   
 	   priceService= new PriceServiceImpl();
 	   priceService.checkPrice(p);
        return priceService.insert(p);
			
 	   } catch (SQLException e) {
	    	throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
    }
}
