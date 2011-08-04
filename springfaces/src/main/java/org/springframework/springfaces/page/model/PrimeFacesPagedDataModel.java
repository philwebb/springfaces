package org.springframework.springfaces.page.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelListener;
import javax.swing.SortOrder;

import org.springframework.springfaces.model.PagedDataRows;

//FIXME change to extend PF
public class PrimeFacesPagedDataModel<E> extends DataModel<E> implements PagedDataRows<E> {

	private PagedDataModel<E> delegate;

	public PrimeFacesPagedDataModel(PagedDataModelState<E> state, PagedDataModelPageProvider<E> pageProvider) {
		delegate = new PagedDataModel<E>(state, pageProvider);
	}

	public boolean isRowAvailable() {
		return delegate.isRowAvailable();
	}

	public int getRowCount() {
		return delegate.getRowCount();
	}

	public E getRowData() {
		return delegate.getRowData();
	}

	public int getRowIndex() {
		return delegate.getRowIndex();
	}

	public void setRowIndex(int rowIndex) {
		delegate.setRowIndex(rowIndex);
	}

	public Object getWrappedData() {
		return delegate.getWrappedData();
	}

	public void setWrappedData(Object o) {
		// Ignore the wrapped data provided by primefaces
	}

	public int getPageSize() {
		return delegate.getPageSize();
	}

	public void setPageSize(int pageSize) {
		delegate.setPageSize(pageSize);
	}

	public Boolean getSortAscending() {
		return delegate.getSortAscending();
	}

	public void setSortAscending(Boolean sortAscending) {
		delegate.setSortAscending(sortAscending);
	}

	public String getSortColumn() {
		return delegate.getSortColumn();
	}

	public void setSortColumn(String sortColumn) {
		delegate.setSortColumn(sortColumn);

	}

	public Map<String, String> getFilters() {
		return delegate.getFilters();
	}

	public void setFilters(Map<String, String> filters) {
		delegate.setFilters(filters);
	}

	@Override
	public void addDataModelListener(DataModelListener listener) {
		delegate.addDataModelListener(listener);
	}

	@Override
	public void removeDataModelListener(DataModelListener listener) {
		delegate.removeDataModelListener(listener);
	}

	@Override
	public DataModelListener[] getDataModelListeners() {
		return delegate.getDataModelListeners();
	}

	public void setRowCount(int rowCount) {
		throw new UnsupportedOperationException("Unable to set the row count for a PagedDataModel");
	}

	public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		setPageSize(pageSize);
		setSortColumn(sortField);
		setSortAscending(true);
		setFilters(filters);
		return Collections.singletonList(getRowData());
	}
}
