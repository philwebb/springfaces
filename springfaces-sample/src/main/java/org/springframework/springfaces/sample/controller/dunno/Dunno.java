package org.springframework.springfaces.sample.controller.dunno;

import java.util.List;
import java.util.Map;

import org.springframework.springfaces.sample.controller.BookingService;
import org.springframework.springfaces.sample.controller.Hotel;
import org.springframework.springfaces.sample.controller.SearchCriteria;

public class Dunno {

	private SearchCriteria searchCriteria;

	private BookingService bookingService;

	public Dunno(SearchCriteria searchCriteria, BookingService bookingService) {
		// TODO Auto-generated constructor stub
	}

	public List<Hotel> load(int first, int pageSize, String sortField, boolean sortOrder, Map<String, String> filters) {
		//		searchCriteria.setCurrentPage(first / pageSize + 1);
		//		return bookingService.findHotels(searchCriteria, first, sortField, sortOrder);
		return null;
	}

}
