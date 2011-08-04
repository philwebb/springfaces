package org.springframework.springfaces.page.ui;

import java.util.Map;

public interface PageaRequest {

	int getRowIndex();

	int getPageNumber();

	int getPageSize();

	int getOffset();

	Boolean getSortAscending();

	String getSortColumn();

	Map<String, String> getFilters();

}
