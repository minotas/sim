package it.polito.ai.spesainmano.rest.resource;

import java.sql.SQLException;

import it.polito.ai.spesainmano.model.User;
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
    	 
    	 userService= new UserServiceImpl();
    	 userService.validateRegisterForm(user);
    	 return userService.create(user);    
              
       }
       
       @GET
       @Path("/{id}")
       @Produces({MediaType.APPLICATION_JSON})
       public User getUserInfo(@PathParam("id") int id) {
    	 
    	   userService= new UserServiceImpl();
    	   return userService.getUserInfo(id);
	    	     
       }
       
     
}
