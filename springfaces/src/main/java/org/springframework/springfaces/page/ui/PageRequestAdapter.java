package org.springframework.springfaces.page.ui;

import java.util.Collections;
import java.util.Map;

import org.springframework.springfaces.page.model.PagedDataModelState;
import org.springframework.util.Assert;

/**
 * Adapter class that converts {@link PagedDataModelState} to a {@link PageRequest}.
 * 
 * @author Phillip Webb
 */
class PageRequestAdapter implements PageRequest {

	private PagedDataModelState state;

	public PageRequestAdapter(PagedDataModelState state) {
		Assert.notNull(state, "State must not be null");
		this.state = state;
	}

	public int getPageNumber() {
		return state.getRowIndex() / getPageSize();
	}

	public int getPageSize() {
		return state.getPageSize();
	}

	public int getOffset() {
		return getPageNumber() * getPageSize();
	}

	public String getSortColumn() {
		return state.getSortColumn();
	}

	public boolean isSortAscending() {
		return state.isSortAscending();
	}

	public Map<String, String> getFilters() {
		Map<String, String> filters = state.getFilters();
		return (filters == null ? Collections.<String, String> emptyMap() : filters);
	}
}
