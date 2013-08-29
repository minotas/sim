package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.responses.Statistic;

import java.sql.SQLException;
import java.util.List;

public interface StatisticDAO {

	public List<Statistic> getAverageLastSixMonths(int supermarketId, int productId) throws SQLException;
}
