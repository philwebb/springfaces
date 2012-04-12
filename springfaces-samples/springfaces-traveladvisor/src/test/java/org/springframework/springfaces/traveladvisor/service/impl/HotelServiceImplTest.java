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
package org.springframework.springfaces.traveladvisor.service.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.Rating;
import org.springframework.springfaces.traveladvisor.domain.RatingCount;
import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.springfaces.traveladvisor.domain.repository.HotelRepository;
import org.springframework.springfaces.traveladvisor.domain.repository.HotelSummaryRepository;
import org.springframework.springfaces.traveladvisor.domain.repository.ReviewRepository;
import org.springframework.springfaces.traveladvisor.service.ReviewsSummary;

@RunWith(MockitoJUnitRunner.class)
public class HotelServiceImplTest {

	@InjectMocks
	private HotelServiceImpl hotelService = new HotelServiceImpl();

	@Mock
	private HotelRepository hotelRepository;

	@Mock
	private HotelSummaryRepository hotelSummaryRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private City city;

	@Mock
	private Hotel hotel;

	@Mock
	private Pageable pageable;

	@Mock
	private Page<Review> reviews;

	@Mock
	private Review review;

	@Test
	public void shouldGetHotel() throws Exception {
		given(hotelRepository.findByCityAndName(city, "name")).willReturn(hotel);
		assertThat(hotelService.getHotel(city, "name"), is(hotel));
	}

	@Test
	public void shouldGetReviews() throws Exception {
		given(reviewRepository.findByHotel(hotel, pageable)).willReturn(reviews);
		assertThat(hotelService.getReviews(hotel, pageable), is(reviews));
	}

	@Test
	public void shouldGetReview() throws Exception {
		given(reviewRepository.findByHotelAndIndex(hotel, 1)).willReturn(review);
		assertThat(hotelService.getReview(hotel, 1), is(review));
	}

	@Test
	public void shouldGetReviewSummary() throws Exception {
		List<RatingCount> ratingCounts = new ArrayList<RatingCount>();
		ratingCounts.add(new RatingCount(Rating.EXCELLENT, 10));
		ratingCounts.add(new RatingCount(Rating.AVERAGE, 9));
		ratingCounts.add(new RatingCount(Rating.GOOD, 8));
		given(hotelSummaryRepository.findRatingCounts(hotel)).willReturn(ratingCounts);
		ReviewsSummary summary = hotelService.getReviewSummary(hotel);
		assertThat(summary.getNumberOfReviewsWithRating(Rating.EXCELLENT), is(10L));
		assertThat(summary.getNumberOfReviewsWithRating(Rating.AVERAGE), is(9L));
		assertThat(summary.getNumberOfReviewsWithRating(Rating.GOOD), is(8L));
		assertThat(summary.getNumberOfReviewsWithRating(Rating.POOR), is(0L));
	}
}
