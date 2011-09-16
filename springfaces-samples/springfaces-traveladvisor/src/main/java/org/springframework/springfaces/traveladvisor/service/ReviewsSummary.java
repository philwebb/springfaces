package org.springframework.springfaces.traveladvisor.service;

import org.springframework.springfaces.traveladvisor.domain.Rating;

public interface ReviewsSummary {

	public long getNumberOfReviewsWithRating(Rating rating);

}
