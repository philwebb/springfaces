package org.springframework.springfaces.traveladvisor.domain;

import java.util.Date;

//FIXME Hibernate bug HHH-5792 prevents this being embedded

public class ReviewDetails {

	private Rating rating;

	private Date checkInDate;

	private TripType tripType;

	private String title;

	private String details;

	public ReviewDetails() {
	}

	@Deprecated
	public ReviewDetails(String title, Rating rating) {
		this.title = title;
		this.rating = rating;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Date getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}

	public TripType getTripType() {
		return tripType;
	}

	public void setTripType(TripType tripType) {
		this.tripType = tripType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
