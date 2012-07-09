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
@ContextConfiguration("classpath:/META-INF/spring/data-access-config.xml")
public class HotelRepositoryTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private HotelRepository hotelRepository;

	private City bath;

	@Before
	public void setup() {
		this.bath = this.entityManager.find(City.class, 9L);
		assertThat(this.bath.getName(), is("Bath"));
	}

	@Test
	public void shouldFindByCityAndName() throws Exception {
		String name = "Bath Travelodge";
		Hotel hotel = this.hotelRepository.findByCityAndName(this.bath, name);
		assertThat(hotel.getName(), is(name));
	}

	@Test
	public void shouldNotFindIfMalfomed() throws Exception {
		Hotel hotel = this.hotelRepository.findByCityAndName(this.bath, "missing");
		assertThat(hotel, is(nullValue()));
	}
}
