package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.DAO.PriceDAO;
import it.polito.ai.spesainmano.DAO.SupermarketDAO;
import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.responses.Statistic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the functions required to the database access related with the prices
 * @version 1.0
 */
public class PriceDAOImp implements PriceDAO {
	Connection con;

	/**
	 * Inserts a new price in the database
	 * @param p A price object containing the price information to required to add the price
	 * @return When the insert success returns 1, otherwise 0
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
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
		} finally {
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}

	}

	/**
	 * Gets the average and the standard deviation of the last 50 prices of a product 
	 * @param p A price object containing the price information
	 * @return A float array containing the average(position 0) and the standard deviation(position 1)
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public float[] checkPrice(Price p) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = "select avg (price), std(price) "
				+ "from (select * "
					+ "from price "
					+ "where id_product=? and type != 'o' "
					+ "order by date desc "
					+ "limit 50)AS last_fifty_prices";
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

		} finally {
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

	/**
	 * Gets prices of a product in the supermarkets located in an area of 1km of radio given a position 
	 * and in the monitored supermarkets by the user
	 * @param userId The id of the user to obtain its monitored supermarkets
	 * @param productId The id of the product
	 * @param latitude The latitude of the current position of the user
	 * @param longitude The longitude of the current position of the user
	 * @param supermarketId The id of the supermarket in which the user is
	 * @return The list of the current prices of the product in the near and monitored supermarkets
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public List<Price> getProductPriceInNearSupermarkets(int userId, int productId, float latitude, float longitude, int supermarketId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select p.id_price, p.price, s.name, s.longitude, s.latitude, s.id_supermarket, p.type "
					+ "from price p, supermarket s, (Select max(p.id_price) as id_price, s.id_supermarket "
						+ "from price p, supermarket s  "
						+ "where p.id_product = ? and p.id_supermarket = s.id_supermarket  "
						+ "and SQRT(POWER((s.longitude-?),2)+POWER((s.latitude-?),2))*111120<=1000 "
						+ "and s.id_supermarket != ? "
						+ "group by s.id_supermarket) maxPrice "
					+ "where s.id_supermarket = maxPrice.id_supermarket and p.id_price = maxPrice.id_price ";
		
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
			
			String query2 = "Select  max(p.id_price), p.price, s.name, s.longitude, s.latitude,  s.id_supermarket, p.type "
						+ "from price p, supermarket s, (Select max(p.id_price) as id_price, s.id_supermarket "
							+ "from price p, supermarket s  "
							+ "	where p.id_product = ? and p.id_supermarket = s.id_supermarket  "
							+ "and s.id_supermarket = ?  "
							+ ") maxPrice "
						+ "where p.id_price = maxPrice.id_price and p.id_supermarket = s.id_supermarket";
			
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
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}
	}
	
	

	/**
	 * Gets the average of the prices of the product in a given supermarket in the last 6 months
	 * @param productId The id of the product
	 * @param supermarketId The id of the supermarket
	 * @return A list of statistic objects containing the month-year and average price of the product in the last 6 months
	 * 		   in the supermarket.
	 * @throws SQLException Generated when there is any problem accessing the database
	*/
	@Override
	public List<Statistic> getAverageLastSixMonths(int supermarketId, int productId) throws SQLException{
		
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = "select YEAR(date), MONTHNAME(date), avg(price) "
					+ "from price where id_product = ? and id_supermarket = ? and date > DATE_SUB(CURDATE(),INTERVAL 6 MONTH) "
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
			
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return statistics;
	}
	
