package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.DAO.MarketListDAO;
import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.responses.MarketListDetails;
import it.polito.ai.spesainmano.responses.SupermarketListPrice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;  
import java.util.Comparator;

public class MarketListDAOImp implements MarketListDAO{
	Connection con;

	@Override
	public int insert(int idUser) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into market_list(id_user, date) values(?, CURDATE())";
		try {
			ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, idUser);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next()){
	           return rs.getInt(1);
			}
			else{
				return 0;
			}
			
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

	
	public List<SupermarketListPrice> getTotal(List<Supermarket> importantSupermarkets, int marketListId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		List<SupermarketListPrice> totalsList = new ArrayList<SupermarketListPrice>();
		PreparedStatement ps = null;
		String query = " SELECT count(*), SUM(li.quantity * a.actual_price) "
						+ "FROM list_item li, (SELECT p.price as actual_price, p.id_product as id_product "
							+ "FROM price p, (SELECT max(p.id_price) as id_price "
								+ "FROM price p " 
								+ "WHERE p.id_supermarket = ? " 
								+ "GROUP BY id_product) currentIdPrice "
							+ "WHERE p.id_price = currentIdPrice.id_price) a " 
						+ "where li.id_market_list = ? and li.id_product = a.id_product";
		try {
			for(int i=0; i<importantSupermarkets.size(); i++){
				ps=null;
				ps = con.prepareStatement(query);
				ps.setInt(1, importantSupermarkets.get(i).getId_supermarket());
				ps.setInt(2, marketListId);
				ResultSet rs = ps.executeQuery();
				if(rs.next()){
		            SupermarketListPrice slp = new SupermarketListPrice();
		            slp.setId_supermarket(importantSupermarkets.get(i).getId_supermarket());
		            slp.setProducts_found(rs.getInt(1));
		            slp.setTotal(rs.getFloat(2));
		            slp.setLatitude(importantSupermarkets.get(i).getLatitude());
		            slp.setLongitude(importantSupermarkets.get(i).getLongitude());
					slp.setName(importantSupermarkets.get(i).getName());
		           if(slp.getProducts_found() != 0){
		        	   totalsList.add(slp);
		           }
					
				}
			}
			Collections.sort(totalsList, new Comparator<Object>() {  
				  
	            public int compare(Object o1, Object o2) {  
	            	SupermarketListPrice gtr1 = (SupermarketListPrice) o1;  
	            	SupermarketListPrice gtr2 = (SupermarketListPrice) o2;  
	                int num1 = gtr1.getProducts_found();  
	                int num2 = gtr2.getProducts_found();  
	  
	                if (num1 > num2) {  
	                    return -1;  
	                } else if (num1 < num2) {  
	                    return 1;  
	                } else {
	                	float tot1 = gtr1.getTotal();  
		                float tot2 = gtr2.getTotal();  
	                	if (tot1 > tot2) {  
		                    return 1;  
		                } else if (tot1 < tot2) {  
		                    return -1;  
		                } else {  
		                    return 0;  
		                }   
	                }  
	            }  
	        });
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return totalsList;
	}


	@Override
	public int getNumberOfMarketLists(int idUser) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "select count(id_market_list) from market_list list where id_user = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, idUser);
			rs = ps.executeQuery();
			rs.next();
	        return rs.getInt(1);
			
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}


	@Override
	public int getLastMarketListId(int idUser) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "select max(ml.id_market_list) from market_list ml where ml.id_user = ? group by ml.id_user";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, idUser);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
	        return 0;
			
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}


	
	@Override
	public List<MarketListDetails> getDetails(int supermaketId, int marketListId) throws SQLException{
	con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
	List<MarketListDetails> detailsList = new ArrayList<MarketListDetails>();
	PreparedStatement ps = null;
	ResultSet rs = null;
	String query = "  SELECT p.name, p.brand, p.measure_unit, p.quantity, li.quantity, li.quantity * a.actual_price "
						+ "FROM list_item li, product p, (SELECT p.price as actual_price, p.id_product as id_product "
							+ "FROM price p, (SELECT max(p.id_price) as id_price "
								+ "FROM price p " 
								+ "WHERE p.id_supermarket = ? " 
								+ "GROUP BY id_product) currentIdPrice "
							+ "WHERE p.id_price = currentIdPrice.id_price) a " 
						+ "where li.id_market_list = ? and li.id_product = a.id_product and li.id_product = p.id_product" ;
	try {
		ps = con.prepareStatement(query);
		ps.setInt(1, supermaketId);
		ps.setInt(2, marketListId);
		rs = ps.executeQuery();
		while(rs.next()){
           MarketListDetails mld = new MarketListDetails();
            mld.setName(rs.getString(1));
            mld.setBrand(rs.getString(2));
            mld.setMeasure(rs.getString(4) + " " + rs.getString(3) );
            mld.setQuantity(rs.getInt(5));
            mld.setPrice(rs.getFloat(6));
			detailsList.add(mld);
		}
		
	}catch (SQLException e) {
		 throw e;
	} finally{
		ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
	}
	return detailsList;
	}

}
