package org.springframework.springfaces.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.List;

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

/**
 * Tests for {@link LazyDataModel}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class LazyDataModelTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private LazyDataModel<String, ? extends LazyDataModelState> dataModel;

	private MockLoader loader = spy(new MockLoader());

	private LazyDataModelState state = newLazyDataModelState();

	private long totalNumberOfRows = 1000;

	@Captor
	private ArgumentCaptor<DataModelEvent> dataModelEvent;

	@Before
	public void setup() {
		dataModel = newLazyDataModel(loader, state);
	}

	protected LazyDataModelState newLazyDataModelState() {
		return new LazyDataModelState();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected LazyDataModel<String, ? extends LazyDataModelState> newLazyDataModel(LazyDataLoader loader,
			LazyDataModelState state) {
		return new LazyDataModel(loader, state);
	}

	protected LazyDataModel<String, ? extends LazyDataModelState> getDataModel() {
		return dataModel;
	}

	@Test
	public void shouldNeedLoader() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Loader must not be null");
		newLazyDataModel(null, state);
	}

	@Test
	public void shouldNeedState() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("State must not be null");
		new LazyDataModel<String, LazyDataModelState>(loader, null);
	}

	@Test
	public void shouldHaveNoRowAvailableWhenNotOnARow() throws Exception {
		dataModel.setRowIndex(-1);
		assertThat(dataModel.isRowAvailable(), is(false));
		verifyZeroInteractions(loader);
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
	public void shouldUpdateRowCountFromProviderWhenRowIsSelected() throws Exception {
		assertThat(dataModel.getRowCount(), is(1000));
		totalNumberOfRows = 1001;
		dataModel.setRowIndex(11);
		// At this point we have not triggered a load so the previous row count is used
		assertThat(dataModel.getRowCount(), is(1000));
		// trigger load
		dataModel.getRowData();
		// The updated row count is now used
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
		assertThat(dataModel.getWrappedData(), is(DataModelRowSet.class));
	}

	@Test
	public void shouldNotAllowSetOfWrappedData() throws Exception {
		thrown.expect(UnsupportedOperationException.class);
		dataModel.setWrappedData(new Object());
	}

	@Test
	public void shouldUseMinimalProviderCalls() throws Exception {
		for (int i = 0; i < 20; i++) {
			dataModel.setRowIndex(i);
			dataModel.getRowData();
		}
		verify(loader, times(2)).getRows(state);
	}

	@Test
	public void shouldCacheEmptyLoad() throws Exception {
		@SuppressWarnings("unchecked")
		LazyDataLoader<String, LazyDataModelState> emptyLoader = mock(LazyDataLoader.class);
		dataModel = new LazyDataModel<String, LazyDataModelState>(emptyLoader, state);
		dataModel.setRowIndex(0);
		dataModel.isRowAvailable();
		dataModel.isRowAvailable();
		verify(emptyLoader, times(1)).getRows(state);
	}

	@Test
	public void resetShouldSetRowIndexAndRequireAFreshDataFetch() throws Exception {
		dataModel.setRowIndex(0);
		dataModel.getRowData();
		reset(loader);
		dataModel.reset();
		assertThat(dataModel.getRowIndex(), is(-1));
		dataModel.setRowIndex(0);
		dataModel.getRowData();
		verify(loader).getRows(state);
	}

	@Test
	public void shouldCachRowCount() throws Exception {
		assertThat(dataModel.getRowCount(), is(1000));
		dataModel.setRowIndex(11);
		assertThat(dataModel.getRowCount(), is(1000));
		verify(loader, times(1)).getRows(state);
	}

	private class MockLoader implements LazyDataLoader<String, LazyDataModelState> {
		public DataModelRowSet<String> getRows(LazyDataModelState state) {
			List<String> contents = new ArrayList<String>();
			for (int i = 0; i < 10; i++) {
				contents.add("Data " + (state.getRowIndex() + i));
			}
			return new DefaultDataModelRowSet<String>(state.getRowIndex(), contents, totalNumberOfRows);
		}
	}
}
