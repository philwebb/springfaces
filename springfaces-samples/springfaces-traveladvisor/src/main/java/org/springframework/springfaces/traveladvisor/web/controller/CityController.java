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
package org.springframework.springfaces.traveladvisor.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.annotation.NavigationMapping;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.service.CitySearchCriteria;
import org.springframework.springfaces.traveladvisor.service.CityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CityController {

	private CityService cityService;

	@RequestMapping("/advisor/cities/search")
	public String enterSearchDetails(Model model) {
		model.addAttribute(new CitySearchCriteria());
		return "citysearch";
	}

	@NavigationMapping
	public Object onSearch(CitySearchCriteria criteria) {
		City city = findSingleCity(criteria);
		if (city != null) {
			ExtendedModelMap model = new ExtendedModelMap().addAttribute("country", city.getCountry()).addAttribute(
					"name", city.getName());
			return new NavigationOutcome("@showCity", model);
		}
		return "@performSearch";
	}

	private City findSingleCity(CitySearchCriteria criteria) {
		Page<City> cities = cityService.findCities(criteria, new PageRequest(0, 1));
		if (cities.getTotalElements() == 1L) {
			return cities.getContent().get(0);
		}
		return null;
	}

	@RequestMapping("/advisor/cities")
	public String performSearch(CitySearchCriteria searchCriteria, Model model) {
		Page<City> cities = cityService.findCities(searchCriteria, null);
		model.addAttribute("cities", cities.getContent());
		return "cities";
	}

	@RequestMapping("/advisor/{country}/{name}")
	public String showCity(@PathVariable String country, @PathVariable String name, Model model) {
		model.addAttribute(cityService.getCity(name, country));
		return "city";
	}

	@Autowired
	public void setCityService(CityService cityService) {
		this.cityService = cityService;
	}

}
