package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Category;
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
	}

}
