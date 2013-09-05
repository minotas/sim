package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.DAO.SupermarketDAO;
import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Supermarket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the functions required to the database access related with the supermarket
 * @version 1.0
 */
public class SupermarketDAOImpl implements SupermarketDAO {

	Connection con;

	/**
	 * Gets the supermarkets located in a radio of 100m from the location received
	 * @param latitude The latitude of the position of the user
	 * @param longitude The longitude of the position of the user
	 * @return a List of supermarkets located in 100m of radio from the position of the user
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public List<Supermarket> checkin(float latitude, float longitude) throws SQLException {

		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;

		String query = "SELECT id_supermarket, name, longitude, latitude "
				+ "FROM supermarket "
				+ "WHERE SQRT(POWER((longitude-?),2)+POWER((latitude-?),2))*111120<=100";

		List<Supermarket> supermarkets = new ArrayList<Supermarket>();

		try {

			ps = con.prepareStatement(query);
			ps.setFloat(1, longitude);
			ps.setFloat(2, latitude);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				Supermarket s = new Supermarket();
				s.setId_supermarket(rs.getInt(1));
				s.setName(rs.getString(2));
				s.setLatitude(rs.getFloat(4));
				s.setLongitude(rs.getFloat(3));
				supermarkets.add(s);
			}
		}finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}

		return supermarkets;
	}

	/**
	 * Gets the 3 most visited supermarkets by an user in the last six months
	 * @param userId The id of the user
	 * @return a List containing 3 or less supermarkets
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public List<Supermarket> getMostVisitedSupermarkets(int userId) throws SQLException{
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select s.id_supermarket, s.longitude, s.latitude, s.name "
				+ "from supermarket s, price p "
				+ "where p.date > DATE_SUB(CURDATE(),INTERVAL 6 MONTH) and  p.id_supermarket = s.id_supermarket and p.id_user = ? "
				+ "group by id_supermarket "
				+ "order by count(p.id_product) desc limit 3";
		List<Supermarket> supermarkets = new ArrayList<Supermarket>();

		try {

			ps = con.prepareStatement(query);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){

				Supermarket s = new Supermarket();
				s.setId_supermarket(rs.getInt(1));
				s.setLongitude(rs.getFloat(2));
				s.setLatitude(rs.getFloat(3));
				s.setName(rs.getString(4));
				supermarkets.add(s);

			}

		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}

		return supermarkets;

	}

	/**
	 * Gets the supermarket located in an area of 2km or radio
	 * @param latitude The latitude of the position of the user
	 * @param longitude The longitude of the position of the user
	 * @return a List containing all the supermarkets in an area of 2km of radio
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public List<Supermarket> getNearSupermarkets(float latitude, float longitude)throws SQLException{
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = "select s.id_supermarket, s.longitude, s.latitude, s.name "
				+ "FROM supermarket s "
				+ "WHERE SQRT(POWER((longitude-?),2)+POWER((latitude-?),2))*111120<=2000 "
				+ "ORDER BY SQRT(POWER((longitude-?),2)+POWER((latitude-?),2))*111120";

		List<Supermarket> supermarkets = new ArrayList<Supermarket>();

		try {

			ps = con.prepareStatement(query);
			ps.setFloat(1, longitude);
			ps.setFloat(2, latitude);
			ps.setFloat(3, longitude);
			ps.setFloat(4, latitude);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){

				Supermarket s = new Supermarket();
				s.setId_supermarket(rs.getInt(1));
				s.setLongitude(rs.getFloat(2));
				s.setLatitude(rs.getFloat(3));
				s.setName(rs.getString(4));
				supermarkets.add(s);

			}
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return supermarkets;	
	}


}
