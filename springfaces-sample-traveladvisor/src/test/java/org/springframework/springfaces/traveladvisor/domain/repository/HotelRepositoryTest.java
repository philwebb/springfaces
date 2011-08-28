package org.springframework.springfaces.traveladvisor.domain.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/config/data-access-config.xml")
public class HotelRepositoryTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private HotelRepository hotelRepository;

	private City bath;

	@Before
	public void setup() {
		this.bath = entityManager.find(City.class, 9L);
		assertThat(bath.getName(), is("Bath"));
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
}
