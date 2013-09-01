package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.DAO.CategoryDAO;
import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the functions required to the database access related with the categories
 * @version 1.0
 */
public class CategoryDAOImpl implements CategoryDAO {

	Connection con;
	
	/**
	 * Gets all the categories from the database
	 * @return A list containing all the categories with these attributes(id, name)
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public List<Category> getCategories() throws SQLException {
		
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		
		String query = "SELECT id_category, name "
					+ "FROM Category";
		
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
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	
		return categories;
	}

}
