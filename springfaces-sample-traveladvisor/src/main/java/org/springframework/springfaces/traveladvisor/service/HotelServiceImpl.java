package org.springframework.springfaces.traveladvisor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.Rating;
import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.springfaces.traveladvisor.domain.ReviewDetails;
import org.springframework.stereotype.Component;

@Component("hotelService")
public class HotelServiceImpl implements HotelService {

	public Hotel getHotel(City city, String name) {
		return new Hotel(city, "Excelsior");
	}

	public Page<Review> getReviews(Hotel hotel, Pageable pageable) {
		List<Review> content = new ArrayList<Review>();
		content.add(new Review(hotel, 1, new ReviewDetails("Top hotel", Rating.EXCELLENT)));
		content.add(new Review(hotel, 2, new ReviewDetails("Too much building work", Rating.POOR)));
		return new PageImpl<Review>(content);
	}

	public Review getReview(Hotel hotel, int reviewNumber) {
		return new Review(hotel, 1, new ReviewDetails("Top hotel", Rating.EXCELLENT));
	}

	public Review addReview(Hotel hotel, ReviewDetails details) {
		System.out.println(details.getTitle());
		return new Review(hotel, 1, details);
	}

}
