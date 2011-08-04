package org.springframework.springfaces.model;

import java.util.Map;

import javax.faces.component.UIData;


/**
 * Extension of {@link DataRows} that provides additional sorting, filtering and paging capabilities.
 * 
 * @author Phillip Webb
 * 
 * @param <E>
 */
public interface PagedDataRows<E> extends DataRows<E> {

	/**
	 * Returns the number of rows contained in a page. It is recommended that this value is bound to the
	 * {@link UIData#setRows(int) rows} property of {@link UIData} to ensure that the number of rows displayed is the
	 * same as the number that is fetched.
	 * @return the page size
	 */
	int getPageSize();

	/**
	 * Sets the number of rows contained in a page.
	 * @param pageSize the page size.
	 */
	void setPageSize(int pageSize);

	// FIXME do we want an enum?
	Boolean getSortAscending();

	void setSortAscending(Boolean sortAscending);

	// FIXME do we want to name this sort field
	String getSortColumn();

	void setSortColumn(String sortColumn);

	// FIXME DC this is a map of field -> filter value
	Map<String, String> getFilters();

	void setFilters(Map<String, String> filters);
}