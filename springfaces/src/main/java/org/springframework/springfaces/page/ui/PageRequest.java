package org.springframework.springfaces.page.ui;

import java.util.Map;

/**
 * Pagination information made available by the {@link UIPagedData} component.
 * 
 * @see UIPagedData
 * @author Phillip Webb
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
