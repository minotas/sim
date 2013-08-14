package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {

	Connection con;
	
	@Override
	public List<Category> getCategories() throws SQLException {
		
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "SELECT id_category, name FROM Category";
		List<Category> categories = new ArrayList<Category>();
	
		try {
		
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()){
		
				Category c = new Category();
				c.setId_category(rs.getInt(1));
				c.setName(rs.getString(2));
				categories.add(c);
			
			}
		
		} catch (SQLException e) {
		
			throw e;
		
		} finally{
	
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
	
		}
	
		return categories;
	
	}

}
