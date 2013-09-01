package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAOImp.UserDAOImp;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.LoginService;

/**
 * Defines the functions related with the login process
 * @version 1.0
 */
public class LoginServiceImpl implements LoginService{

	/** 
	 * Validates the user information required to do the login
	 * @param user An user object containing the user information to do the login
	 * @throws CustomBadRequestException Generated when the email or password fields are empty
	*/
	@Override
	public void validateForm(User user) throws CustomBadRequestException {
		if(user.getEmail().equals("") || user.getPassword().equals("")){
			throw new CustomBadRequestException("Incomplete username or password");
		}
	}
	
	/** 
	 * Implements the registration logical process 
	 * @param user An user object containing the user information to do the login
	 * @return an user object containing the information when the login success
	 * @throws CustomNotFoundException Generated when the user is not registered or the password is wrong
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 */
	@Override
	public User login(User u) throws CustomNotFoundException, CustomServiceUnavailableException{
		
		User user;
		UserDAO userDao = new UserDAOImp();
	   
		try {
	    	user = userDao.login(u.getEmail(), u.getPassword());
		} catch (SQLException e) {
		  	throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
	    
	    if (user == null) {
	    	throw new CustomNotFoundException("Wrong username or password");
		}
	    
	    return user;
	}

}
