package org.springframework.springfaces.traveladvisor.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface CityRepository extends Repository<City, Long> {

	// FIXME Drop @Query when SD patch applied
	@Query("select c from City c where upper(c.name) like upper(?1) and upper(c.country) like upper(?2)")
	Page<City> findByNameAndCountryLikeAllIgnoringCase(String name, String country, Pageable pageable);

	// FIXME ignore case?
	City findByNameAndCountry(String name, String country);

}
