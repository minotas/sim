package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnathorizedException;
import it.polito.ai.spesainmano.rest.service.PriceService;
import it.polito.ai.spesainmano.rest.serviceimpl.PriceServiceImpl;
import it.polito.ai.spesainmano.rest.serviceimpl.ProductServiceImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/price")
public class PriceResource {
	
private PriceService priceService;
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<Price> create(Price p)throws CustomBadRequestException{
 	   if(p.getId_user().getId_user()==0 || p.getId_supermarket().getId_supermarket()==0 || p.getId_product().getId_product()==0  || p.getPrice()==0 || p.getType().equals("")){
 		 throw new CustomBadRequestException("Incomplete Information about the price");  
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
    
    @GET
	@Produces({ MediaType.APPLICATION_JSON})
	public float getMaxMinPrices(@QueryParam("productId") int productId, @QueryParam("SupermarketId") int supermarketId, @Context HttpHeaders hh) {
		float averageLastSixMonths;
		Map<String, Cookie> pathParams = hh.getCookies();
		if(!pathParams.containsKey("id_user")){
			  throw new CustomUnathorizedException("The user isn't logged in");
		}
		
		try {
			priceService = new PriceServiceImpl();
			averageLastSixMonths = priceService.getAverageLastSixMonths(productId, supermarketId);
			
			return averageLastSixMonths;

		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}

	}
}
