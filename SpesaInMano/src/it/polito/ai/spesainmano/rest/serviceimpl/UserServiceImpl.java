package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;

import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public void validateRegisterForm(User user) throws CustomBadRequestException {
		
		if(user.getName().equals("") || user.getLastname().equals("")|| user.getEmail().equals("")  || user.getPassword().equals("")){
			
			throw new CustomBadRequestException("Incomplete Information about the user");  
   	   
		}
		
	}
	
	@Override
	public User create(User u) throws  CustomBadRequestException, CustomServiceUnavailableException{
		
		try {
			
			UserDAO userDao = new UserDAOImp();
        	return userDao.insert(u);
		
		} catch (SQLException e) {
		
		   	if(e.getErrorCode() == 1062){
		   	
		   		throw new CustomBadRequestException("There is another user registered with this email");
		   	
		   	}
			System.out.println(e.getMessage());
		   	throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
				
		}
		
	}

	

	@Override
	public int getPoints(int id) throws SQLException {
		UserDAO uDao = new UserDAOImp();
		return uDao.getPoints(id);
	}

	@Override
	public User getUserInfo(int id) throws CustomNotFoundException{
		UserDAO uDao = new UserDAOImp();
		
		try {
			
			User user = uDao.getUser(id);
			
			if(user == null){
				
				
				throw new CustomNotFoundException("The user is not registered");
			}
			
			return user;
			
		} catch (SQLException e) {
		   
			throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
		
		}
		
	
	}



	

}
