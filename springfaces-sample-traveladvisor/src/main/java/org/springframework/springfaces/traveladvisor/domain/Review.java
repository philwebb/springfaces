package org.springframework.springfaces.traveladvisor.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.util.Assert;

@Entity
public class Review {

	@Id
	@GeneratedValue
	@SuppressWarnings("unused")
	private Long id;

	@ManyToOne(optional = false)
	private Hotel hotel;

	@Column(nullable = false)
	private int index;

	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private Rating rating;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date checkInDate;

	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private TripType tripType;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	@Lob
	private String details;

	protected Review() {
	}

	public Review(Hotel hotel, int index, ReviewDetails details) {
		Assert.notNull(hotel, "Hotel must not be null");
		Assert.notNull(details, "Details must not be null");
		this.hotel = hotel;
		this.index = index;
		this.rating = details.getRating();
		this.checkInDate = details.getCheckInDate();
		this.tripType = details.getTripType();
		this.title = details.getTitle();
		this.details = details.getDetails();
	}

	public Hotel getHotel() {
		return hotel;
	}

	public int getIndex() {
		return index;
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
