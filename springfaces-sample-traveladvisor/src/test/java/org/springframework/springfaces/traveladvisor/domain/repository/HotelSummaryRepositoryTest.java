package org.springframework.springfaces.traveladvisor.domain.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.number.OrderingComparisons.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparisons.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.HotelSummary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/config/data-access-config.xml")
public class HotelSummaryRepositoryTest {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	private HotelSummaryRepository hotelSummaryRepository;

	private Pageable pageable = new PageRequest(0, 10);

	private City bath;

	private City tokyo;

	@Before
	public void setup() {
		this.bath = entityManager.find(City.class, 9L);
		this.tokyo = entityManager.find(City.class, 6L);
		assertThat(bath.getName(), is("Bath"));
		assertThat(tokyo.getName(), is("Tokyo"));
	}

	@Test
	public void shouldFindAverage() throws Exception {
		pageable = new PageRequest(0, 10, new Sort(Direction.ASC, "name"));
		Page<HotelSummary> hotels = hotelSummaryRepository.findByCity(bath, pageable);
		assertThat(hotels.getTotalElements(), is(2L));
		assertThat(hotels.getContent().get(0).getName(), is("Bath Travelodge"));
		double expected = (0 + 0 + 1 + 0 + 1 + 0 + 0 + 0 + 1 + 1 + 0 + 1 + 2 + 3) / 14.0;
		expected = new BigDecimal(expected, new MathContext(2)).doubleValue();
		assertThat(hotels.getContent().get(0).getAverageRating(), is(expected));
	}

	@Test
	public void shouldFindHotelsWithoutReview() throws Exception {
		Page<HotelSummary> hotels = hotelSummaryRepository.findByCity(tokyo, pageable);
		assertThat(hotels.getTotalElements(), is(1L));
		assertThat(hotels.getContent().get(0).getAverageRating(), is(nullValue()));
	}

	@Test
	public void shouldSortByRatingAsc() throws Exception {
		pageable = new PageRequest(0, 100, new Sort(Direction.ASC, "averageRating"));
		Page<HotelSummary> page = hotelSummaryRepository.findByCity(bath, pageable);
		double rating = page.getContent().get(0).getAverageRating();
		for (HotelSummary hotelSummary : page) {
			assertThat(hotelSummary.getAverageRating(), is(greaterThanOrEqualTo(rating)));
		}
	}

	@Test
	public void shouldSortByRatingDesc() throws Exception {
		pageable = new PageRequest(0, 100, new Sort(Direction.DESC, "averageRating"));
		Page<HotelSummary> page = hotelSummaryRepository.findByCity(bath, pageable);
		double rating = page.getContent().get(0).getAverageRating();
		for (HotelSummary hotelSummary : page) {
			assertThat(hotelSummary.getAverageRating(), is(lessThanOrEqualTo(rating)));
		}
	}

	@Test
	public void shouldSortByHotelName() throws Exception {
		pageable = new PageRequest(0, 100, new Sort(Direction.ASC, "name"));
		Page<HotelSummary> page = hotelSummaryRepository.findByCity(bath, pageable);
		List<String> names = new ArrayList<String>();
		for (HotelSummary summary : page) {
			names.add(summary.getName());
		}
		String hotels = StringUtils.collectionToCommaDelimitedString(names);
		assertThat(hotels, is(equalTo("Bath Travelodge,The Bath Priory Hotel")));
	}

}
