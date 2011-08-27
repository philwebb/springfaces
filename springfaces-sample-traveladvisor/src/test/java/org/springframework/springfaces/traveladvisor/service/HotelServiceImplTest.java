package org.springframework.springfaces.traveladvisor.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.HotelRepository;
import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.springfaces.traveladvisor.domain.ReviewRepository;

@RunWith(MockitoJUnitRunner.class)
public class HotelServiceImplTest {

	@InjectMocks
	private HotelServiceImpl hotelService = new HotelServiceImpl();

	@Mock
	private HotelRepository hotelRepository;

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

	@Test
	public void shouldGetHotel() throws Exception {
		given(hotelRepository.findByCityAndName(city, "name")).willReturn(hotel);
		assertThat(hotelService.getHotel(city, "name"), is(hotel));
	}

	@Test
	public void should() throws Exception {
		given(reviewRepository.findByHotel(hotel, pageable)).willReturn(reviews);
		assertThat(hotelService.getReviews(hotel, pageable), is(reviews));
	}
}
