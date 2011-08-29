package org.springframework.springfaces.traveladvisor.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.springfaces.traveladvisor.domain.Rating;
import org.springframework.springfaces.traveladvisor.domain.ReviewDetails;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class ReviewRating {

	@Value("#{review}")
	private ReviewDetails review;

	public Double getValue() {
		Rating rating = review.getRating();
		return (rating == null ? null : (double) rating.ordinal());
	}

	public void setValue(Double value) {
		Rating rating = value == null ? null : Rating.values()[value.intValue()];
		review.setRating(rating);
	}
}
