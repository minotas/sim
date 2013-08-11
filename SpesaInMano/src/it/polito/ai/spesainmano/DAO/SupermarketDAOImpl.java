package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.model.Supermarket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupermarketDAOImpl implements SupermarketDAO {

	Connection con;
	@Override
	public List<Supermarket> checkin(float latitude, float longitude) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "SELECT id_supermarket, name, longitude, latitude FROM supermarket WHERE SQRT(POWER((longitude-?),2)+POWER((latitude-?),2))*111120<=100";
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
		} catch (SQLException e) {
			throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return supermarkets;
	}

}
