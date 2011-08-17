package org.springframework.springfaces.traveladvisor.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class ReviewRatingValue {

	@Value("#{review}")
	private Review review;

	public Double getValue() {
		// FIXME
		return 0.0;
	}

	private void setValue(Double value) {
		// FIXME
	}

}
