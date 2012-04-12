/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	// FIXME check status of http://code.google.com/p/primefaces/issues/detail?id=2642 when PF 3 final is released

	private static final long serialVersionUID = 1L;

	private PagedDataModel delegate;

	public PrimeFacesPagedDataModel(PagedDataModel delegate) {
		Assert.notNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
	}

	@Override
	public boolean isRowAvailable() {
		return this.delegate.isRowAvailable();
	}

	@Override
	public int getRowCount() {
		return this.delegate.getRowCount();
	}

	@Override
	public Object getRowData() {
		return this.delegate.getRowData();
	}

	@Override
	public int getRowIndex() {
		return this.delegate.getRowIndex();
	}

	@Override
	public void setRowIndex(int rowIndex) {
		this.delegate.setRowIndex(rowIndex);
	}

	@Override
	public Object getWrappedData() {
		return this.delegate.getWrappedData();
	}

	@Override
	public void setWrappedData(Object o) {
		// Ignore the wrapped data provided by primefaces
	}

	@Override
	public int getPageSize() {
		return this.delegate.getPageSize();
	}

	@Override
	public void setPageSize(int pageSize) {
		this.delegate.setPageSize(pageSize);
	}

	public boolean isSortAscending() {
		return this.delegate.isSortAscending();
	}

	public void setSortAscending(boolean sortAscending) {
		this.delegate.setSortAscending(sortAscending);
	}

	public String getSortColumn() {
		return this.delegate.getSortColumn();
	}

	public void setSortColumn(String sortColumn) {
		this.delegate.setSortColumn(sortColumn);
	}

	public void toggleSort(String sortColumn) {
		this.delegate.toggleSort(sortColumn);
	}

	public Map<String, String> getFilters() {
		return this.delegate.getFilters();
	}

	public void setFilters(Map filters) {
		this.delegate.setFilters(filters);
	}

	@Override
	public void addDataModelListener(DataModelListener listener) {
		this.delegate.addDataModelListener(listener);
	}

	@Override
	public void removeDataModelListener(DataModelListener listener) {
		this.delegate.removeDataModelListener(listener);
	}

	@Override
	public DataModelListener[] getDataModelListeners() {
		return this.delegate.getDataModelListeners();
	}

	@Override
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
		this.delegate.clearCachedRowCount(first);
		return Collections.emptyList();
	}

	// Primefaces 3
	@Override
	public List load(int first, int pageSize, String sortField, org.primefaces.model.SortOrder sortOrder, Map filters) {
		boolean sort = sortOrder == SortOrder.ASCENDING || sortOrder == SortOrder.UNSORTED;
		return load(first, pageSize, sortField, sort, filters);
	}
}
