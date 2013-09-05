package it.polito.ai.spesainmano.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {
	private int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
}
