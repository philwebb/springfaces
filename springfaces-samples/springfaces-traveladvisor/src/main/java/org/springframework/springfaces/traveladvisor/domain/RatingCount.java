package org.springframework.springfaces.traveladvisor.domain;

public class RatingCount {

	private Rating rating;

	private long count;

	public RatingCount(Rating rating, long count) {
		this.rating = rating;
		this.count = count;
	}

	public Rating getRating() {
		return rating;
	}

	public long getCount() {
		return count;
	}
}
