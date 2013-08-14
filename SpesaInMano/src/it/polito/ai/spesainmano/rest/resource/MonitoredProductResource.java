package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.MonitoredProduct;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.MonitoredProductService;
import it.polito.ai.spesainmano.rest.serviceimpl.MonitoredProductServiceImpl;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/MonitoredProduct")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })


public class MonitoredProductResource {
	
private MonitoredProductService monitoredProductService;
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MonitoredProduct create(MonitoredProduct mp)
    {
    	if(mp.getId_product().getId_product()!=0 || mp.getId_user().getId_user()!=-0){
 		 throw new RuntimeException("Incomplete Information about the product");  
 	   }
 	   
 	   try {   
 	   monitoredProductService= new MonitoredProductServiceImpl();
        return monitoredProductService.insert(mp);
			
 	   } catch (SQLException e) {
 		    if (e.getErrorCode()==1062){
 		    	throw new CustomBadRequestException("This product is already being monitored!"); 
 		    }
 		    else
 		    	throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
           
    }


}
