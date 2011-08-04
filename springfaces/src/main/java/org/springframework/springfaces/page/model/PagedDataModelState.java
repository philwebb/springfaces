package org.springframework.springfaces.page.model;

import java.util.Map;

import org.springframework.springfaces.page.ui.PageaRequest;

public interface PagedDataModelState<E> extends PageaRequest {

	void setRowIndex(int rowIndex);

	void setPageSize(int pageSize);

	void setSortAscending(Boolean sortAscending);

	void setSortColumn(String sortColumn);

	void setFilters(Map<String, String> filters);
}