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
package org.springframework.springfaces.page.model;

import java.util.Map;

import javax.faces.component.UIData;
import javax.faces.model.DataModel;

import org.springframework.springfaces.model.DataRows;
import org.springframework.springfaces.page.ui.PageRequest;

/**
 * Extension of {@link DataRows} that provides additional sorting, filtering and paging capabilities. This interface is
 * mainly provided to allow consistent access to paging functionality across different {@link DataModel} subclasses.
 * 
 * @author Phillip Webb
 * @param <E> The element type
 */
public interface PagedDataRows<E> extends DataRows<E> {

	/**
	 * Returns the number of rows contained in a page. It is recommended that this value is bound to the
	 * {@link UIData#setRows(int) rows} property of {@link UIData} to ensure that the number of rows displayed is the
	 * same as the number that is fetched.
	 * @return the page size
	 * @see PageRequest#getPageSize()
	 */
	int getPageSize();

	/**
	 * Sets the number of rows contained in a page.
	 * @param pageSize the page size.
	 * @see PageRequest#getPageSize()
	 */
	void setPageSize(int pageSize);

	/**
	 * Returns the sort column.
	 * @return the sort column
	 * @see PageRequest#getSortColumn()
	 */
	String getSortColumn();

	/**
	 * Sets the sort column.
	 * @param sortColumn
	 * @see PageRequest#getSortColumn()
	 */
	void setSortColumn(String sortColumn);

	/**
	 * Returns <tt>true</tt> if sorting is in ascending order or <tt>false</tt> if sorting in descending order.
	 * @return if the sort is in ascending order
	 * @see PageRequest#isSortAscending()
	 */
	boolean isSortAscending();

	/**
	 * Set if sorting is in ascending order.
	 * @param sortAscending the sort order
	 * @see PageRequest#isSortAscending()
	 */
	void setSortAscending(boolean sortAscending);

	/**
	 * Toggle sorting parameters. If the rows are already sorted by the specified <tt>sortColumn</tt> then the
	 * {@link #isSortAscending() sort order} is toggled. If the specified <tt>sortColumn</tt> differs from the
	 * {@link #getSortColumn() current column} then the sort column is updates and the order is set to ascending.
	 * @param sortColumn the sort column
	 */
	void toggleSort(String sortColumn);

	/**
	 * Returns the filters that should be applied to the data. The key of the filter map is the column that should be
	 * filtered and the value is the filter that should be applied.
	 * @return the filters.
	 */
	Map<String, String> getFilters();

	/**
	 * Sets filters that should be applied to the data. The key of the filter map is the column that should be filtered
	 * and the value is the filter that should be applied.
	 * @param filters the filter to apply
	 */
	void setFilters(Map<String, String> filters);
}
