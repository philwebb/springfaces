package org.springframework.springfaces.page;

import java.util.Map;

public interface MutablePageContext extends PageContext {

	void setPageSize(int pageSize);

	void setSortAscending(Boolean sortAscending);

	void setSortColumn(String sortColumn);

	void setFilters(Map<String, String> filters);
}
