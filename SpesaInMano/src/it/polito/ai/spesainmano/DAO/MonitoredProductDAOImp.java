package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.MonitoredProduct;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MonitoredProductDAOImp implements MonitoredProductDAO{
	Connection con;

	@Override
	public MonitoredProduct insert(MonitoredProduct mp) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into monitored_product(id_user,id_product) values(?, ?)";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, mp.getId_user().getId_user());
			ps.setInt(2, mp.getId_product().getId_product());
			ps.getGeneratedKeys();
			
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return mp;
	}

}
