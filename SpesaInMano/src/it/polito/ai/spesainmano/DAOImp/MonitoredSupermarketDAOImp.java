package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.DAO.MonitoredSupermarketDAO;
import it.polito.ai.spesainmano.DAO.SupermarketDAO;
import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MonitoredSupermarketDAOImp implements MonitoredSupermarketDAO{

	Connection con;

	/**
	 * Inserts a new Supermarket in the database
	 * @param msList A List of MonitoredSupermarkets objects containing the userId and supermarket ids
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public void insert(List<MonitoredSupermarket> msList, int userId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;

		String query = "insert into monitored_supermarket(id_user,id_supermarket) values(?, ?)";

		int insertsNumber = msList.size();

		try{
			ps = con.prepareStatement(query);

			for(int i = 0; i < insertsNumber; i++){

				try {
					ps.setInt(1, userId);
					ps.setInt(2, msList.get(i).getId_supermarket().getId_supermarket());
					ps.executeUpdate();
				}catch (SQLException e) {
					continue;
				} 	
			}
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return;

	}

	/**
	 * Gets all the monitored supermarkets by an user
	 * @param u An user object containing the user id
	 * @return a List of MonitoredSupermarkets objects containing all the information of the supermarkets monitored by the user
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public List<Supermarket> getMonitoredSupermarkets(int idUser) throws SQLException{
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = "SELECT s.id_supermarket, s.longitude, s.latitude, s.name "
				+ "FROM supermarket s, monitored_supermarket ms "
				+ "WHERE s.id_supermarket = ms.id_supermarket and ms.id_user = ? ";
		
		List<Supermarket> supermarkets = new ArrayList<Supermarket>();
		
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, idUser);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()){

				Supermarket s = new Supermarket();
				s.setId_supermarket(rs.getInt(1));
				s.setLatitude(rs.getFloat(2));
				s.setLatitude(rs.getFloat(3));
				s.setName(rs.getString(4));
				supermarkets.add(s);
			}
			return supermarkets;
		}finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}

	}

	/**
	 * Gets all the supermarkets in an area of 2.5 kilometers and specifies which of them are monitored
	 * @param userId The id of the user
	 * @param latitude The latitude of the current position of the user
	 * @param longitude The longitude of the current position of the user
	 * @return a List of MonitoredSupermarkets objects containing all the information of the products monitored by the user
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public List<MonitoredSupermarket> getSupermarkets(float latitude, float longitude, int userId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		SupermarketDAO supermarketDao = new SupermarketDAOImpl();
		List<Supermarket> nearSupermarkets = supermarketDao.getNearSupermarkets(latitude, longitude);	
		PreparedStatement ps = null;

		String query = "select ms.id_supermarket "
				+ "from monitored_supermarket ms "
				+ "where ms.id_user = ?";

		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			int nearSupermarketsNumber = nearSupermarkets.size();
			List<MonitoredSupermarket> msList = new ArrayList<MonitoredSupermarket>();
			for(int i = 0; i < nearSupermarketsNumber; i++){
				MonitoredSupermarket ms = new MonitoredSupermarket();
				ms.setId_supermarket(nearSupermarkets.get(i));
				boolean flagFound = false;
				while(rs.next()){
					if(rs.getInt(1) == nearSupermarkets.get(i).getId_supermarket()){
						flagFound = true;
						break;
					}			
				}
				User u = new User();
				if(flagFound == true){
					u.setId_user(userId);	
				}
				else{
					u.setId_user(0);
				}
				ms.setId_user(u);
				rs.beforeFirst();
				msList.add(ms);

			}

			return msList;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}


	/**
	 * Deletes a set of monitored supermarkets by an user
	 * @param mpList A list of monitored supermarkets to delete
	 * @param userId The id of the user
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public void delete(List<MonitoredSupermarket> msList, int userId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;

		String query = "delete from monitored_supermarket where id_user = ? and id_supermarket = ?";

		int insertsNumber = msList.size();
		
		try{
			ps = con.prepareStatement(query);

			for(int i = 0; i < insertsNumber; i++){
				
				try {
					ps.setInt(1, userId);
					ps.setInt(2, msList.get(i).getId_supermarket().getId_supermarket());
					ps.executeUpdate();
				}catch (SQLException e) {
					continue;
				} 	
			}
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		
		return;

	}

	/**
	 * Gets the number of supermarkets that an user monitors
	 * @param userId The id of the user
	 * @return The number of monitored supermarkets
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public int getMonitoredSupermarketsNumber(int userId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;

		String query = "select count(*) "
				+ "from monitored_supermarket "
				+ "where id_user = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		}finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

}
