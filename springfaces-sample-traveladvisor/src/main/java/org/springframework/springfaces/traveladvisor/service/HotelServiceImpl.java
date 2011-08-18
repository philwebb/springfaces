package org.springframework.springfaces.traveladvisor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.Rating;
import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.stereotype.Component;

@Component("hotelService")
public class HotelServiceImpl implements HotelService {

	public Hotel getHotel(String key) {
		return new Hotel(key, "Excelsior");
	}

	public Page<Review> getReviews(Hotel hotel, Pageable pageable) {
		List<Review> content = new ArrayList<Review>();
		content.add(new Review(1, "Top hotel", Rating.EXCELLENT));
		content.add(new Review(2, "Too much building work", Rating.POOR));
		return new PageImpl<Review>(content);
	}

	public Review getReview(Hotel hotel, int reviewNumber) {
		return new Review(1, "Top hotel", Rating.EXCELLENT);
	}

	public Review addReview(Hotel hotel, Review review) {
		System.out.println(review.getTitle());
		return review;
	}

}
