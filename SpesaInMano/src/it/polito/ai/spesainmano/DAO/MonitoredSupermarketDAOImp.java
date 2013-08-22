package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.model.Supermarket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MonitoredSupermarketDAOImp implements MonitoredSupermarketDAO{
	Connection con;

	@Override
	public MonitoredSupermarket insert(MonitoredSupermarket ms) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into monitored_supermarket(id_user,id_supermarket) values(?, ?)";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, ms.getId_user().getId_user());
			ps.setInt(2, ms.getId_supermarket().getId_supermarket());
			ps.getGeneratedKeys();
			
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return ms;
	}

	@Override
	public List<Supermarket> getMonitoredSupermarkets(int idUser) throws SQLException{
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "SELECT s.id_supermarket, s.longitude, s.latitude"
				+ "FROM supermarket s, monitored_supermarket ms"
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
				supermarkets.add(s);
		
			}
			return supermarkets;
			
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}

	}

}
