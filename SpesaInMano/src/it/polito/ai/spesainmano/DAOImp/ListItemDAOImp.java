package it.polito.ai.spesainmano.DAOImp;

import it.polito.ai.spesainmano.DAO.ListItemDAO;
import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.ListItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Defines the functions required to the database access related with the list item
 * @version 1.0
 */
public class ListItemDAOImp implements ListItemDAO{
	Connection con;

	/**
	 * Inserts a set of list items
	 * @param listItems A Set of list items to be inserted
	 * @param idList The id of the market list that includes the list items
	 * @throws SQLException Generated when there is any problem accessing the database
	 */
	@Override
	public void insertListItems(List<ListItem> listItems, int idList) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into list_item(id_market_list,id_product, quantity) values(?, ?, ?)";
		try {
			ps = con.prepareStatement(query);
			int itemsNumber = listItems.size();
			for(int i = 0; i < itemsNumber; i++){
				ps.setInt(1, idList);
				ps.setInt(2, listItems.get(i).getId_product().getId_product());
				ps.setInt(3, listItems.get(i).getQuantity());
				ps.executeUpdate();
			}
			
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		
	}

}
