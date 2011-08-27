package org.springframework.springfaces.traveladvisor.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface CityRepository extends Repository<City, Long> {

	@Query("select c from City c where upper(c.name) like upper(?1)")
	Page<City> findByNameLike(String name, Pageable pageable);

	City findByNameAndCountry(String name, String country);

	@Query(value = "select new org.springframework.springfaces.traveladvisor.domain.HotelSummary(h.city, h.name, avg(r.details.rating)) "
			+ "from Hotel h left outer join h.reviews r where h.city = ?1 group by h", countQuery = "select count(h) from Hotel h where h.city = ?1")
	Page<HotelSummary> getHotels(City city, Pageable pageable);

}
