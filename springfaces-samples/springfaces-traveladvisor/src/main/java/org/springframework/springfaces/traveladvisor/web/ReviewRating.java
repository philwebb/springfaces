/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
