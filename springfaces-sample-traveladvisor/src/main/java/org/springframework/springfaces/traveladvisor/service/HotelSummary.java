package org.springframework.springfaces.traveladvisor.service;

import org.springframework.springfaces.traveladvisor.domain.City;

public class HotelSummary {

	private City city;
	private String name;
	private int averageRating;

	public HotelSummary(City city, String name, int averageRating) {
		this.city = city;
		this.name = name;
		this.averageRating = averageRating;
	}

	public City getCity() {
		return city;
	}

	public String getName() {
		return name;
	}

	public int getAverageRating() {
		return averageRating;
	}

}
