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
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link PrimeFacesPagedDataModel}
 * 
 * @author Phillip Webb
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PrimeFacesPagedDataModelTest {

	@Mock
	private PagedDataModel delegate;

	private PrimeFacesPagedDataModel dataModel;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		dataModel = new PrimeFacesPagedDataModel(delegate);
	}

	@Test
	public void shouldNeedDelegate() throws Exception {

	}

	@Test
	public void shouldDelegateIsRowAvailable() throws Exception {
		dataModel.isRowAvailable();
		verify(delegate).isRowAvailable();
	}

	@Test
	public void shouldDelegateGetRowCount() throws Exception {
		dataModel.getRowCount();
		verify(delegate).getRowCount();
	}

	@Test
	public void shouldDelegateGetRowData() throws Exception {
		dataModel.getRowData();
		verify(delegate).getRowData();
	}

	@Test
	public void shouldDelegateGetRowIndex() throws Exception {
		dataModel.getRowIndex();
		verify(delegate).getRowIndex();
	}

	@Test
	public void shouldDelegateSetRowIndex() throws Exception {
		int index = 1;
		dataModel.setRowIndex(index);
		verify(delegate).setRowIndex(index);
	}

	@Test
	public void shouldDelegateGetWrappedData() throws Exception {
		dataModel.getWrappedData();
		verify(delegate).getWrappedData();
	}

	@Test
	public void shouldIgnoreSetWrappedData() throws Exception {
		Object data = new Object();
		dataModel.setWrappedData(data);
		verify(delegate, never()).setWrappedData(data);
	}

	@Test
	public void shouldDelegateGetPageSize() throws Exception {
		dataModel.getPageSize();
		verify(delegate).getPageSize();
	}

	@Test
	public void shouldDelegateSetPageSize() throws Exception {
		int pageSize = 10;
		dataModel.setPageSize(pageSize);
		verify(delegate).setPageSize(10);
	}

	@Test
	public void shouldDelegateGetSortAscending() throws Exception {
		dataModel.isSortAscending();
		verify(delegate).isSortAscending();
	}

	@Test
	public void shouldDelegateSetSortAscending() throws Exception {
		boolean sortAscending = true;
		dataModel.setSortAscending(sortAscending);
		verify(delegate).setSortAscending(sortAscending);
	}

	@Test
	public void shouldDelegateGetSortColumn() throws Exception {
		dataModel.getSortColumn();
		verify(delegate).getSortColumn();
	}

	@Test
	public void shouldDelegateSetSortColumn() throws Exception {
		String sortColumn = "column";
		dataModel.setSortColumn(sortColumn);
		verify(delegate).setSortColumn(sortColumn);
	}

	@Test
	public void shouldDelegateGetFilters() throws Exception {
		dataModel.getFilters();
		verify(delegate).getFilters();
	}

	@Test
	public void shouldDelegateSetFilters() throws Exception {
		Map filters = Collections.singletonMap("a", "b");
		dataModel.setFilters(filters);
		verify(delegate).setFilters(filters);
	}

	@Test
	public void shouldDelegateAddDataModelListener() throws Exception {
		DataModelListener listner = mock(DataModelListener.class);
		dataModel.addDataModelListener(listner);
		verify(delegate).addDataModelListener(listner);
	}

	@Test
	public void shouldDelegateRemoveDataModelListener() throws Exception {
		DataModelListener listner = mock(DataModelListener.class);
		dataModel.removeDataModelListener(listner);
		verify(delegate).removeDataModelListener(listner);
	}

	@Test
	public void shouldDelegateGetDataModelListeners() throws Exception {
		dataModel.getDataModelListeners();
		verify(delegate).getDataModelListeners();
	}

	@Test
	public void shouldSupportLazyPrimefacesLoad() throws Exception {
		int first = 1;
		int pageSize = 100;
		String sortField = "sort";
		boolean sortOrder = true;
		Map filters = Collections.singletonMap("a", "b");
		Object rowData = new Object();
		given(delegate.getRowData()).willReturn(rowData);
		List loaded = dataModel.load(first, pageSize, sortField, sortOrder, filters);
		assertThat(loaded, is((List) Collections.singletonList(rowData)));
		verify(delegate).setPageSize(pageSize);
		verify(delegate).setSortColumn(sortField);
		verify(delegate).setSortAscending(sortOrder);
		verify(delegate).setFilters(filters);
	}
}
