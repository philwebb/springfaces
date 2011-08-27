package org.springframework.springfaces.traveladvisor.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.MathContext;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/config/data-access-config.xml")
public class HotelRepositoryTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private HotelRepository hotelRepository;

	private PageRequest pageable = new PageRequest(0, 10);

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
	public void shouldFindByCityAndName() throws Exception {
		String name = "Bath Travelodge";
		Hotel hotel = hotelRepository.findByCityAndName(bath, name);
		assertThat(hotel.getName(), is(name));
	}

	@Test
	public void shouldNotFindIfMalfomed() throws Exception {
		Hotel hotel = hotelRepository.findByCityAndName(bath, "missing");
		assertThat(hotel, is(nullValue()));
	}

	@Test
	public void shouldFindAverage() throws Exception {
		pageable = new PageRequest(0, 10, new Sort(Direction.ASC, "name"));
		Page<HotelSummary> hotels = hotelRepository.findByCity(bath, pageable);
		assertThat(hotels.getTotalElements(), is(2L));
		assertThat(hotels.getContent().get(0).getName(), is("Bath Travelodge"));
		double expected = (0 + 0 + 1 + 0 + 1 + 0 + 0 + 0 + 1 + 1 + 0 + 1 + 2 + 3) / 14.0;
		expected = new BigDecimal(expected, new MathContext(2)).doubleValue();
		assertThat(hotels.getContent().get(0).getAverageRating(), is(expected));
	}

	@Test
	public void shouldFindHotelsWithoutReview() throws Exception {
		Page<HotelSummary> hotels = hotelRepository.findByCity(tokyo, pageable);
		assertThat(hotels.getTotalElements(), is(1L));
		assertThat(hotels.getContent().get(0).getAverageRating(), is(nullValue()));
	}
}
