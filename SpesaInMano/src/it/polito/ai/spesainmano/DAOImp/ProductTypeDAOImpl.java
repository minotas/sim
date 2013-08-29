package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.DAO.ProductTypeDAO;
import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.ProductType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductTypeDAOImpl implements ProductTypeDAO {

	Connection con;
	
	@Override
	public List<ProductType> getProductTypesByCategory(int categoryId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "SELECT id_product_type, name FROM product_type WHERE id_category = ?";
		List<ProductType> productTypes = new ArrayList<ProductType>();
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, categoryId);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()){
				ProductType pt = new ProductType();
				pt.setId_product_type(rs.getInt(1));
				pt.setName(rs.getString(2));
				productTypes.add(pt);
			}
		} catch (SQLException e) {
			throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return productTypes;
	}

	@Override
	public int getIdByName(String name) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "SELECT id_product_type FROM product_type WHERE name = ?";
		ps = con.prepareStatement(query);
		ps.setString(1, name);
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			return rs.getInt(1);			
		}
		return 0;
	}

}
