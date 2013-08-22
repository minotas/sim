package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.model.Supermarket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PriceDAOImp implements PriceDAO {
	Connection con;

	@Override
	public int insert(Price p) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance()
				.getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into price(id_user, id_supermarket, id_product, date, price, type) values(?, ?, ?, CURDATE(), ?, ?)";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, p.getId_user().getId_user());
			ps.setInt(2, p.getId_supermarket().getId_supermarket());
			ps.setInt(3, p.getId_product().getId_product());
			ps.setFloat(4, p.getPrice());
			ps.setString(5, p.getType());
			return ps.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}

	}

	@Override
	public float[] checkPrice(Price p) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance()
				.getConnectionFromPool();
		PreparedStatement ps = null;
		
		//Discutir si es buena idea comparar el precio con el de ese supermercado o con los precios de todos
		String query = "select avg (price), std(price) from (select * from price where id_product=? order by date desc limit 50)AS last_fifty_prices";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, p.getId_product().getId_product());
			
			ResultSet rs = ps.executeQuery();
			float[] data = new float[2];
			if (rs.next()) {
				data[0] = rs.getFloat(1);
				data[1] = rs.getFloat(2);
			} else {
				data[0] = 0;
				data[1] = 0;
			}

			return data;

		} catch (SQLException e) {
			throw e;
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}
	}

	@Override
	public List<Price> getProductPriceInNearSupermarkets(int userId, int productId, float latitude, float longitude, int supermarketId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "Select max(p.id_price), p.price, s.name, s.longitude, s.latitude, s.id_supermarket, p.type "+
						"from price p, supermarket s " +
						"where p.id_product = ? and p.id_supermarket = s.id_supermarket " +
						"and SQRT(POWER((s.longitude-?),2)+POWER((s.latitude-?),2))*111120<=1000 " +
						"and s.id_supermarket != ? "+
						"group by s.id_supermarket";
		
		List<Price> prices = new ArrayList<Price>();
		
		try {

			ps = con.prepareStatement(query);
			ps.setInt(1,productId);
			ps.setFloat(2, longitude);
			ps.setFloat(3,latitude);
			ps.setInt(4,supermarketId);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				Price price = new Price();
				price.setPrice(rs.getFloat(2));
				Supermarket s = new Supermarket();
				s.setName(rs.getString(3));
				s.setLongitude(rs.getFloat(4));
				s.setLatitude(rs.getFloat(5));
				s.setId_supermarket(rs.getInt(6));
				price.setType(rs.getString(7));
				price.setId_supermarket(s);
				prices.add(price);
			}
			
			SupermarketDAO supermarketDao = new SupermarketDAOImpl();
			List<Supermarket> supermarkets = supermarketDao.getMostVisitedSupermarkets(userId);
			
			String query2 = " Select max(p.id_price), p.price, s.name, s.longitude, s.latitude,  s.id_supermarket, p.type "
					+ "from price p, supermarket s "
					+ "where p.id_product = ? and p.id_supermarket = s.id_supermarket "
					+ "and s.id_supermarket = ? "
					+ "group by s.id_supermarket";
			
			for(int i = 0; i < supermarkets.size(); i++){
				if(supermarkets.get(i).getId_supermarket() != supermarketId){
					ps = con.prepareStatement(query2);
					ps.setInt(1, productId);
					ps.setInt(2, supermarkets.get(i).getId_supermarket());
					ResultSet rs1 = ps.executeQuery();
					if(rs1.next()){
						Price price = new Price();
						price.setType(rs1.getString(7));
						price.setPrice(rs1.getFloat(2));
						Supermarket s = new Supermarket();
						s.setName(rs1.getString(3));
						s.setLongitude(rs1.getFloat(4));
						s.setLatitude(rs1.getFloat(5));
						s.setId_supermarket(rs1.getInt(6));
						price.setId_supermarket(s);
						for(int j = 0; j < prices.size(); j++) {
							if(price.getId_supermarket().getId_supermarket() != prices.get(j).getId_supermarket().getId_supermarket()){
								prices.add(price);	
							}
						}
					}
				}
				
				
			}
			return prices;
		} catch (SQLException e) {
			throw e;
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}
	}

	@Override
	public float getAverageLastSixMonths(int productId, int supermarketId)
			throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance()
				.getConnectionFromPool();
		PreparedStatement ps = null;
		String query = ("select avg(price) from price where id_product = ? and id_supermarket = ? and date > date(now()) - 180");

		ps = con.prepareStatement(query);
		ps.setInt(1, productId);
		ps.setInt(2, supermarketId);
		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			return rs.getFloat(1);
		} else {
			return -1;
		}
	}

	@Override
	public float[] getPriceQualityInfo(int productId, int supermarketId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance()
				.getConnectionFromPool();
		PreparedStatement ps = null;
		String query = ("select avg(price), min(price), max(price), count(price) from price where id_product = ? and id_supermarket = ? and date > date(now()) - 180");

		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, productId);
			ps.setInt(2, supermarketId);
			ResultSet rs = ps.executeQuery();
			float[] data = new float[4];
			if (rs.next()) {
				data[0] = rs.getFloat(1);
				data[1] = rs.getFloat(2);
				data[2] = rs.getFloat(3);
				data[3] = rs.getFloat(4);

			} 
			return data;

		} catch (SQLException e) {
			throw e;
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}
	}

	
	@Override
	public List<Price> getOffersMonitored(int idUser, float latitude, float longitude) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type " 
							+ " from price p, supermarket s, product pro " 
							+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product "
							+ "and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000 "
							+ "group by p.id_product, p.id_supermarket "
					+ ") a, monitored_product mp "
					+ " where a.type = 'o' and mp.id_product = a.id_product and mp.id_user = ?" 
				+ "UNION" 
				+ "select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type, s.id_supermarket "
					+ "from price p, supermarket s, product pro " 
					+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product " 
					+ "group by p.id_product, p.id_supermarket "
					+ " ) a, monitored_supermarket ms "
					+ "where a.type = 'o' and ms.id_supermarket = a.id_supermarket and ms.id_user = ? "
				+ "UNION "
				+"select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type, s.id_supermarket "
					+ "from price p, supermarket s, product pro " 
					+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product " 
					+ "group by p.id_product, p.id_supermarket "
					+ " ) a"
					+ "where a.type = 'o' and a.id_supermarket in(select * from (select p.id_supermarket "
						+"from price p " 
						+ "where p.id_user = ? " 
						+ "group by id_supermarket "  
						+ "order by count(p.id_supermarket) "
						+ "limit 3) a "
						+ ")) ";
		
		List<Price> prices = new ArrayList<Price>();
		
		try{
			ps = con.prepareStatement(query);
			ps.setFloat(1, longitude);
			ps.setFloat(2, latitude);
			ps.setInt(3, idUser);
			ps.setInt(4, idUser);
			ps.setInt(5, idUser);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Price price = new Price();
				Product product = new Product();
				product.setId_product(rs.getInt(1));
				price.setPrice(rs.getFloat(2));
				product.setName(rs.getString(3));
				product.setBrand(rs.getString(4));
				product.setQuantity(rs.getString(5));
				product.setMeasure_unit(rs.getString(6));
				Supermarket supermarket = new Supermarket();
				supermarket.setName(rs.getString(7));
				price.setId_product(product);
				price.setId_supermarket(supermarket);
				prices.add(price);
			}
			return prices;
		} catch (SQLException e) {
			throw e;
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}
	}

	
	@Override
	public List<Price> getGeneralOffers(float longitude, float latitude) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type " 
							+ "from price p, supermarket s, product pro " 
							+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product "
							+ "and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=2000 " 
							+ "group by p.id_product, p.id_supermarket "
						+ ") a " 
					+ "where a.type = 'o'";
		List<Price> prices = new ArrayList<Price>();
		
		try{
			ps = con.prepareStatement(query);
			ps.setFloat(1, longitude);
			ps.setFloat(2, latitude);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Price price = new Price();
				Product product = new Product();
				product.setId_product(rs.getInt(1));
				price.setPrice(rs.getFloat(2));
				product.setName(rs.getString(3));
				product.setBrand(rs.getString(4));
				product.setQuantity(rs.getString(5));
				product.setMeasure_unit(rs.getString(6));
				Supermarket supermarket = new Supermarket();
				supermarket.setName(rs.getString(7));
				price.setId_product(product);
				price.setId_supermarket(supermarket);
				prices.add(price);
			}
			return prices;
		} catch (SQLException e) {
			throw e;
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}
	}

	@Override
	public List<Price> getOffersProductsInOneList(int idUser, float latitude, float longitude) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name "
				+ " from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type, s.id_supermarket "
					+ " from price p, supermarket s, product pro " 
					+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product "
					+ "and ((SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000  or s.id_supermarket in( "
						+" select * from (select p.id_supermarket " 
						+ " from price p " 
						+ "where p.id_user = ? " 
						+ "group by id_supermarket "  
						+ "order by count(p.id_supermarket) "
						+ "limit 3) a "
						+ " )) "  
						+ "group by p.id_product, p.id_supermarket "
						+ " ) a, list_item li, market_list ml "
						+ " where a.type = 'o' and li.id_product = a.id_product and ml.id_market_list = li.id_market_list and ml.id_user = ?"
						+"UNION "
				+ "select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type " 
							+ " from price p, supermarket s, product pro " 
							+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product "
							+ "and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000 "
							+ "group by p.id_product, p.id_supermarket "
					+ ") a, monitored_product mp "
					+ " where a.type = 'o' and mp.id_product = a.id_product and mp.id_user = ?" 
				+ "UNION" 
				+ "select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type, s.id_supermarket "
					+ "from price p, supermarket s, product pro " 
					+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product " 
					+ "group by p.id_product, p.id_supermarket "
					+ " ) a, monitored_supermarket ms "
					+ "where a.type = 'o' and ms.id_supermarket = a.id_supermarket and ms.id_user = ? "
				+ "UNION "
				+"select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type, s.id_supermarket "
					+ "from price p, supermarket s, product pro " 
					+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product " 
					+ "group by p.id_product, p.id_supermarket "
					+ " ) a"
					+ "where a.type = 'o' and a.id_supermarket in(select * from (select p.id_supermarket "
						+"from price p " 
						+ "where p.id_user = ? " 
						+ "group by id_supermarket "  
						+ "order by count(p.id_supermarket) "
						+ "limit 3) a "
						+ ")) ";
		List<Price> prices = new ArrayList<Price>();
		
		try{
			ps = con.prepareStatement(query);
			ps.setFloat(1, longitude);
			ps.setFloat(2, latitude);
			ps.setInt(3, idUser);
			ps.setInt(4, idUser);
			ps.setFloat(5, longitude);
			ps.setFloat(6, latitude);
			ps.setInt(7, idUser);
			ps.setInt(8, idUser);
			ps.setInt(9, idUser);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Price price = new Price();
				Product product = new Product();
				product.setId_product(rs.getInt(1));
				price.setPrice(rs.getFloat(2));
				product.setName(rs.getString(3));
				product.setBrand(rs.getString(4));
				product.setQuantity(rs.getString(5));
				product.setMeasure_unit(rs.getString(6));
				Supermarket supermarket = new Supermarket();
				supermarket.setName(rs.getString(7));
				price.setId_product(product);
				price.setId_supermarket(supermarket);
				prices.add(price);
			}
			return prices;
		} catch (SQLException e) {
			throw e;
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}
	}
	
	@Override
	public List<Price> getOffersProductsInMultipleLists(int idUser, float latitude, float longitude) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name "
						+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type, s.id_supermarket "
							+ "from price p, supermarket s, product pro " 
							+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product " 
							+ "and ((SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000  or s.id_supermarket in( " 
								+ "select * from (select p.id_supermarket " 
								+ "from price p " 
								+ "where p.id_user = ? "
								+ "group by id_supermarket " 
								+ "order by count(p.id_supermarket) " 
								+ "limit 3) a " 
								+ ")) "   
							+ "group by p.id_product, p.id_supermarket "
							+ ") a "
						+ "where a.type = 'o' and a.id_product in ( "
							+ "select * from (select li.id_product " 
							+ "from list_item li, market_list ml "
							+ "where ml.date >=  date(now()) - 180 and li.id_market_list = ml.id_market_list and ml.id_user = ? "
							+ "group by li.id_product "
							+ "order by count(*) "
							+ "limit 10) b "
							+ ") "
						+"UNION "
				+ "select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type " 
							+ " from price p, supermarket s, product pro " 
							+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product "
							+ "and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000 "
							+ "group by p.id_product, p.id_supermarket "
					+ ") a, monitored_product mp "
					+ " where a.type = 'o' and mp.id_product = a.id_product and mp.id_user = ?" 
				+ "UNION" 
				+ "select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type, s.id_supermarket "
					+ "from price p, supermarket s, product pro " 
					+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product " 
					+ "group by p.id_product, p.id_supermarket "
					+ " ) a, monitored_supermarket ms "
					+ "where a.type = 'o' and ms.id_supermarket = a.id_supermarket and ms.id_user = ? "
				+ "UNION "
				+"select a.id_product, a.price, a.pname, a.brand, a.quantity, a.measure_unit, a.name " 
					+ "from(select pro.id_product, p.price, pro.name as pname, pro.brand, pro.quantity, pro.measure_unit, s.name, max(id_price), p.type, s.id_supermarket "
					+ "from price p, supermarket s, product pro " 
					+ "where p.id_supermarket = s.id_supermarket and p.id_product = pro.id_product " 
					+ "group by p.id_product, p.id_supermarket "
					+ " ) a"
					+ "where a.type = 'o' and a.id_supermarket in(select * from (select p.id_supermarket "
						+"from price p " 
						+ "where p.id_user = ? " 
						+ "group by id_supermarket "  
						+ "order by count(p.id_supermarket) "
						+ "limit 3) a "
						+ ")) ";
		List<Price> prices = new ArrayList<Price>();
		
		try{
			ps = con.prepareStatement(query);
			ps.setFloat(1, longitude);
			ps.setFloat(2, latitude);
			ps.setInt(3, idUser);
			ps.setInt(4, idUser);
			ps.setFloat(5, longitude);
			ps.setFloat(6, latitude);
			ps.setInt(7, idUser);
			ps.setInt(8, idUser);
			ps.setInt(9, idUser);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Price price = new Price();
				Product product = new Product();
				product.setId_product(rs.getInt(1));
				price.setPrice(rs.getFloat(2));
				product.setName(rs.getString(3));
				product.setBrand(rs.getString(4));
				product.setQuantity(rs.getString(5));
				product.setMeasure_unit(rs.getString(6));
				Supermarket supermarket = new Supermarket();
				supermarket.setName(rs.getString(7));
				price.setId_product(product);
				price.setId_supermarket(supermarket);
				prices.add(price);
			}
			return prices;
		} catch (SQLException e) {
			throw e;
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}
	}
}
