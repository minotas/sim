package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.User;
import java.sql.*;

import it.polito.ai.spesainmano.db.*;

public class UserDAOImp implements UserDAO{
	Connection con;
	public void insert(User u){
		
	}
	
	public void delete(User u){
		
	}
	
	public void update(User u){
		
	}
	
	public User findbyID(String id){
		
		return null;
	}

	public User login(String username, String password) {
		User u = null;
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "SELECT id_user, name, lastname, points FROM USER WHERE username = ? and password = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()){
				u = new User();
				u.setId_user(rs.getInt(1));
				u.setName(rs.getString(2));
				u.setLastname(rs.getString(3));
				u.setPoints(rs.getInt(4));
			}
			
		} catch (SQLException e) {
			//Pensar si hacer un log
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return u;
		
	}
	
}
