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
			ExtendedModelMap model = new ExtendedModelMap().addAttribute("country", city.getCountry().toLowerCase())
					.addAttribute("name", city.getName().toLowerCase());
			return new NavigationOutcome("@showCity", model);
		}
		return "@performSearch";
	}

	private City findSingleCity(CitySearchCriteria criteria) {
		Page<City> cities = cityService.findCities(criteria, new PageRequest(0, 1));
		if (cities.getNumberOfElements() == 1) {
			return cities.getContent().get(0);
		}
		return null;
	}

	@RequestMapping("/advisor/cities")
	public String performSearch(CitySearchCriteria searchCriteria, Model model) {
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
