package org.springframework.springfaces.page.model;

import java.util.Map;

import javax.faces.model.DataModel;

import org.springframework.springfaces.model.LazyDataLoader;
import org.springframework.springfaces.model.LazyDataModel;
import org.springframework.util.ObjectUtils;

/**
 * A {@link DataModel} that supports sorting, filtering and paging capabilities.
 * 
 * @author Phillip Webb
 * 
 * @param <E>
 */
public class PagedDataModel<E> extends LazyDataModel<E, PagedDataModelState> implements PagedDataRows<E> {

	public PagedDataModel(LazyDataLoader<E, PagedDataModelState> loader, PagedDataModelState state) {
		super(loader, state);
	}

	public int getPageSize() {
		return getState().getPageSize();
	}

	public void setPageSize(int pageSize) {
		if (getPageSize() != pageSize) {
			setRowIndex(-1);
			getState().setPageSize(pageSize);
		}
	}

	public boolean getSortAscending() {
		return getState().isSortAscending();
	}

	public void setSortAscending(boolean sortAscending) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortAscending)) {
			setRowIndex(-1);
			getState().setSortAscending(sortAscending);
		}
	}

	public String getSortColumn() {
		return getState().getSortColumn();
	}

	public void setSortColumn(String sortColumn) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortColumn)) {
			setRowIndex(-1);
			getState().setSortColumn(sortColumn);
		}
	}

	public Map<String, String> getFilters() {
		return getState().getFilters();
	}

	public void setFilters(Map<String, String> filters) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), filters)) {
			setRowIndex(-1);
			getState().setFilters(filters);
		}
	}
}
