package org.springframework.springfaces.traveladvisor.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Review {

	@Id
	@GeneratedValue
	private Long id;

	private Integer number;

	@ManyToOne(optional = false)
	private Hotel hotel;

	private Rating rating;

	private Date checkInDate;

	private TripType tripType;

	private String title;

	private String details;

	public Review() {
	}

	public Review(Integer number, String title, Rating rating) {
		this.number = number;
		this.title = title;
		this.rating = rating;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Integer getNumber() {
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
