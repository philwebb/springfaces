package org.springframework.springfaces.traveladvisor.service;

public class HotelSummary {

	private String key;
	private String name;
	private int averageRating;

	public HotelSummary(String key, String name, int averageRating) {
		this.key = key;
		this.name = name;
		this.averageRating = averageRating;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public int getAverageRating() {
		return averageRating;
	}

}
