package org.springframework.springfaces.page;

import java.util.Map;

public interface Pageable {

	int getRowIndex();

	int getPageSize();

	Boolean getSortAscending();

	String getSortColumn();

	Map<String, String> getFilters();

}
