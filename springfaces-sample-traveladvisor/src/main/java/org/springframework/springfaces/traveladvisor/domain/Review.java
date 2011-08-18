package org.springframework.springfaces.traveladvisor.domain;

import java.util.Date;

public class Review {

	private int number;

	private Rating rating;

	private Date checkInDate;

	private TripType tripType;

	private String title;

	private String details;

	public Review() {
	}

	public Review(int number, String title, Rating rating) {
		this.number = number;
		this.title = title;
		this.rating = rating;
	}

	public int getNumber() {
		return number;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

}
