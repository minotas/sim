package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Supermarket;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PriceDAOImp implements PriceDAO{
	Connection con;
	
	@Override
	public int insert(Price p) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into price(id_user, id_supermarket, id_product, date, price, type) values(?, ?, ?, ?, ?, ?)";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, p.getId_user().getId_user());
			ps.setInt(2, p.getId_supermarket().getId_supermarket());
			ps.setInt(3, p.getId_product().getId_product());
			ps.setDate(4, (Date) p.getDate());
			ps.setFloat(5, p.getPrice());
			ps.setString(6, p.getType());
			return ps.executeUpdate();
			
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	
	}

	@Override
	public float[] checkPrice(Price p) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = "select avg (price), std(price) from (select * from price where id_supermarket=? and id_product=? order by date desc limit 10)AS last_ten_prices";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, p.getId_supermarket().getId_supermarket());
			ps.setInt(2, p.getId_product().getId_product());
			
			ResultSet rs = ps.executeQuery();
			float[] data = new float[2];
			if(rs.next()){
				data[0] = rs.getFloat(1);
				data[1] = rs.getFloat(2);
			}
			else{
				data[0] = 0;
				data[1] = 0;
			}
			
			return data;
		
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

	@Override
	public List<Price> getProductPriceInNearSupermarkets(Price p) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = " Select max(p.id_price), p.date, p.type, p.price, s.name, s.longitude, s.latitude"+ 
						"from price p, supermarket s" +  
						"where p.id_product = ? and p.id_supermarket = s.id_supermarket and p.id_supermarket != ?" + 
						"and ((SQRT(POWER((s.longitude-?),2)+POWER((s.latitude-?),2))*111120<=1000) or (p.id_supermarket in("+
							"select s1.id_supermarket"+ 
							"from price p1, supermarket s1"+ 
							"where p1.id_user = ?"+ 
							"group by id_supermarket"+ 
							"having count(p1.id_price) >="+  
								"(select count(*)/"+
									"(select count(distinct id_supermarket)"+ 
									"from price where id_user = ?)"+ 
								"from price p2 "+
								"where p2.id_user = ?))"+ 
								"))"+
						"group by s.id_supermarket;";
		
		List<Price> prices = new ArrayList<Price>();
		try {
			
			ps = con.prepareStatement(query);
			ps.setInt(1, p.getId_product().getId_product());
			ps.setFloat(2, p.getId_supermarket().getLongitude());
			ps.setFloat(3, p.getId_supermarket().getLatitude());
			ps.setInt(4, p.getId_supermarket().getId_supermarket());
			ps.setInt(5, p.getId_user().getId_user());
			ps.setInt(6, p.getId_user().getId_user());
			ps.setInt(7, p.getId_user().getId_user());
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Price price = new Price();
				price.setDate(rs.getDate(2));
				price.setType(rs.getString(3));
				price.setPrice(rs.getFloat(4));
				Supermarket s = new Supermarket();
				s.setName(rs.getString(5));
				s.setLongitude(rs.getFloat(6));
				s.setLatitude(rs.getFloat(7));
				p.setId_supermarket(s);
				prices.add(price);
			}
			return prices;
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

	@Override
	public float getAverageLastSixMonths(int productId, int supermarketId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = ("select avg(price) from price where id_product = ? and id_supermarket = ? and date > date(now()) - 180");
		ps = con.prepareStatement(query);
		ps.setInt(1, productId);
		ps.setInt(2, supermarketId);
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()){
			return rs.getFloat(1);
		}
		else{ 
			return -1;
		}
	}
}
