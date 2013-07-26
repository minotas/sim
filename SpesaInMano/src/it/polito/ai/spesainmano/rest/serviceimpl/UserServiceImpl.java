package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;

import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.service.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public User create(User u) throws SQLException {
		UserDAO userDao = new UserDAOImp();
        return userDao.insert(u);
	}

	@Override
	public boolean checkEmail(User user) throws SQLException {
		UserDAO uDao = new UserDAOImp();
  	    return uDao.checkEmail(user.getEmail());
		
	}

}
