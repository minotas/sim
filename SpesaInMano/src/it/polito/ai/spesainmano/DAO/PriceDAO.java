package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.responses.Statistic;

import java.sql.SQLException;
import java.util.List;

public interface PriceDAO {

	public int insert(Price p) throws SQLException;
	
	public float[] checkPrice (Price p) throws SQLException;

	public List<Price> getProductPriceInNearSupermarkets(int id_user, int productId, float latitude, float longitude, int supermarketId) throws SQLException;

	public List<Statistic> getAverageLastSixMonths(int supermarketId, int productId) throws SQLException;

	public float[] getPriceQualityInfo( int productId)throws SQLException;

	public List<Price> getOffersMonitored(int idUser, float latitude, float longitude) throws SQLException;

	public List<Price> getGeneralOffers(float longitude, float latitude) throws SQLException;

	public List<Price> getOffersProductsInOneList(int idUser, float latitude, float longitude) throws SQLException;
	
	public List<Price> getOffersProductsInMultipleLists(int idUser, float latitude, float longitude) throws SQLException;
	
	
}
