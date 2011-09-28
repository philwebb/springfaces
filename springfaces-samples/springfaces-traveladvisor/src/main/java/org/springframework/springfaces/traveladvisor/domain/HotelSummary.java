package org.springframework.springfaces.traveladvisor.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class HotelSummary {

	private static final MathContext MATH_CONTEXT = new MathContext(2, RoundingMode.HALF_UP);

	private City city;

	private String name;

	private Double averageRating;

	public HotelSummary(City city, String name, Double averageRating) {
		this.city = city;
		this.name = name;
		this.averageRating = averageRating == null ? null : new BigDecimal(averageRating, MATH_CONTEXT).doubleValue();
	}

	public City getCity() {
		return city;
	}

	public String getName() {
		return name;
	}

	public Double getAverageRating() {
		return averageRating;
	}
}
