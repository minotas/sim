package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;





import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.LoginService;

public class LoginServiceImpl implements LoginService{

	@Override
	public void validateForm(User user) throws CustomBadRequestException {
		if(user.getEmail().equals("") || user.getPassword().equals("")){
			throw new CustomBadRequestException("Incomplete username or password");
		}
	}
	
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
