package org.springframework.springfaces.traveladvisor.web;

import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewUtils {

	public double getRating(Review review) {
		return review.getRating().ordinal();
	}

	public String getDetailsSummary(Review review) {
		// FIXME cut with ...
		return review.getDetails();
	}

}
