package org.springframework.springfaces.traveladvisor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/config/data-access-config.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class DataAccessTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	public void testname() throws Exception {
		// City city = new City("name", "country");
		// Hotel hotel = new Hotel("key", "name");
		// hotel.setCity(city);
		// Review review = new Review(null, "title", Rating.AVERAGE);
		// review.setHotel(hotel);
		// entityManager.persist(city);
		// entityManager.persist(hotel);
		// entityManager.persist(review);
		// entityManager.flush();
		// System.out.println(city.getId());
		// System.out.println(hotel.getId());
		// System.out.println(review.getNumber());
	}

}
