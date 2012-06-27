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
		this.dataModel = newLazyDataModel(this.loader, this.state);
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
		return this.dataModel;
	}

	@Test
	public void shouldNeedLoader() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Loader must not be null");
		newLazyDataModel(null, this.state);
	}

	@Test
	public void shouldNeedState() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("State must not be null");
		new LazyDataModel<String, LazyDataModelState>(this.loader, null);
	}

	@Test
	public void shouldHaveNoRowAvailableWhenNotOnARow() throws Exception {
		this.dataModel.setRowIndex(-1);
		assertThat(this.dataModel.isRowAvailable(), is(false));
		verifyZeroInteractions(this.loader);
	}

	@Test
	public void shouldHaveRowAvailableWhenOnARow() throws Exception {
		this.dataModel.setRowIndex(0);
		assertThat(this.dataModel.isRowAvailable(), is(true));
	}

	@Test
	public void shouldGetRowCountFromProvider() throws Exception {
		assertThat(this.dataModel.getRowCount(), is(1000));
	}

	@Test
	public void shouldUpdateRowCountFromProviderWhenRowIsSelected() throws Exception {
		assertThat(this.dataModel.getRowCount(), is(1000));
		this.totalNumberOfRows = 1001;
		this.dataModel.setRowIndex(11);
		// At this point we have not triggered a load so the previous row count is used
		assertThat(this.dataModel.getRowCount(), is(1000));
		// trigger load
		this.dataModel.getRowData();
		// The updated row count is now used
		assertThat(this.dataModel.getRowCount(), is(1001));
	}

	@Test
	public void shouldNotReturnARowCountIfThePageIsTooBig() throws Exception {
		this.totalNumberOfRows = Integer.MAX_VALUE + 1L;
		this.dataModel.setRowIndex(0);
		assertThat(this.dataModel.getRowCount(), is(-1));
	}

	@Test
	public void shouldGetRowDataFromProvider() throws Exception {
		this.dataModel.setRowIndex(0);
		assertThat(this.dataModel.getRowData(), is("Data 0"));
	}

	@Test
	public void shouldThrowIfNoData() throws Exception {
		this.thrown.expect(NoRowAvailableException.class);
		this.dataModel.getRowData();
	}

	@Test
	public void shouldDefaultToNoRowIndex() throws Exception {
		assertThat(this.dataModel.getRowIndex(), is(-1));
	}

	@Test
	public void shouldSupportChageOfRowIndex() throws Exception {
		this.dataModel.setRowIndex(-1);
		this.dataModel.setRowIndex(0);
		this.dataModel.setRowIndex(100);
		assertThat(this.dataModel.getRowIndex(), is(100));
	}

	@Test
	public void shouldNotSupportRowIndexLessThanMinusOne() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("RowIndex must not be less than -1");
		this.dataModel.setRowIndex(-2);
	}

	@Test
	public void shouldFireDataModelListners() throws Exception {
		DataModelListener listener = mock(DataModelListener.class);
		this.dataModel.addDataModelListener(listener);
		this.dataModel.setRowIndex(0);
		verify(listener).rowSelected(this.dataModelEvent.capture());
		assertThat(this.dataModelEvent.getValue().getRowIndex(), is(0));
		assertThat(this.dataModelEvent.getValue().getRowData(), is((Object) "Data 0"));
	}

	@Test
	public void shouldFireListenerOnChangeToNoRow() throws Exception {
		this.dataModel.setRowIndex(0);
		DataModelListener listener = mock(DataModelListener.class);
		this.dataModel.addDataModelListener(listener);
		this.dataModel.setRowIndex(-1);
		verify(listener).rowSelected(this.dataModelEvent.capture());
		assertThat(this.dataModelEvent.getValue().getRowIndex(), is(-1));
		assertThat(this.dataModelEvent.getValue().getRowData(), is(nullValue()));
	}

	@Test
	public void shouldNotDoubleFireListeners() throws Exception {
		DataModelListener listener = mock(DataModelListener.class);
		this.dataModel.addDataModelListener(listener);
		this.dataModel.setRowIndex(0);
		this.dataModel.setRowIndex(0);
		verify(listener).rowSelected(this.dataModelEvent.capture());
		verifyNoMoreInteractions(listener);
	}

	@Test
	public void shouldReturnPageAsWrappedData() throws Exception {
		this.dataModel.setRowIndex(0);
		assertThat(this.dataModel.getWrappedData(), is(DataModelRowSet.class));
	}

	@Test
	public void shouldNotAllowSetOfWrappedData() throws Exception {
		this.thrown.expect(UnsupportedOperationException.class);
		this.dataModel.setWrappedData(new Object());
	}

	@Test
	public void shouldUseMinimalProviderCalls() throws Exception {
		for (int i = 0; i < 20; i++) {
			this.dataModel.setRowIndex(i);
			this.dataModel.getRowData();
		}
		verify(this.loader, times(2)).getRows(this.state);
	}

	@Test
	public void shouldCacheEmptyLoad() throws Exception {
		@SuppressWarnings("unchecked")
		LazyDataLoader<String, LazyDataModelState> emptyLoader = mock(LazyDataLoader.class);
		this.dataModel = new LazyDataModel<String, LazyDataModelState>(emptyLoader, this.state);
		this.dataModel.setRowIndex(0);
		this.dataModel.isRowAvailable();
		this.dataModel.isRowAvailable();
		verify(emptyLoader, times(1)).getRows(this.state);
	}

	@Test
	public void resetShouldSetRowIndexAndRequireAFreshDataFetch() throws Exception {
		this.dataModel.setRowIndex(0);
		this.dataModel.getRowData();
		reset(this.loader);
		this.dataModel.reset();
		assertThat(this.dataModel.getRowIndex(), is(-1));
		this.dataModel.setRowIndex(0);
		this.dataModel.getRowData();
		verify(this.loader).getRows(this.state);
	}

	@Test
	public void shouldCacheRowCount() throws Exception {
		assertThat(this.dataModel.getRowCount(), is(1000));
		this.dataModel.setRowIndex(11);
		assertThat(this.dataModel.getRowCount(), is(1000));
		verify(this.loader, times(1)).getRows(this.state);
	}

	@Test
	public void shouldClearCachedRowCount() throws Exception {
		assertThat(this.dataModel.getRowCount(), is(1000));
		this.dataModel.reset();
		this.dataModel.clearCachedRowCount();
		assertThat(this.dataModel.getRowCount(), is(1000));
		this.dataModel.setRowIndex(11);
		assertThat(this.dataModel.getRowData(), is("Data 11"));
		verify(this.loader, times(3)).getRows(this.state);
	}

	@Test
	public void shouldClearCachedRowCountWithNextRow() throws Exception {
		assertThat(this.dataModel.getRowCount(), is(1000));
		this.dataModel.reset();
		this.dataModel.clearCachedRowCount(11);
		assertThat(this.dataModel.getRowCount(), is(1000));
		this.dataModel.setRowIndex(11);
		assertThat(this.dataModel.getRowData(), is("Data 11"));
		verify(this.loader, times(2)).getRows(this.state);
	}

	private class MockLoader implements LazyDataLoader<String, LazyDataModelState> {
		public DataModelRowSet<String> getRows(LazyDataModelState state) {
			List<String> contents = new ArrayList<String>();
			for (int i = 0; i < 10; i++) {
				contents.add("Data " + (state.getRowIndex() + i));
			}
			return new DefaultDataModelRowSet<String>(state.getRowIndex(), contents,
					LazyDataModelTest.this.totalNumberOfRows);
		}
	}
}
