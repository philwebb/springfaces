package org.springframework.springfaces.page;

import java.util.Map;

public interface PageContext {

	int getPageSize();

	Boolean getSortAscending();

	String getSortColumn();

	Map<String, String> getFilters();

}
