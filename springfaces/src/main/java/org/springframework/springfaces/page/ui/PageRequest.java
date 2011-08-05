package org.springframework.springfaces.page.ui;

import java.util.Map;

public interface PageRequest {

	int getPageNumber();

	int getPageSize();

	int getOffset();

	String getSortColumn();

	boolean isSortAscending();

	Map<String, String> getFilters();

}
