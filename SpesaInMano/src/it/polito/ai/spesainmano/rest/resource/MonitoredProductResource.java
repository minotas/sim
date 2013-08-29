package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.MonitoredProduct;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomUnathorizedException;
import it.polito.ai.spesainmano.rest.service.MonitoredProductService;
import it.polito.ai.spesainmano.rest.serviceimpl.MonitoredProductServiceImpl;

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

@Path("/monitoredProduct")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })


public class MonitoredProductResource {
	
private MonitoredProductService monitoredProductService;
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MonitoredProduct create(MonitoredProduct mp, @Context HttpHeaders hh)
    {
    	Map<String, Cookie> pathParams = hh.getCookies();
    	
		if(!pathParams.containsKey("id_user")){
	
			throw new CustomUnathorizedException("The user isn't logged in");
		
		}
		
		MonitoredProduct monP = mp;
		User u = new User();
		u.setId_user(Integer.parseInt(pathParams.get("id_user").getValue()));
		monP.setId_user(u); 
 	    monitoredProductService= new MonitoredProductServiceImpl();
        return monitoredProductService.insert(mp);
		    
    }
    
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<MonitoredProduct> getMonitoredProducts(@Context HttpHeaders hh)
    {
    	Map<String, Cookie> pathParams = hh.getCookies();
    	
		if(!pathParams.containsKey("id_user")){
	
			throw new CustomUnathorizedException("The user isn't logged in");
		
		}
		
		User u = new User();
		u.setId_user(Integer.parseInt(pathParams.get("id_user").getValue()));
		monitoredProductService= new MonitoredProductServiceImpl();
        return monitoredProductService.getMonitoredProducts(u);
		    
    }
    
    @Path("/delete")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void deleteMonitoredProducts(List<MonitoredProduct> mpList,  @Context HttpHeaders hh)
    {
    	Map<String, Cookie> pathParams = hh.getCookies();
    	
		if(!pathParams.containsKey("id_user")){
	
			throw new CustomUnathorizedException("The user isn't logged in");
		
		}
		
		User u = new User();
		u.setId_user(Integer.parseInt(pathParams.get("id_user").getValue()));
		monitoredProductService= new MonitoredProductServiceImpl();
        monitoredProductService.deleteMonitoredProducts(mpList, u);
        return;
		    
    }
    
    


}
