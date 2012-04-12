/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.traveladvisor.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.springfaces.traveladvisor.domain.City;

public interface CityRepository extends Repository<City, Long> {

	// FIXME Drop @Query when SD patch applied
	@Query("select c from City c where upper(c.name) like upper(?1) and upper(c.country) like upper(?2)")
	Page<City> findByNameAndCountryLikeAllIgnoringCase(String name, String country, Pageable pageable);

	// FIXME ignore case?
	City findByNameAndCountry(String name, String country);

}
