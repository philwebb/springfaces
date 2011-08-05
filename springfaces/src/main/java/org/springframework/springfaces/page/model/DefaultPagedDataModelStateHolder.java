package org.springframework.springfaces.page.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * A default implementation for {@link PagedDataModelStateHolder}.
 * 
 * @author Phillip Webb
 */
public class DefaultPagedDataModelStateHolder implements PagedDataModelStateHolder, Serializable {

	private static final long serialVersionUID = 1L;

	private int rowIndex;
	private int pageSize;
	private boolean sortAscending;
	private String sortColumn;
	private Map<String, String> filters;

	public DefaultPagedDataModelStateHolder(int pageSize) {
		Assert.isTrue(pageSize >= 1, "PageSize must be a positive number");
		this.rowIndex = -1;
		this.pageSize = pageSize;
		this.sortAscending = true;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
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
