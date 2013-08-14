package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.MonitoredSupermarketService;
import it.polito.ai.spesainmano.rest.serviceimpl.MonitoredSupermarketServiceImpl;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/MonitoredSupermarket")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })


public class MonitoredSupermarketResource {
	
private MonitoredSupermarketService monitoredSupermarketService;
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MonitoredSupermarket create(MonitoredSupermarket ms)
    {
 	   if(ms.getId_supermarket().getId_supermarket()!=0 || ms.getId_user().getId_user()!=-0){
 		 throw new RuntimeException("Incomplete Information about the supermarket");  
 	   }
 	   
 	   try {   
 	   monitoredSupermarketService= new MonitoredSupermarketServiceImpl();
        return monitoredSupermarketService.insert(ms);
			
 	   } catch (SQLException e) {
 		    if (e.getErrorCode()==1062){
 		    	throw new CustomBadRequestException("This supermarket is already being monitored!"); 
 		    }
 		    else
 		    	throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
           
    }


}
