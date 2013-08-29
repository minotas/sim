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
				rs.first();
				msList.add(ms);
			}
			
			return msList;
			
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

}
