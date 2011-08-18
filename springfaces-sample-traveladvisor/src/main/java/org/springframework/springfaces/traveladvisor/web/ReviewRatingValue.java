package org.springframework.springfaces.traveladvisor.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.springfaces.traveladvisor.domain.Rating;
import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class ReviewRatingValue {

	private static final Map<Rating, Double> VALUES;
	static {
		VALUES = new HashMap<Rating, Double>();
		VALUES.put(Rating.EXCELLENT, 4.0);
		VALUES.put(Rating.GOOD, 3.0);
		VALUES.put(Rating.AVERAGE, 2.0);
		VALUES.put(Rating.POOR, 1.0);
		VALUES.put(Rating.TERRIBLE, 0.0);

	}

	@Value("#{review}")
	private Review review;

	public Double getValue() {
		return VALUES.get(review.getRating());
	}

	private void setValue(Double value) {
		// FIXME
	}

}
