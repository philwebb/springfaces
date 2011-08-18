package org.springframework.springfaces.traveladvisor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.Review;

public interface HotelService {

	Hotel getHotel(String key);

	Page<Review> getReviews(Hotel hotel, Pageable pageable);

	Review getReview(Hotel hotel, int reviewNumber);

	Review addReview(Hotel hotel, Review review);

}
