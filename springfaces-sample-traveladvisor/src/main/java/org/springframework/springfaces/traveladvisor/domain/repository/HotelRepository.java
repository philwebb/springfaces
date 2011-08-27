package org.springframework.springfaces.traveladvisor.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.HotelSummary;

public interface HotelRepository extends Repository<Hotel, Long> {

	@Query(value = "select new org.springframework.springfaces.traveladvisor.domain.HotelSummary(h.city, h.name, avg(r.details.rating)) "
			+ "from Hotel h left outer join h.reviews r where h.city = ?1 group by h", countQuery = "select count(h) from Hotel h where h.city = ?1")
	Page<HotelSummary> findByCity(City city, Pageable pageable);

	Hotel findByCityAndName(City city, String name);

}
