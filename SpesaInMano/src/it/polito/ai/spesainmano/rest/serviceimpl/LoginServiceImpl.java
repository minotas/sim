package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;


import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.service.LoginService;

public class LoginServiceImpl implements LoginService{

	@Override
	public User login(User u) throws SQLException {
		
		User user;
		UserDAO userDao = new UserDAOImp();
	    user = userDao.login(u.getEmail(), u.getPassword());
	    return user;
	}

}
