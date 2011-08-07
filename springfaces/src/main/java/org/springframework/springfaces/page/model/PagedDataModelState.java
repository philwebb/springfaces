package org.springframework.springfaces.page.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.springfaces.model.LazyDataModelState;
import org.springframework.util.Assert;

/**
 * State associated with a {@link PagedDataModel}.
 * 
 * @author Phillip Webb
 */
public class PagedDataModelState extends LazyDataModelState {

	private static final long serialVersionUID = 1L;

	private int pageSize;

	private boolean sortAscending;

	private String sortColumn;

	private Map<String, String> filters = new HashMap<String, String>();

	/**
	 * Create a new {@link PagedDataModelState} instance.
	 * @param pageSize the initial page size
	 */
	public PagedDataModelState(int pageSize) {
		setRowIndex(-1);
		setPageSize(pageSize);
		setSortAscending(true);
	}

	/**
	 * See {@link PagedDataRows#getPageSize} for details.
	 * @return the page size
	 * @see PagedDataRows#getPageSize()
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * See {@link PagedDataRows#setPageSize} for details.
	 * @param pageSize the page size
	 * @see PagedDataRows#setPageSize(int)
	 */
	public void setPageSize(int pageSize) {
		Assert.isTrue(pageSize >= 1, "PageSize must be a positive number");
		this.pageSize = pageSize;
	}

	/**
	 * See {@link PagedDataRows#isSortAscending} for details.
	 * @return if the sort is ascending
	 * @see PagedDataRows#isSortAscending()
	 */
	public boolean isSortAscending() {
		return sortAscending;
	}

	/**
	 * See {@link PagedDataRows#setSortAscending} for details.
	 * @param sortAscending the sort ascending value
	 * @see PagedDataRows#setSortAscending
	 */
	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}

	/**
	 * See {@link PagedDataRows#getSortColumn} for details.
	 * @return the sort column
	 * @see PagedDataRows#getSortColumn()
	 */
	public String getSortColumn() {
		return sortColumn;
	}

	/**
	 * See {@link PagedDataRows#getSortColumn} for details.
	 * @param sortColumn the sort column
	 * @see PagedDataRows#getSortColumn()
	 */
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	/**
	 * See {@link PagedDataRows#getFilters} for details.
	 * @return the filters
	 * @see PagedDataRows#getFilters()
	 */
	public Map<String, String> getFilters() {
		return filters;
	}

	/**
	 * See {@link PagedDataRows#setFilters} for details.
	 * @param filters the filters
	 * @see PagedDataRows#setFilters(Map)
	 */
	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}
}
