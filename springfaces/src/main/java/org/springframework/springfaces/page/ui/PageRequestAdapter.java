package org.springframework.springfaces.page.ui;

import java.util.Collections;
import java.util.Map;

import org.springframework.springfaces.page.model.PagedDataModelState;

class PageRequestAdapter implements PageRequest {

	private PagedDataModelState stateHolder;

	public PageRequestAdapter(PagedDataModelState stateHolder) {
		this.stateHolder = stateHolder;
	}

	public int getPageNumber() {
		return stateHolder.getRowIndex() / getPageSize();
	}

	public int getPageSize() {
		return stateHolder.getPageSize();
	}

	public int getOffset() {
		return getPageNumber() * getPageSize();
	}

	public String getSortColumn() {
		return stateHolder.getSortColumn();
	}

	public boolean isSortAscending() {
		return stateHolder.isSortAscending();
	}

	public Map<String, String> getFilters() {
		Map<String, String> filters = stateHolder.getFilters();
		return (filters == null ? Collections.<String, String> emptyMap() : filters);
	}
}
