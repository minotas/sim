package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAOImp.UserDAOImp;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.UserService;

/**
 * Defines the functions related with the users in the business logic
 * @version 1.0
 */
public class UserServiceImpl implements UserService {

	/** 
	 * Validates the user information required to do the registration
	 * @param user An user object containing the user information to do the login
	 * @throws CustomBadRequestException Generated when the one or more fields of the user are empty or are invalid
	*/
	@Override
	public void validateRegisterForm(User user) throws CustomBadRequestException {
		
		if(user.getName().equals("") || user.getLastname().equals("")|| user.getEmail().equals("")  || user.getPassword().equals("")){
			throw new CustomBadRequestException("Incomplete Information about the user");  
   	   	}
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		String NAME_PATTERN = "^[a-zA-Z ]+$";
		
		Pattern patternName, patternEmail;
		Matcher matcherName, matcherEmail;
		
		patternName = Pattern.compile(NAME_PATTERN);
		matcherName = patternName.matcher(user.getName());
		
		if (!matcherName.matches()) {
			throw new CustomBadRequestException("Invalid user name");
		}
		
		matcherName = patternName.matcher(user.getLastname());
		if (!matcherName.matches()) {
			throw new CustomBadRequestException("Invalid user lastname");
		}

		patternEmail = Pattern.compile(EMAIL_PATTERN);
		matcherEmail = patternEmail.matcher(user.getEmail());
		if (!matcherEmail.matches()) {
			throw new CustomBadRequestException("Invalid user email");
		}
		
		if(user.getPassword().length() < 7){
			throw new CustomBadRequestException("Invalid user password, it must have at least 7 characters");
		}		
	}
	
	
	/**
	 * Implements the registration logical process
	 * @param user An user object containing the user information to do the registration
	 * @return an user object containing the information of the user, including the id assigned.
	 * @throws CustomBadRequestException Generated when the user is already registered
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 */
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

	/*
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
	
	 */

}
