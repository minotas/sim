package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.User;


public interface UserDAO {

	public void insert(User u);
	
	public void delete(User u);
	
	public void update(User u);
	
	public User findbyID(String id);
	
	public User login(String username, String password);
	
}
