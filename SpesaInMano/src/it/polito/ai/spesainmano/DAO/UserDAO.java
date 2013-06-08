package it.polito.ai.spesainmano.DAO;

import java.sql.SQLException;

import javax.naming.ServiceUnavailableException;

import it.polito.ai.spesainmano.model.User;


public interface UserDAO {

	public User insert(User u) throws SQLException;
	
	public boolean delete(User u);
	
	public User update(User u);
	
	public User findbyID(String id);
	
	public User login(String username, String password) throws SQLException;

	public boolean checkUsername(String username);

	public boolean checkEmail(String email);
	
}
