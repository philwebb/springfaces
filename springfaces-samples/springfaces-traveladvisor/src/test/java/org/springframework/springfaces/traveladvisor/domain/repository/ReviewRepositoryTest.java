package org.springframework.springfaces.traveladvisor.domain.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/config/data-access-config.xml")
public class ReviewRepositoryTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ReviewRepository reviewRepository;

	private PageRequest pageable = new PageRequest(0, 10);

	private Hotel bathTravelodge;

	@Before
	public void setup() {
		this.bathTravelodge = entityManager.find(Hotel.class, 10L);
		assertThat(bathTravelodge.getName(), is("Bath Travelodge"));
	}

	@Test
	public void shouldFindByHotel() throws Exception {
		Page<Review> reviews = reviewRepository.findByHotel(bathTravelodge, pageable);
		assertThat(reviews.getTotalElements(), is(14L));
	}

	@Test
	public void shouldFindByHotelAndIndex() throws Exception {
		Review review = reviewRepository.findByHotelAndIndex(bathTravelodge, 8);
		assertThat(review.getTitle(), is("Very Noisy"));
	}

	@Test
	public void shouldSortReviews() throws Exception {
		pageable = new PageRequest(0, 10, new Sort(Direction.DESC, "rating"));
		reviewRepository.findByHotel(bathTravelodge, pageable);
		// FIXME assert
	}
}
