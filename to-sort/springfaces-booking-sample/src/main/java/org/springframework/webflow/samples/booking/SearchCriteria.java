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
package org.springframework.webflow.samples.booking;

import java.io.Serializable;

/**
 * A backing bean for the main hotel search form. Encapsulates the criteria needed to perform a hotel search.
 */
public class SearchCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The user-provided search criteria for finding Hotels.
	 */
	private String searchString = "";

	/**
	 * The maximum page size of the Hotel result list
	 */
	private int pageSize = 5;

	/**
	 * The page the user is currently on.
	 */
	private int currentPage = 1;

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String toString() {
		return "[Search Criteria searchString = '" + searchString + "'";
	}

}
