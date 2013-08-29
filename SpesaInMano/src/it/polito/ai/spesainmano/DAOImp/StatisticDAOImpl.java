package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.DAO.StatisticDAO;
import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.responses.Statistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAOImpl implements StatisticDAO{
	
	private Connection con;
	
	public List<Statistic> getAverageLastSixMonths(int supermarketId, int productId) throws SQLException{
		
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select YEAR(date), MONTHNAME(date), avg(price) "
					+ "from price where id_product = ? and id_supermarket = ? and date > date(now()) - 180 "
					+ "group by YEAR(date), MONTH(date)";
		List<Statistic> statistics = new ArrayList<Statistic>();
	
		try {
		
			ps = con.prepareStatement(query);
			ps.setInt(1, productId);
			ps.setInt(2, supermarketId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
	           Statistic statistic = new Statistic();
	           statistic.setMonth(rs.getString(2) + " " + rs.getString(1));
	           statistic.setAverage(rs.getDouble(3));
	           statistics.add(statistic);
			}
			
		} catch (SQLException e) {
		
			throw e;
		
		} finally{
	
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
	
		}
	

		return statistics;
	}

}
