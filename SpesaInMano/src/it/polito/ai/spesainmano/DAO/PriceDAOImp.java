package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Price;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PriceDAOImp implements PriceDAO{
	Connection con;
	
	@Override
	public Price insert(Price p) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into price(id_user, id_supermarket, id_product, date, price, type) values(?, ?, ?, ?, ?, ?)";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, p.getId_user().getId_user());
			ps.setInt(2, p.getId_supermarket().getId_supermarket());
			ps.setInt(3, p.getId_product().getId_product());
			ps.setDate(4, (Date) p.getDate());
			ps.setInt(5, p.getPrice());
			ps.setString(6, p.getType());
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next()){
	            // Update the id in the returned object. This is important as this value must be returned to the client.
	            int id = rs.getInt(1);
	            p.setId_price(id);
			}
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return p;
	}

	@Override
	public boolean checkPrice(Price p) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select avg (price), std(price) from (select top 10 * from price where id_supermarket=? and id_product=? order by id_price desc)";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, p.getId_supermarket().getId_supermarket());
			ps.setInt(2, p.getId_product().getId_product());
			
			ResultSet rs = ps.getGeneratedKeys();
			float avg=rs.getFloat(1);
			float std=rs.getFloat(2);
			if(p.getPrice()>avg+std || p.getPrice()<avg-std){
	            return false;
			}
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return true;
	}

}
