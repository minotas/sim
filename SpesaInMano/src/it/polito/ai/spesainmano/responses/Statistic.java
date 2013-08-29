package it.polito.ai.spesainmano.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Statistic {
	private String month;
	private double average;
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public double getAverage() {
		return average;
	}
	public void setAverage(double average) {
		this.average = average;
	}
	
	
}
