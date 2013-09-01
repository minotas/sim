package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.model.User;
import java.sql.*;
import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.db.*;

/**
 * Defines the functions required to the database access related with the user
 * @version 1.0
 */
public class UserDAOImp implements UserDAO{
	Connection con;
	
	/**
	 * Inserts a new user in the database
	 * @param user An user object containing the user information to do the registration
	 * @return an user object containing the information of the user, including the id assigned.
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	public User insert(User u) throws SQLException{
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into user(name, lastname, password, email, points) values(?, ?, ?, ?, ?)";
		try {
			ps = con.prepareStatement(query,  Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, u.getName());
			ps.setString(2, u.getLastname());
			ps.setString(3, u.getPassword());
			ps.setString(4, u.getEmail());
			ps.setInt(5, 0);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next()){
	            // Update the id in the returned object. This is important as this value must be returned to the client.
	            int id = rs.getInt(1);
	            u.setId_user(id);
			}
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return u;
	}
	
	/**
	 * Verifies that the user exists in the database and the password is correct
	 * @param email Email of the user(username)
	 * @param password Password of the user
	 * @return If the user exists an user object containing the id, name, lastaname and points of a user, 
	 *		   otherwise returns null
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	public User login(String email, String password) throws SQLException {
	
		User u = null;
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "SELECT id_user, name, lastname, points FROM USER WHERE email = ? and password = ?";
		
		try {
			
			ps = con.prepareStatement(query);
			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()){
				u = new User();
				u.setId_user(rs.getInt(1));
				u.setName(rs.getString(2));
				u.setLastname(rs.getString(3));
				u.setPoints(rs.getInt(4));
			}
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		
		return u;
	}

	@Override
	public int getPoints(int id) throws SQLException{
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select points from user where id_user = ?";
		
		try {
		
			ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
		
			if(rs.next()){
			
				return rs.getInt(1);
			
			}
		
		}finally{
			
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		
		}
		
		return -1;
	}

	
	/**
	 * Increments the user points by 5
	 * @param userId The id of the user
	 * @return true if success, otherwise false
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public boolean incrementPoints(int userId) throws SQLException {
		
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "update user set points = points + 5 where id_user = ?";
	
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, userId);
			
			if(ps.executeUpdate() > 0){
				return true;
			} 
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		
		return false;
	}

	/*@Override
	public User getUser(int id) throws SQLException {
		
		User u = null;
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "SELECT id_user, name, lastname, points FROM USER WHERE id_user = ?";
		
		try {
		
			ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()){
		
				u = new User();
				u.setId_user(rs.getInt(1));
				u.setName(rs.getString(2));
				u.setLastname(rs.getString(3));
		
				u.setPoints(rs.getInt(4));
		
			}
		
		} catch (SQLException e) {
		
			throw e;
		
		} finally{
		
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		
		}
		
		return u;
	
	}*/

}
