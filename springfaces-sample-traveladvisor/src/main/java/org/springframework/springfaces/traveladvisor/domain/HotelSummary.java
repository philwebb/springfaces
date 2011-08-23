package org.springframework.springfaces.traveladvisor.domain;

public class HotelSummary {

	private Hotel hotel;
	private Double averageRating;

	public HotelSummary(Hotel hotel, Double averageRating) {
		this.hotel = hotel;
		this.averageRating = averageRating;
	}

	public City getCity() {
		return hotel.getCity();
	}

	public String getName() {
		return hotel.getName();
	}

	public Double getAverageRating() {
		return averageRating;
	}

}
