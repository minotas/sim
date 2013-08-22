package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.db.ConnectionPoolManager;
import it.polito.ai.spesainmano.model.ListItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ListItemDAOImp implements ListItemDAO{
	Connection con;

	@Override
	public void insertListItems(List<ListItem> listItems, int idList) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into list_item(id_market_list,id_product, quantity) values(?, ?, quantity)";
		try {
			ps = con.prepareStatement(query);
			int itemsNumber = listItems.size();
			for(int i = 1; i < itemsNumber; i++){
				ps.setInt(1, idList);
				ps.setInt(2, listItems.get(i).getId_product().getId_product());
				ps.setInt(3, listItems.get(i).getQuantity());
				ps.executeUpdate();
			}
			
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		
	}

}
