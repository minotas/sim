package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.User;




import java.sql.*;

import it.polito.ai.spesainmano.db.*;

public class UserDAOImp implements UserDAO{
	Connection con;
	
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
			throw e;
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
				return rs.getInt(0);
			}
		
		}catch (SQLException e) {
			throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return -1;
	}

	@Override
	public boolean incrementPoints(int id_user) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "update user set points = points + 5 where id_user = ?";
		try {
			
			ps = con.prepareStatement(query);
			ps.setInt(1, id_user);
			
			if(ps.executeUpdate() > 0){
				return true;
			} 
			
		}catch (SQLException e) {
			throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return false;
	}



}
