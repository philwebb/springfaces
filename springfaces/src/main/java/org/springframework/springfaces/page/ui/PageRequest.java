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
package org.springframework.springfaces.page.ui;

import java.util.Map;

/**
 * Pagination information made available by the {@link UIPagedData} component.
 * 
 * @author Phillip Webb
 * @see UIPagedData
 */
public interface PageRequest {

	/**
	 * Returns the page to be returned.
	 * @return the page to be returned.
	 */
	int getPageNumber();

	/**
	 * Returns the number of items to be returned.
	 * @return the number of items of that page
	 */
	int getPageSize();

	/**
	 * Returns the offset to be taken according to the underlying page and page size.
	 * @return the offset to be taken
	 */
	int getOffset();

	/**
	 * Returns the column used for sorting.
	 * @return the sort column
	 */
	String getSortColumn();

	/**
	 * Return if the sort should be in ascending order.
	 * @return the the sort is in ascending order
	 */
	boolean isSortAscending();

	/**
	 * Returns the filters that should be applied to the data. The key of the filter map is the column that should be
	 * filtered and the value is the filter that should be applied.
	 * @return the filters.
	 */
	Map<String, String> getFilters();
}
