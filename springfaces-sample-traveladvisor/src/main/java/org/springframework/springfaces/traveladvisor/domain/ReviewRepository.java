package org.springframework.springfaces.traveladvisor.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface ReviewRepository extends Repository<Review, Long> {

	Page<Review> findByHotel(Hotel hotel, Pageable pageable);

}
