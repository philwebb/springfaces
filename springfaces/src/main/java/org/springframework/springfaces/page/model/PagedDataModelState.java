package org.springframework.springfaces.page.model;

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

	private Map<String, String> filters;

	/**
	 * Create a new {@link PagedDataModelState} instance.
	 * @param pageSize the initial page size
	 */
	public PagedDataModelState(int pageSize) {
		Assert.isTrue(pageSize >= 1, "PageSize must be a positive number");
		setRowIndex(-1);
		this.pageSize = pageSize;
		this.sortAscending = true;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isSortAscending() {
		return sortAscending;
	}

	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}

	public String getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}
}
