package it.polito.ai.spesainmano.rest.resource;

import java.sql.SQLException;

import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.User;
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
       @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
       @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
       public User create(User user) throws SQLException
       {
    	   if(user.getName().equals("") || user.getLastname().equals("")|| user.getEmail().equals("")  || user.getPassword().equals("")){
    		 throw new RuntimeException("Incomplete Information about the user");  
    	   }
    	   
    	   UserDAO uDao = new UserDAOImp();
    	
    	   
    	   if(uDao.checkEmail(user.getEmail())){
    		   
    		   
    	   }
    	   try {   
    	   userService= new UserServiceImpl();
           return userService.create(user);
			
    	   } catch (SQLException e) {
    		    throw e;
			}
              
       }
       
     
}
