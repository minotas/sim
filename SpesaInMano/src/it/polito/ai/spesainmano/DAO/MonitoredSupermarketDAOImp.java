package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.MonitoredSupermarket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

}
