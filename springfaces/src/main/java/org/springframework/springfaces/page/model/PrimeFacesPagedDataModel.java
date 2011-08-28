package org.springframework.springfaces.page.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelListener;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.util.Assert;

/**
 * Adapts a {@link PagedDataModel} to a PrimeFaces {@link LazyDataModel}. NOTE: This implementation is unable to support
 * generic type arguments due to the fact that the PrimeFaces {@link LazyDataModel} does not pass generic types to the
 * inherited {@link DataModel}.
 * 
 * @author Phillip Webb
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PrimeFacesPagedDataModel extends LazyDataModel implements PagedDataRows {

	private static final long serialVersionUID = 1L;

	private PagedDataModel delegate;

	public PrimeFacesPagedDataModel(PagedDataModel delegate) {
		Assert.notNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
	}

	public boolean isRowAvailable() {
		return delegate.isRowAvailable();
	}

	public int getRowCount() {
		return delegate.getRowCount();
	}

	public Object getRowData() {
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

	public boolean isSortAscending() {
		return delegate.isSortAscending();
	}

	public void setSortAscending(boolean sortAscending) {
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

	public void setFilters(Map filters) {
		delegate.setFilters(filters);
	}

	public void addDataModelListener(DataModelListener listener) {
		delegate.addDataModelListener(listener);
	}

	public void removeDataModelListener(DataModelListener listener) {
		delegate.removeDataModelListener(listener);
	}

	public DataModelListener[] getDataModelListeners() {
		return delegate.getDataModelListeners();
	}

	public void setRowCount(int rowCount) {
		throw new UnsupportedOperationException("Unable to set the row count for a PagedDataModel");
	}

	// Primefaces 2.1
	public List load(int first, int pageSize, String sortField, boolean sortOrder, Map filters) {
		setPageSize(pageSize);
		if (sortField != null) {
			setSortColumn(sortField);
			setSortAscending(sortOrder);
		}
		setFilters(filters);
		return Collections.emptyList();
	}

	// Primefaces 3
	public List load(int first, int pageSize, String sortField, org.primefaces.model.SortOrder sortOrder, Map filters) {
		boolean sort = sortOrder == SortOrder.ASCENDING || sortOrder == SortOrder.UNSORTED;
		return load(first, pageSize, sortField, sort, filters);
	}
}
