package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;

import java.sql.*;

import it.polito.ai.spesainmano.db.*;

public class UserDAOImp implements UserDAO{
	Connection con;
	
	public User insert(User u) throws SQLException{
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into user(name, lastname, password, email, points) values(?, ?, ?, ?, 0)";
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, u.getName());
			ps.setString(2, u.getLastname());
			ps.setString(4, u.getPassword());
			ps.setString(5, u.getEmail());
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next()){
	            // Update the id in the returned object. This is important as this value must be returned to the client.
	            int id = rs.getInt(1);
	            u.setId_user(id);
			}
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return u;
	}
	
	public boolean delete(User u){
		return false;
		
	}
	
	public User update(User u){
		return u;
		
	}
	
	public User findbyID(String id){
		
		return null;
	}

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
		} catch (SQLException e) {
			throw new SQLException("Server received an invalid response from upstream server");
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return u;
	}



	@Override
	public boolean checkEmail(String email) throws SQLException{
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select email from user where email = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
		}catch (SQLException e) {
			throw new SQLException("Server received an invalid response from upstream server");
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return false;
	}
	
}
