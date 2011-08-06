package org.springframework.springfaces.page.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Collections;
import java.util.Map;

import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.model.NoRowAvailableException;

/**
 * Tests for {@link PagedDataModel}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class PagedDataModelTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private PagedDataModel<String> dataModel;

	private DataModelPageProvider<String> pageProvider = spy(new MockPageProvider());

	private PagedDataModelStateHolder stateHolder = new PagedDataModelState(10);

	private long totalNumberOfRows = 1000;

	@Captor
	private ArgumentCaptor<DataModelEvent> dataModelEvent;

	@Before
	public void setup() {
		dataModel = new PagedDataModel<String>(pageProvider, stateHolder);
	}

	@Test
	public void shouldNeedPageProvider() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("PageProvider must not be null");
		new PagedDataModel<String>(null, stateHolder);
	}

	@Test
	public void shouldNeedStateHolder() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("StateHolder must not be null");
		new PagedDataModel<String>(pageProvider, null);
	}

	@Test
	public void shouldHaveNoRowAvailableWhenNotOnARow() throws Exception {
		dataModel.setRowIndex(-1);
		assertThat(dataModel.isRowAvailable(), is(false));
		verifyZeroInteractions(pageProvider);
	}

	@Test
	public void shouldHaveRowAvailableWhenOnARow() throws Exception {
		dataModel.setRowIndex(0);
		assertThat(dataModel.isRowAvailable(), is(true));
	}

	@Test
	public void shouldGetRowCountFromProvider() throws Exception {
		assertThat(dataModel.getRowCount(), is(1000));
	}

	@Test
	public void shouldGetRowCountFromProviderRowIsSelected() throws Exception {
		assertThat(dataModel.getRowCount(), is(1000));
		totalNumberOfRows = 1001;
		dataModel.setRowIndex(0);
		assertThat(dataModel.getRowCount(), is(1001));
	}

	@Test
	public void shouldNotReturnARowCountIfThePageIsTooBig() throws Exception {
		totalNumberOfRows = Integer.MAX_VALUE + 1L;
		dataModel.setRowIndex(0);
		assertThat(dataModel.getRowCount(), is(-1));
	}

	@Test
	public void shouldGetRowDataFromProvider() throws Exception {
		dataModel.setRowIndex(0);
		assertThat(dataModel.getRowData(), is("Data 0"));
	}

	@Test
	public void shouldThrowIfNoData() throws Exception {
		thrown.expect(NoRowAvailableException.class);
		dataModel.getRowData();
	}

	@Test
	public void shouldDefaultToNoRowIndex() throws Exception {
		assertThat(dataModel.getRowIndex(), is(-1));
	}

	@Test
	public void shouldSupportChageOfRowIndex() throws Exception {
		dataModel.setRowIndex(-1);
		dataModel.setRowIndex(0);
		dataModel.setRowIndex(100);
		assertThat(dataModel.getRowIndex(), is(100));
	}

	@Test
	public void shouldNotSupportRowIndexLessThanMinusOne() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("RowIndex must not be less than -1");
		dataModel.setRowIndex(-2);
	}

	@Test
	public void shouldFireDataModelListners() throws Exception {
		DataModelListener listener = mock(DataModelListener.class);
		dataModel.addDataModelListener(listener);
		dataModel.setRowIndex(0);
		verify(listener).rowSelected(dataModelEvent.capture());
		assertThat(dataModelEvent.getValue().getRowIndex(), is(0));
		assertThat(dataModelEvent.getValue().getRowData(), is((Object) "Data 0"));
	}

	@Test
	public void shouldFireListenerOnChangeToNoRow() throws Exception {
		dataModel.setRowIndex(0);
		DataModelListener listener = mock(DataModelListener.class);
		dataModel.addDataModelListener(listener);
		dataModel.setRowIndex(-1);
		verify(listener).rowSelected(dataModelEvent.capture());
		assertThat(dataModelEvent.getValue().getRowIndex(), is(-1));
		assertThat(dataModelEvent.getValue().getRowData(), is(nullValue()));
	}

	@Test
	public void shouldNotDoubleFireListeners() throws Exception {
		DataModelListener listener = mock(DataModelListener.class);
		dataModel.addDataModelListener(listener);
		dataModel.setRowIndex(0);
		dataModel.setRowIndex(0);
		verify(listener).rowSelected(dataModelEvent.capture());
		verifyNoMoreInteractions(listener);
	}

	@Test
	public void shouldReturnPageAsWrappedData() throws Exception {
		dataModel.setRowIndex(0);
		assertThat(dataModel.getWrappedData(), is(MockPage.class));
	}

	@Test
	public void shouldNotAllowSetOfWrappedData() throws Exception {
		thrown.expect(UnsupportedOperationException.class);
		dataModel.setWrappedData(new Object());
	}

	@Test
	public void shouldGetAndSetPageSize() throws Exception {
		dataModel.setPageSize(100);
		assertThat(dataModel.getPageSize(), is(100));
	}

	@Test
	public void shouldResetRowIndexOnPageSizeChange() throws Exception {
		dataModel.setRowIndex(0);
		dataModel.setPageSize(99);
		assertThat(dataModel.getRowIndex(), is(-1));
	}

	@Test
	public void shouldGetAndSetSortAscending() throws Exception {
		dataModel.setSortAscending(true);
		assertThat(dataModel.getSortAscending(), is(true));
	}

	@Test
	public void shouldResetRowIndexOnSortAscendingChange() throws Exception {
		dataModel.setRowIndex(0);
		dataModel.setSortAscending(false);
		assertThat(dataModel.getRowIndex(), is(-1));
	}

	@Test
	public void shouldGetAndSetSortColumn() throws Exception {
		dataModel.setSortColumn("column");
		assertThat(dataModel.getSortColumn(), is("column"));
	}

	@Test
	public void shouldResetRowIndexOnSortColumnChange() throws Exception {
		dataModel.setRowIndex(0);
		dataModel.setSortColumn("column");
		assertThat(dataModel.getRowIndex(), is(-1));
	}

	@Test
	public void shouldGetAndSetFilters() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		dataModel.setFilters(filters);
		assertThat(dataModel.getFilters(), is(filters));
	}

	@Test
	public void shouldResetRowIndexOnFiltersChange() throws Exception {
		dataModel.setRowIndex(0);
		dataModel.setFilters(Collections.singletonMap("a", "b"));
		assertThat(dataModel.getRowIndex(), is(-1));
	}

	@Test
	public void shouldUseMinimalProviderCalls() throws Exception {
		for (int i = 0; i < 20; i++) {
			dataModel.setRowIndex(i);
			dataModel.getRowData();
		}
		verify(pageProvider, times(2)).getPage(stateHolder);
	}

	private class MockPageProvider implements DataModelPageProvider<String> {
		public PagedDataModelContent<String> getPage(PagedDataModelStateHolder stateHolder) {
			return new MockPage(stateHolder.getRowIndex(), stateHolder.getPageSize());
		}
	}

	private class MockPage implements PagedDataModelContent<String> {

		private int start;
		private int end;

		public MockPage(int start, int pageSize) {
			this.start = start;
			this.end = start + pageSize;
		}

		public long getTotalRowCount() {
			return totalNumberOfRows;
		}

		public boolean contains(int rowIndex) {
			return rowIndex < getTotalRowCount() && rowIndex >= start && rowIndex < end;
		}

		public boolean isRowAvailable(int rowIndex) {
			return contains(rowIndex);
		}

		public String getRowData(int rowIndex) throws NoRowAvailableException {
			if (!contains(rowIndex)) {
				throw new NoRowAvailableException();
			}
			return "Data " + String.valueOf(rowIndex);
		}
	}
}
