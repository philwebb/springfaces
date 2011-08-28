package org.springframework.springfaces.traveladvisor.domain.repository;

import org.springframework.data.repository.Repository;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.Hotel;

public interface HotelRepository extends Repository<Hotel, Long> {

	Hotel findByCityAndName(City city, String name);

}
