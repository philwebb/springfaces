package org.springframework.springfaces.page.ui;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StringUtils;

/**
 * Extends a {@link PageRequest} to support the Spring Data {@link Pageable} interface.
 * 
 * @author Phillip Webb
 */
public class SpringDataPageRequest implements PageRequest, Pageable {

	private PageRequest pageRequest;

	public SpringDataPageRequest(PageRequest pageRequest) {
		this.pageRequest = pageRequest;
	}

	public int getPageNumber() {
		return this.pageRequest.getPageNumber();
	}

	public int getPageSize() {
		return this.pageRequest.getPageSize();
	}

	public int getOffset() {
		return this.pageRequest.getOffset();
	}

	public String getSortColumn() {
		return this.pageRequest.getSortColumn();
	}

	public boolean isSortAscending() {
		return this.pageRequest.isSortAscending();
	}

	public Map<String, String> getFilters() {
		return this.pageRequest.getFilters();
	}

	public Sort getSort() {
		if (StringUtils.hasLength(getSortColumn())) {
			return new Sort(getSortDirection(), getSortColumn());
		}
		return null;
	}

	private Direction getSortDirection() {
		return (isSortAscending() ? Direction.ASC : Direction.DESC);
	}

}
