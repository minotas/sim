package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.ListItem;

import java.sql.SQLException;
import java.util.List;

public interface ListItemDAO {
	
	public void insertListItems(List<ListItem> listItems, int idList) throws SQLException;

}
