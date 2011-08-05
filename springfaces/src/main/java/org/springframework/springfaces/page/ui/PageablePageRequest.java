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
public class PageablePageRequest implements PageRequest, Pageable {

	private PageRequest pageRequest;

	public PageablePageRequest(PageRequest pageRequest) {
		this.pageRequest = pageRequest;
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

	public int getPageNumber() {
		return pageRequest.getPageNumber();
	}

	public int getPageSize() {
		return pageRequest.getPageSize();
	}

	public int getOffset() {
		return pageRequest.getOffset();
	}

	public String getSortColumn() {
		return pageRequest.getSortColumn();
	}

	public boolean isSortAscending() {
		return pageRequest.isSortAscending();
	}

	public Map<String, String> getFilters() {
		return pageRequest.getFilters();
	}
}
