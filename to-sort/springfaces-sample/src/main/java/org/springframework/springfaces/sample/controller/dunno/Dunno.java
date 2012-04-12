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
