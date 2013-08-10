package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.PriceService;
import it.polito.ai.spesainmano.rest.serviceimpl.PriceServiceImpl;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/price")
public class PriceResource {
	
private PriceService priceService;
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<Price> create(Price p)throws CustomBadRequestException{
 	   if(p.getId_user().getId_user()==0 || p.getId_supermarket().getId_supermarket()==0 || p.getId_product().getId_product()==0  || p.getPrice()==0 ){
 		 throw new RuntimeException("Incomplete Information about the product");  
 	   }
 	   
 	   try {   
	 	   priceService= new PriceServiceImpl();
	 	   if(priceService.checkPrice(p)){
	 	   	   return priceService.insert(p);
	 	   }
	 	   else{
	 		   throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
	 	   }
				
	 	} catch (SQLException e) {
	    	throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
    }
}