	/**
	 * Gets average, min, max and number of prices of a product in all the supermarkets in the last 6 months
	 * @param productId The id of the product
	 * @return A float array containing the average(position 0), min(position 1), max(position 2) and number
	 * 		   of prices(position 3) 
	 * @throws SQLException Generated when there is any problem accessing the database
	*/
	@Override
	public float[] getPriceQualityInfo(int productId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = ("select avg(price), min(price), max(price), count(price) "
				+ "from price "
				+ "where id_product = ?  and date > DATE_SUB(CURDATE(),INTERVAL 6 MONTH)");

		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, productId);
			ResultSet rs = ps.executeQuery();
			float[] data = new float[4];
			if (rs.next()) {
				data[0] = rs.getFloat(1);
				data[1] = rs.getFloat(2);
				data[2] = rs.getFloat(3);
				data[3] = rs.getFloat(4);

			} 
			return data;

		} finally {
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

	
	/**
	 * Gets offers in the monitored and favourite supermarkets and the offers of monitored products
	 * @param idUser The id of the user
	 * @param latitude The latitude of the current location of the user
	 * @param longitude The longitude the current location of the user
	 * @return Selects the monitored products that are in offer in the supermarkets located in an area of  5km of radio 
	 *		   Select the products that are in offer in the monitored supermarkets
	 *		   Select the products that are in offer in the 3 most visited supermarkets
	 * @throws SQLException Generated when there is any problem accessing the database
	*/
	@Override
	public List<Price> getOffersMonitored(int idUser, float latitude, float longitude) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = "Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name "
					+ "from monitored_product mp, product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price " 
						+ "from price p, ( "
							+ "select max(p.id_price) as id_price " 
							+ "from price p "
							+ "group by p.id_product, p.id_supermarket " 
							+ ") currentPrice "
						+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
					+ "where mp.id_product = oP.id_product and mp.id_user = ? and pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000 "
				+ "UNION " 
				+ "Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name " 
				+ "from monitored_supermarket ms, product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price " 
					+ "from price p, ( "
						+ "select max(p.id_price) as id_price " 
						+ "from price p "
						+ "group by p.id_product, p.id_supermarket " 
						 + ") currentPrice "
					+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
				+ "where ms.id_user = ? and pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket and oP.id_supermarket = ms.id_supermarket "
				+ "UNION "
				+"Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name "
				+ "from product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price  "
					+ "from price p, (  "
						+ "select max(p.id_price) as id_price " 
						+ "from price p  "
						+ "group by p.id_product, p.id_supermarket " 
						+ ") currentPrice  "
					+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
				+ "where pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket and s.id_supermarket in( select * " 
					+ "from (select p.id_supermarket  "
						+ "from price p   "
						+ "where p.id_user = ? "  
						+ "group by id_supermarket "   
						+ "order by count(p.id_supermarket) "
						+ "limit 3) "
					+ "a)";
		
		List<Price> prices = new ArrayList<Price>();
		
		try{
			ps = con.prepareStatement(query);
			ps.setInt(1, idUser);
			ps.setFloat(2, longitude);
			ps.setFloat(3, latitude);
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
		} finally {
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

	/**
	 * Gets offers in the monitored and favourite supermarkets and the offers of monitored supermarkets
	 * @param latitude The latitude of the current location of the user
	 * @param longitude The longitude the current location of the user
	 * @return Selects all the offer prices in the supermarkets that are in area of 2.5km radio 
	 * @throws SQLException Generated when there is any problem accessing the database
	*/	
	@Override
	public List<Price> getGeneralOffers(float longitude, float latitude) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = "select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name "
						+ "from product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price "
							+ "from price p, ( "
								+ "select max(p.id_price) as id_price "
								+ "from price p "
								+ "group by p.id_product, p.id_supermarket "
								+ ") currentPrice "
							+ "where p.id_price = currentPrice.id_price and p.type = 'o') oP "
						+ "where op.id_product = pro.id_product and op.id_supermarket = s.id_supermarket and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=2500 ";
		
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
		} finally {
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

	
	/**
	 * Gets offers in the monitored and favourite supermarkets and the offers of monitored and of all the products in the
	 * market list
	 * @param idUser The id of the user
	 * @param latitude The latitude of the current location of the user
	 * @param longitude The longitude the current location of the user
	 * @return Selects the monitored products that are in offer in the supermarkets located in an area of  5km of radio 
	 *		   Selects the products that are in offer in the monitored supermarkets
	 *		   Selects the products that are in offer in the 3 most visited supermarkets
	 *		   Selects the product in the market list that are in offer in the supermarkets located in an area of 5km of radio
	 * @throws SQLException Generated when there is any problem accessing the database
	*/
	@Override
	public List<Price> getOffersProductsInOneList(int idUser, float latitude, float longitude) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name "
					+ "from monitored_product mp, product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price " 
						+ "from price p, ( "
							+ "select max(p.id_price) as id_price " 
							+ "from price p "
							+ "group by p.id_product, p.id_supermarket " 
							+ ") currentPrice "
						+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
					+ "where mp.id_product = oP.id_product and mp.id_user = ? and pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000 "
				+ "UNION " 
				+ "Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name " 
				+ "from monitored_supermarket ms, product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price " 
					+ "from price p, ( "
						+ "select max(p.id_price) as id_price " 
						+ "from price p "
						+ "group by p.id_product, p.id_supermarket " 
						 + ") currentPrice "
					+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
				+ "where ms.id_user = ? and pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket and oP.id_supermarket = ms.id_supermarket "
				+ "UNION "
				+"Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name "
				+ "from product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price  "
					+ "from price p, (  "
						+ "select max(p.id_price) as id_price " 
						+ "from price p  "
						+ "group by p.id_product, p.id_supermarket " 
						+ ") currentPrice  "
					+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
				+ "where pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket and s.id_supermarket in( select * " 
					+ "from (select p.id_supermarket  "
						+ "from price p   "
						+ "where p.id_user = ? "  
						+ "group by id_supermarket "   
						+ "order by count(p.id_supermarket) "
						+ "limit 3) "
					+ "a) "
					+ "UNION "
					+ "Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name "
					+ "from market_list ml, list_item li, product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price " 
						+ "from price p, ( "
							+ "select max(p.id_price) as id_price " 
							+ "from price p "
							+ "group by p.id_product, p.id_supermarket "
						+ "	) currentPrice  "
						+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
					+ "where pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket "
					+ "and ml.id_user = ? and ml.id_market_list = li.id_market_list and li.id_product = op.id_product  and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000 ";
		
		List<Price> prices = new ArrayList<Price>();
		
		try{
			ps = con.prepareStatement(query);
			ps.setInt(1, idUser);
			ps.setFloat(2, longitude);
			ps.setFloat(3, latitude);
			ps.setInt(4, idUser);
			ps.setInt(5, idUser);
			ps.setInt(6, idUser);
			ps.setFloat(7, longitude);
			ps.setFloat(8, latitude);
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
		} finally {
			ConnectionPoolManager.getPoolManagerInstance()
					.returnConnectionToPool(con);
		}
	}
	
	/**
	 * Gets offers in the monitored and favourite supermarkets and the offers of monitored and of all the products in the
	 * market list
	 * @param idUser The id of the user
	 * @param latitude The latitude of the current location of the user
	 * @param longitude The longitude the current location of the user
	 * @return Selects the monitored products that are in offer in the supermarkets located in an area of  5km of radio 
	 *		   Selects the products that are in offer in the monitored supermarkets
	 *         Selects the products that are in offer in the 3 most visited supermarkets
	 *         Selects the 5 most favourite products in the market lists that are in offer in the supermarkets located in an area of 5km of radio\
	 * @throws SQLException Generated when there is any problem accessing the database
	*/
	
	@Override
	public List<Price> getOffersProductsInMultipleLists(int idUser, float latitude, float longitude) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = "Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name "
					+ "from monitored_product mp, product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price " 
						+ "from price p, ( "
							+ "select max(p.id_price) as id_price " 
							+ "from price p "
							+ "group by p.id_product, p.id_supermarket " 
							+ ") currentPrice "
						+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
					+ "where mp.id_product = oP.id_product and mp.id_user = ? and pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000 "
					+ "UNION " 
					+ "Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name " 
					+ "from monitored_supermarket ms, product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price " 
						+ "from price p, ( "
							+ "select max(p.id_price) as id_price " 
							+ "from price p "
							+ "group by p.id_product, p.id_supermarket " 
							 + ") currentPrice "
						+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
					+ "where ms.id_user = ? and pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket and oP.id_supermarket = ms.id_supermarket "
					+ "UNION "
					+"Select oP.id_product, oP.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name "
					+ "from product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price  "
						+ "from price p, (  "
							+ "select max(p.id_price) as id_price " 
							+ "from price p  "
							+ "group by p.id_product, p.id_supermarket " 
							+ ") currentPrice  "
						+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
					+ "where pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket and s.id_supermarket in( select * " 
						+ "from (select p.id_supermarket  "
							+ "from price p   "
							+ "where p.id_user = ? "  
							+ "group by id_supermarket "   
							+ "order by count(p.id_supermarket) "
							+ "limit 3) "
						+ "a) "
						+ "UNION "
						+ "Select op.id_product, op.price, pro.name, pro.brand, pro.quantity, pro.measure_unit, s.name "
						+ "from market_list ml, list_item li, product pro, supermarket s, (select p.id_product, p.id_supermarket, p.price "
							+ "from price p, (  "
								+ "select max(p.id_price) as id_price " 
								+ "from price p  "
								+ "group by p.id_product, p.id_supermarket " 
								+ ") currentPrice  "
							+ "where p.id_price = currentPrice.id_price and p.type = 'o' ) oP "
						+ "where pro.id_product = oP.id_product and s.id_supermarket = oP.id_supermarket "
						+ "and ml.id_user = ? and ml.id_market_list = li.id_market_list and li.id_product = op.id_product  "
						+ "and (SQRT(POWER(s.longitude-?,2)+POWER(s.latitude-?,2)))*111120<=5000 and li.id_product in(select * " 
							+ "from(select count(*) "
									+ "from market_list ml1, list_item li1 "
									+ "where ml1.id_user = ? and li1.id_market_list = ml1.id_market_list "
									+ "group by li1.id_product  "
									+ "order by count(*) desc "
									+ "limit 5) favoriteProducts "
								+ ") ";
		
		List<Price> prices = new ArrayList<Price>();
		
		try{
			ps = con.prepareStatement(query);
			ps.setInt(1, idUser);
			ps.setFloat(2, longitude);
			ps.setFloat(3, latitude);
			ps.setInt(4, idUser);
			ps.setInt(5, idUser);
			ps.setInt(6, idUser);
			ps.setFloat(7, longitude);
			ps.setFloat(8, latitude);
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
		} finally {
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}
}
