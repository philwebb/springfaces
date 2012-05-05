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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModelListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.model.SortOrder;

/**
 * Tests for {@link PrimeFacesPagedDataModel}
 * 
 * @author Phillip Webb
 */
public class PrimeFacesPagedDataModelTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PagedDataModel<Object> delegate;

	private PrimeFacesPagedDataModel<Object> dataModel;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.dataModel = new PrimeFacesPagedDataModel<Object>(this.delegate);
	}

	@Test
	public void shouldNeedDelegate() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Delegate must not be null");
		new PrimeFacesPagedDataModel<Object>(null);
	}

	@Test
	public void shouldDelegateIsRowAvailable() throws Exception {
		this.dataModel.isRowAvailable();
		verify(this.delegate).isRowAvailable();
	}

	@Test
	public void shouldDelegateGetRowCount() throws Exception {
		this.dataModel.getRowCount();
		verify(this.delegate).getRowCount();
	}

	@Test
	public void shouldDelegateGetRowData() throws Exception {
		this.dataModel.getRowData();
		verify(this.delegate).getRowData();
	}

	@Test
	public void shouldDelegateGetRowIndex() throws Exception {
		this.dataModel.getRowIndex();
		verify(this.delegate).getRowIndex();
	}

	@Test
	public void shouldDelegateSetRowIndex() throws Exception {
		int index = 1;
		this.dataModel.setRowIndex(index);
		verify(this.delegate).setRowIndex(index);
	}

	@Test
	public void shouldDelegateGetWrappedData() throws Exception {
		this.dataModel.getWrappedData();
		verify(this.delegate).getWrappedData();
	}

	@Test
	public void shouldIgnoreSetWrappedData() throws Exception {
		Object data = new Object();
		this.dataModel.setWrappedData(data);
		verify(this.delegate, never()).setWrappedData(data);
	}

	@Test
	public void shouldDelegateGetPageSize() throws Exception {
		this.dataModel.getPageSize();
		verify(this.delegate).getPageSize();
	}

	@Test
	public void shouldDelegateSetPageSize() throws Exception {
		int pageSize = 10;
		this.dataModel.setPageSize(pageSize);
		verify(this.delegate).setPageSize(10);
	}

	@Test
	public void shouldDelegateGetSortAscending() throws Exception {
		this.dataModel.isSortAscending();
		verify(this.delegate).isSortAscending();
	}

	@Test
	public void shouldDelegateSetSortAscending() throws Exception {
		boolean sortAscending = true;
		this.dataModel.setSortAscending(sortAscending);
		verify(this.delegate).setSortAscending(sortAscending);
	}

	@Test
	public void shouldDelegateGetSortColumn() throws Exception {
		this.dataModel.getSortColumn();
		verify(this.delegate).getSortColumn();
	}

	@Test
	public void shouldDelegateSetSortColumn() throws Exception {
		String sortColumn = "column";
		this.dataModel.setSortColumn(sortColumn);
		verify(this.delegate).setSortColumn(sortColumn);
	}

	@Test
	public void shouldDelegateToggleSort() throws Exception {
		String sortColumn = "column";
		this.dataModel.toggleSort(sortColumn);
		verify(this.delegate).toggleSort(sortColumn);
	}

	@Test
	public void shouldDelegateGetFilters() throws Exception {
		this.dataModel.getFilters();
		verify(this.delegate).getFilters();
	}

	@Test
	public void shouldDelegateSetFilters() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		this.dataModel.setFilters(filters);
		verify(this.delegate).setFilters(filters);
	}

	@Test
	public void shouldDelegateAddDataModelListener() throws Exception {
		DataModelListener listner = mock(DataModelListener.class);
		this.dataModel.addDataModelListener(listner);
		verify(this.delegate).addDataModelListener(listner);
	}

	@Test
	public void shouldDelegateRemoveDataModelListener() throws Exception {
		DataModelListener listner = mock(DataModelListener.class);
		this.dataModel.removeDataModelListener(listner);
		verify(this.delegate).removeDataModelListener(listner);
	}

	@Test
	public void shouldDelegateGetDataModelListeners() throws Exception {
		this.dataModel.getDataModelListeners();
		verify(this.delegate).getDataModelListeners();
	}

	@Test
	public void shouldNotSupportSetRowCount() throws Exception {
		this.thrown.expect(UnsupportedOperationException.class);
		this.thrown.expectMessage("Unable to set the row count for a PagedDataModel");
		this.dataModel.setRowCount(1);
	}

	@Test
	public void shouldSupportLazyPrimefacesLoad() throws Exception {
		int first = 1;
		int pageSize = 100;
		String sortField = "sort";
		boolean sortOrder = true;
		Map<String, String> filters = Collections.singletonMap("a", "b");
		Object rowData = new Object();
		given(this.delegate.getRowData()).willReturn(rowData);
		List loaded = this.dataModel.load(first, pageSize, sortField, sortOrder, filters);
		assertThat(loaded, is((List) Collections.emptyList()));
		verify(this.delegate).setPageSize(pageSize);
		verify(this.delegate).setSortColumn(sortField);
		verify(this.delegate).setSortAscending(sortOrder);
		verify(this.delegate).setFilters(filters);
		verify(this.delegate).clearCachedRowCount(first);
	}

	@Test
	public void shouldSupportLazyPrimefacesLoadV3() throws Exception {
		int first = 1;
		int pageSize = 100;
		String sortField = "sort";
		SortOrder sortOrder = SortOrder.ASCENDING;
		Map<String, String> filters = Collections.singletonMap("a", "b");
		Object rowData = new Object();
		given(this.delegate.getRowData()).willReturn(rowData);
		List loaded = this.dataModel.load(first, pageSize, sortField, sortOrder, filters);
		assertThat(loaded, is((List) Collections.emptyList()));
		verify(this.delegate).setPageSize(pageSize);
		verify(this.delegate).setSortColumn(sortField);
		verify(this.delegate).setSortAscending(true);
		verify(this.delegate).setFilters(filters);
		verify(this.delegate).clearCachedRowCount(first);
	}
}
