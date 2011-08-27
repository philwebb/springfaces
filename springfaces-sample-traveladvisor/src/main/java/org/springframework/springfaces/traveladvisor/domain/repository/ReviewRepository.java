package org.springframework.springfaces.traveladvisor.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.Review;

public interface ReviewRepository extends Repository<Review, Long> {

	Page<Review> findByHotel(Hotel hotel, Pageable pageable);

	Review findByHotelAndIndex(Hotel hotel, int index);

}
