package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.DAO.MonitoredProductDAO;
import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.MonitoredProduct;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the functions required to the database access related with the monitored products
 * @version 1.0
 */
public class MonitoredProductDAOImp implements MonitoredProductDAO{

	Connection con;

	/**
	 * Inserts a new Product in the database
	 * @param mp A MonitoredProduct object containing the userId and productId
	 * @return The same monitored product object
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public MonitoredProduct insert(MonitoredProduct mp) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;

		String query = "insert into monitored_product(id_user,id_product) values(?, ?)";

		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, mp.getId_user().getId_user());
			ps.setInt(2, mp.getId_product().getId_product());
			ps.executeUpdate();
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return mp;
	}

	/**
	 * Gets all the monitored product by an user
	 * @param u An user object containing the user id
	 * @return a List of MonitoredProduct objects containing all the information of the products monitored by the user
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public List<MonitoredProduct> getMonitoredProducts(User u) throws SQLException{
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;

		String query = "select p.id_product, p.name, p.brand, p.quantity, p.measure_unit "
				+ "from product p, monitored_product mp "
				+ "where mp.id_user = ? and mp.id_product = p.id_product";

		List<MonitoredProduct> monitoredProductsList = new ArrayList<MonitoredProduct>();
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, u.getId_user());
			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				Product product = new Product();
				product.setId_product(rs.getInt(1));
				product.setName(rs.getString(2));
				product.setBrand(rs.getString(3));
				product.setQuantity(rs.getString(4));
				product.setMeasure_unit(rs.getString(5));
				MonitoredProduct mp = new MonitoredProduct();
				mp.setId_product(product);
				monitoredProductsList.add(mp);
			}
			return monitoredProductsList;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
	}

	/**
	 * Deletes a set of monitored products by an user
	 * @param mpList A list of monitored products to delete
	 * @param user An user object containing the user id
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public void deleteMonitoredProducts(List<MonitoredProduct> mpList, User u) throws SQLException {

		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;

		String query = "DELETE " + "FROM monitored_product "
				+ "WHERE id_user = ? and id_product = ?";

		try{
			ps = con.prepareStatement(query);
			int monitoredProductsNumber = mpList.size();
			
			for (int i = 0; i < monitoredProductsNumber; i++) {
				
				try {
					ps.setInt(1, u.getId_user());
					ps.setInt(2, mpList.get(i).getId_product().getId_product());
					ps.executeUpdate();
				} catch (SQLException e) {
					continue;
				}
			}
		}finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}

	}
}
