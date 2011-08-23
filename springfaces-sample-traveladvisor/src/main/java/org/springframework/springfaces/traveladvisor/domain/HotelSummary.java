package org.springframework.springfaces.traveladvisor.domain;

public class HotelSummary {

	private Hotel hotel;
	private double averageRating;

	public HotelSummary(Hotel hotel, double averageRating) {
		this.hotel = hotel;
		this.averageRating = averageRating;
	}

	public City getCity() {
		return hotel.getCity();
	}

	public String getName() {
		return hotel.getName();
	}

	public double getAverageRating() {
		return averageRating;
	}

}
