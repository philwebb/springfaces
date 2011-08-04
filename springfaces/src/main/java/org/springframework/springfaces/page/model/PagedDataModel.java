package org.springframework.springfaces.page.model;

import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.springframework.springfaces.model.PagedDataRows;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class PagedDataModel<E> extends DataModel<E> implements PagedDataRows<E> {

	private PagedDataModelState<E> state;

	private PagedDataModelPageProvider<E> provider;

	private PagedDataModelPage<E> cachedPage;

	public PagedDataModel(PagedDataModelState<E> state, PagedDataModelPageProvider<E> pageProvider) {
		Assert.notNull(state, "state must not be null");
		Assert.notNull(pageProvider, "PageProvider must not be null");
		this.state = state;
		this.provider = pageProvider;
	}

	@Override
	public boolean isRowAvailable() {
		return getPage().containsRowIndex(getRowIndex());
	}

	@Override
	public int getRowCount() {
		long rowCount = getAnyNonEmptyPage().getRowCount();
		// FIXME check bounds
		return (int) rowCount;
	}

	@Override
	public E getRowData() {
		return getPage().getRowData(getRowIndex());
	}

	@Override
	public int getRowIndex() {
		return this.state.getRowIndex();
	}

	@Override
	public void setRowIndex(int rowIndex) {
		if (getRowIndex() != rowIndex) {
			Assert.isTrue(rowIndex >= -1, "rowIndex must not be less than -1");
			this.state.setRowIndex(rowIndex);
			fireDataModelListeners();
		}
	}

	private void fireDataModelListeners() {
		DataModelListener[] listeners = getDataModelListeners();
		if (listeners == null || listeners.length == 0) {
			return;
		}
		Object rowData = (isRowAvailable() ? getRowData() : null);
		DataModelEvent event = new DataModelEvent(this, getRowIndex(), rowData);
		for (DataModelListener listener : listeners) {
			if (listener != null) {
				listener.rowSelected(event);
			}
		}
	}

	@Override
	public Object getWrappedData() {
		return getPage();
	}

	@Override
	public void setWrappedData(Object o) {
		throw new UnsupportedOperationException("Unable to set wrapped data for LazyPagedDataModel");
	}

	public int getPageSize() {
		return state.getPageSize();
	}

	public void setPageSize(int pageSize) {
		if (getPageSize() != pageSize) {
			setRowIndex(-1);
			state.setPageSize(pageSize);
		}
	}

	public Boolean getSortAscending() {
		return state.getSortAscending();
	}

	public void setSortAscending(Boolean sortAscending) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortAscending)) {
			setRowIndex(-1);
			state.setSortAscending(sortAscending);
		}
	}

	public String getSortColumn() {
		return state.getSortColumn();
	}

	public void setSortColumn(String sortColumn) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortColumn)) {
			setRowIndex(-1);
			state.setSortColumn(sortColumn);
		}
	}

	public Map<String, String> getFilters() {
		return state.getFilters();
	}

	public void setFilters(Map<String, String> filters) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), filters)) {
			setRowIndex(-1);
			state.setFilters(filters);
		}
	}

	private PagedDataModelPage<E> getPage() {
		return getPage(getRowIndex());
	}

	private PagedDataModelPage<E> getAnyNonEmptyPage() {
		return getPage(getRowIndex() == -1 ? 0 : getRowIndex());
	}

	private PagedDataModelPage<E> getPage(int rowIndex) {
		if (rowIndex == -1) {
			return emptyDataPage();
		}
		if (cachedPage == null || !cachedPage.containsRowIndex(rowIndex)) {
			cachedPage = provider.getPage(state);
		}
		return cachedPage;
	}

	@SuppressWarnings("unchecked")
	private static <E> PagedDataModelPage<E> emptyDataPage() {
		return (PagedDataModelPage<E>) PagedDataModelPage.EMPTY;
	}
}
