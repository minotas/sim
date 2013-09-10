package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.PriceDAO;
import it.polito.ai.spesainmano.DAOImp.PriceDAOImp;
import it.polito.ai.spesainmano.responses.Statistic;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.StatisticService;

/**
 * Defines the functions related with the statistics in the business logic
 * @version 1.0
 */
public class StatisticServiceImpl implements StatisticService {

	/**
	 * Implements the logic required to get the statistics of a product in a given supermarket in the last six months
	 * @param supermarketId The id of the Supermarket
	 * @param productId The id of the product
	 * @return a List of Statistic objects containing the average prices of the last six month
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when no statistics are found
	*/
	@Override
	public List<Statistic> getAveragesLastSixMonths(int id_supermaket, int id_product) {
		PriceDAO statisticDao = new PriceDAOImp();
		
		try {
			List<Statistic> statistics = statisticDao.getAverageLastSixMonths(id_supermaket, id_product);
			
			if(statistics.size() == 0){
				throw new CustomNotFoundException("No statistics found");
			}
			return statistics;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}
}