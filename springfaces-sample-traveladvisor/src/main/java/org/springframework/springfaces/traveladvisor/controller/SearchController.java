package org.springframework.springfaces.traveladvisor.controller;

import org.springframework.springfaces.mvc.navigation.annotation.NavigationMapping;
import org.springframework.springfaces.traveladvisor.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

	private SearchService searchService;

	@RequestMapping
	public String enterSearchDetails() {
		return "";
	}

	@NavigationMapping
	public String onSearch(SearchCriteria searchCriteria) {
		if (searchService.isCityNameExactMatch(searchCriteria.getName())) {
			return "@cityController.showCity";
		}
		return "@selectCity";
	}

	@RequestMapping
	public String selectCity() {
		return "";
	}

}
