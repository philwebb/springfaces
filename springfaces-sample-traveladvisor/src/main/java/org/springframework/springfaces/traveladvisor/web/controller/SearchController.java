package org.springframework.springfaces.traveladvisor.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.annotation.NavigationMapping;
import org.springframework.springfaces.traveladvisor.service.SearchService;
import org.springframework.springfaces.traveladvisor.web.SearchCriteria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

	private SearchService searchService;

	@RequestMapping("/cities/search")
	public String enterSearchDetails(Model model) {
		model.addAttribute("searchCriteria", new SearchCriteria());
		return "search";
	}

	@NavigationMapping
	public Object onSearch(SearchCriteria searchCriteria) {
		if (searchService.isCityNameExactMatch(searchCriteria.getName())) {
			return "@cityController.showCity";
		}
		return new NavigationOutcome("@performSearch", "searchCriteria", searchCriteria);
	}

	@RequestMapping("/cities")
	public void performSearch(SearchCriteria searchCriteria, Model model) {
	}

	@Autowired
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

}
