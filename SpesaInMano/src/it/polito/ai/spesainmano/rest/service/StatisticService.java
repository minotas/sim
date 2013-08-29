package it.polito.ai.spesainmano.rest.service;

import java.util.List;

import it.polito.ai.spesainmano.responses.Statistic;

public interface StatisticService {
	public List<Statistic> getAveragesLastSixMonths(int id_supermaket, int id_product);
}
