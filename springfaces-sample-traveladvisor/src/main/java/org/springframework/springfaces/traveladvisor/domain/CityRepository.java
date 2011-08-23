package org.springframework.springfaces.traveladvisor.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface CityRepository extends Repository<City, Long> {

	@Query("select c from City c where upper(c.name) like upper(?1)")
	Page<City> findByNameLike(String name, Pageable pageable);

	City findByNameAndCountry(String name, String country);

	@Query("select new org.springframework.springfaces.traveladvisor.domain.HotelSummary(r.hotel, avg(r.details.rating)) "
			+ "from Review r where r.hotel.city = ?1 group by r.hotel")
	Page<HotelSummary> getHotels(City city, Pageable pageable);

}
