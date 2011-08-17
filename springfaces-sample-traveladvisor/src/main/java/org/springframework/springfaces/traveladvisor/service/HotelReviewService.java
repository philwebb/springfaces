package org.springframework.springfaces.traveladvisor.service;

import org.springframework.springfaces.traveladvisor.domain.Review;

public interface HotelReviewService {

	void add(Review review);

	Review findById(long id);

}
