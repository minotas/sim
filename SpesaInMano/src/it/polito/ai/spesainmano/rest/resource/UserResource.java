package it.polito.ai.spesainmano.rest.resource;

import java.sql.SQLException;

import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.UserService;
import it.polito.ai.spesainmano.rest.serviceimpl.UserServiceImpl;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserResource {
      
private UserService userService;
      
       @POST
       @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
       @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
       public User create(User user) throws SQLException
       {
    	   if(user.getName().equals("") || user.getLastname().equals("")|| user.getEmail().equals("")  || user.getPassword().equals("")){
    		   throw new CustomBadRequestException("Incomplete Information about the user");  
    	   }
    	   
    	   try {   
    	   userService= new UserServiceImpl();
    	   return userService.create(user); 
    	 
    	   } catch (SQLException e) {
    		   e.getErrorCode();
    		   	if(e.getErrorCode() == 1062){
    		   		throw new CustomBadRequestException("There is another user registered with this email");
    		   	}
    		   	throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
    			
			}
              
       }
       
       @GET
       @Path("/{id}")
       @Produces({MediaType.APPLICATION_JSON})
       public int getPoints(@PathParam("id") String id) throws SQLException
       {
    	   try {   
    	   userService= new UserServiceImpl();
    	   int nid = Integer.parseInt(id);
    	   int points = userService.getPoints(nid);
    	   if(points < 0 ){
    		   throw new CustomNotFoundException("User not found");
    	   }
    	   return points;
    	 
    	   } catch (SQLException e) {
    		   e.getErrorCode();
    		   	if(e.getErrorCode() == 1062){
    		   		throw new CustomBadRequestException("There is another user registered with this email");
    		   	}
    		   	throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
    			
			}
              
       }
       
     
}
