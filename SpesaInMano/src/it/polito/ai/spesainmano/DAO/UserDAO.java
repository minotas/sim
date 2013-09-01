package it.polito.ai.spesainmano.DAO;

import java.sql.SQLException;


import it.polito.ai.spesainmano.model.User;


public interface UserDAO {

	public User insert(User u) throws SQLException;
	
	public User login(String email, String password) throws SQLException;

	public int getPoints(int id)  throws SQLException;
	
	public boolean incrementPoints(int id_user) throws SQLException;

	//public User getUser(int id) throws SQLException;
	
}
