package org.springframework.springfaces.traveladvisor.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

	@Embedded
	private ReviewDetails details;

	protected Review() {
	}

	public Review(Hotel hotel, int index, ReviewDetails details) {
		this.hotel = hotel;
		this.index = index;
		this.details = details;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public int getIndex() {
		return index;
	}

	public ReviewDetails getDetails() {
		return details;
	}

	public String getTitle() {
		return details.getTitle();
	}

	public Rating getRating() {
		return details.getRating();
	}
}
