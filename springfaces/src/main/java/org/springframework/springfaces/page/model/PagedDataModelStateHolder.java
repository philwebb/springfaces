package org.springframework.springfaces.page.model;

import java.util.Map;

import org.springframework.springfaces.model.DataRows;
import org.springframework.springfaces.model.PagedDataRows;

/**
 * Strategy interface used by {@link PagedDataModelPage} to get and set state information.
 * 
 * @author Phillip Webb
 */
public interface PagedDataModelStateHolder {

	/**
	 * Returns the row index.
	 * @return the row index
	 * @see DataRows#getRowIndex()
	 */
	int getRowIndex();

	/**
	 * Set the row index.
	 * @param rowIndex the row index
	 * @see DataRows#setRowIndex(int)
	 */
	void setRowIndex(int rowIndex);

	/**
	 * Returns the page size.
	 * @return the page size
	 * @see PagedDataRows#getPageSize()
	 */
	int getPageSize();

	/**
	 * Set the page size.
	 * @param pageSize the page size
	 * @see PagedDataRows#setPageSize(int)
	 */
	void setPageSize(int pageSize);

	/**
	 * Returns if the sort is ascending
	 * @return if the sort is ascending
	 * @see PagedDataRows#getSortAscending()
	 */
	boolean isSortAscending();

	/**
	 * Set if the sort ascending value.
	 * @param sortAscending the sort ascending value
	 * @see PagedDataRows#setSortAscending(Boolean)
	 */
	void setSortAscending(boolean sortAscending);

	/**
	 * Returns the sort column
	 * @return the sort column
	 * @see PagedDataRows#getSortColumn()
	 */
	String getSortColumn();

	/**
	 * Set the sort column.
	 * @param sortColumn the sort column
	 * @see PagedDataRows#setSortColumn(String)
	 */
	void setSortColumn(String sortColumn);

	/**
	 * Returns the filters
	 * @return the filters
	 * @see PagedDataRows#getFilters()
	 */
	Map<String, String> getFilters();

	/**
	 * Set the filters.
	 * @param filters the filters
	 * @see PagedDataRows#setFilters(Map)
	 */
	void setFilters(Map<String, String> filters);
}