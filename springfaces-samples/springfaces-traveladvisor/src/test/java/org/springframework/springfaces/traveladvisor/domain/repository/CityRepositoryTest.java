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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/spring/data-access-config.xml")
public class CityRepositoryTest {

	@Autowired
	private CityRepository cityRepository;

	private PageRequest pageable = new PageRequest(0, 10);

	@Test
	public void shouldFindNotResultsByName() throws Exception {
		Page<City> page = this.cityRepository.findByNameLikeAndCountryLikeAllIgnoringCase("notfound%", "%%",
				this.pageable);
		assertThat(page.getTotalElements(), is(0L));
	}

	@Test
	public void shouldFindSingleResultByName() throws Exception {
		Page<City> page = this.cityRepository.findByNameLikeAndCountryLikeAllIgnoringCase("Bath%", "%%", this.pageable);
		assertThat(page.getTotalElements(), is(1L));
		assertThat(page.getContent().get(0).getName(), is("Bath"));
	}

	@Test
	public void shouldFindByNameIgnoringCase() throws Exception {
		Page<City> page = this.cityRepository.findByNameLikeAndCountryLikeAllIgnoringCase("bAtH%", "%%", this.pageable);
		assertThat(page.getTotalElements(), is(1L));
		assertThat(page.getContent().get(0).getName(), is("Bath"));
	}

	@Test
	public void shouldFindMoreThanOneCity() throws Exception {
		Page<City> page = this.cityRepository.findByNameLikeAndCountryLikeAllIgnoringCase("Melbourne%", "%%",
				this.pageable);
		assertThat(page.getTotalElements(), is(2L));
	}

	@Test
	public void shouldSortFindingMoreThanOneCity() throws Exception {
		this.pageable = new PageRequest(0, 10, new Sort(Direction.ASC, "name", "country"));
		Page<City> page1 = this.cityRepository.findByNameLikeAndCountryLikeAllIgnoringCase("Melbourne%", "%%",
				this.pageable);
		this.pageable = new PageRequest(0, 10, new Sort(Direction.DESC, "name", "country"));
		Page<City> page2 = this.cityRepository.findByNameLikeAndCountryLikeAllIgnoringCase("Melbourne%", "%%",
				this.pageable);
		assertThat(page1.getTotalElements(), is(2L));
		assertThat(page1.getContent().get(0).getCountry(), is(page2.getContent().get(1).getCountry()));
	}

	@Test
	public void shouldFindByNameAndCountryLike() throws Exception {
		Page<City> page = this.cityRepository.findByNameLikeAndCountryLikeAllIgnoringCase("Melbourne%", "%Aus%",
				this.pageable);
		assertThat(page.getTotalElements(), is(1L));
	}

	@Test
	public void shouldFindByNameAndCountry() throws Exception {
		City melbourneUsa = this.cityRepository.findByNameAndCountryAllIgnoringCase("Melbourne", "USA");
		City melbourneAustralia = this.cityRepository.findByNameAndCountryAllIgnoringCase("Melbourne", "Australia");
		assertThat(melbourneUsa.getName(), is("Melbourne"));
		assertThat(melbourneUsa.getCountry(), is("USA"));
		assertThat(melbourneAustralia.getName(), is("Melbourne"));
		assertThat(melbourneAustralia.getCountry(), is("Australia"));
	}

	@Test
	public void shouldReturnNullIfNotFoundByNameAndCountry() throws Exception {
		City city = this.cityRepository.findByNameAndCountryAllIgnoringCase("Melbourne", "UK");
		assertThat(city, is(nullValue()));
	}
}
