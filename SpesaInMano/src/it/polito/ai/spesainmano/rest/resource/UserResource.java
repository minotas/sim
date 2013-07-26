package it.polito.ai.spesainmano.rest.resource;

import java.sql.SQLException;

import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.LoginService;
import it.polito.ai.spesainmano.rest.service.UserService;
import it.polito.ai.spesainmano.rest.serviceimpl.LoginServiceImpl;
import it.polito.ai.spesainmano.rest.serviceimpl.UserServiceImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserResource {
      
private UserService userService;
      
       @POST
       public User create(User user) throws SQLException
       {
    	   if(user.getName().equals("") || user.getLastname().equals("")|| user.getEmail().equals("")  || user.getPassword().equals("")){
    		   throw new RuntimeException("Incomplete Information about the user");  
    	   }
    	   
    	   try {   
    	   userService= new UserServiceImpl();
    	   if(userService.checkEmail(user)){
    		   return userService.create(user); 
    	   }
    	   else{
    		   throw new CustomBadRequestException("There is another user registered with this email");
    	   }
          
    	   } catch (SQLException e) {
    			throw new CustomServiceUnavailableException(e.getMessage());
			}
              
       }
       
     
}