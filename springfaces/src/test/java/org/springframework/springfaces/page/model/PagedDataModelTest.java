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

import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.model.LazyDataLoader;
import org.springframework.springfaces.model.LazyDataModel;
import org.springframework.springfaces.model.LazyDataModelState;
import org.springframework.springfaces.model.LazyDataModelTest;

/**
 * Tests for {@link PagedDataModel}.
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class PagedDataModelTest extends LazyDataModelTest {

	private boolean reset;

	@Override
	protected LazyDataModelState newLazyDataModelState() {
		return new PagedDataModelState(10);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected LazyDataModel newLazyDataModel(LazyDataLoader loader, LazyDataModelState state) {
		return new PagedDataModel(loader, (PagedDataModelState) state) {
			@Override
			protected void reset() {
				super.reset();
				PagedDataModelTest.this.reset = true;
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	protected PagedDataModel<String> getDataModel() {
		return (PagedDataModel<String>) super.getDataModel();
	}

	@Test
	public void shouldGetAndSetPageSize() throws Exception {
		getDataModel().setPageSize(100);
		assertThat(getDataModel().getPageSize(), is(100));
		assertThat(this.reset, is(true));
	}

	@Test
	public void shouldResetRowIndexOnPageSizeChange() throws Exception {
		getDataModel().setRowIndex(0);
		getDataModel().setPageSize(99);
		assertThat(getDataModel().getRowIndex(), is(-1));
		assertThat(this.reset, is(true));
	}

	@Test
	public void shouldGetAndSetSortAscending() throws Exception {
		getDataModel().setSortAscending(true);
		assertThat(getDataModel().isSortAscending(), is(true));
	}

	@Test
	public void shouldResetRowIndexOnSortAscendingChange() throws Exception {
		getDataModel().setRowIndex(0);
		getDataModel().setSortAscending(false);
		assertThat(getDataModel().getRowIndex(), is(-1));
		assertThat(this.reset, is(true));
	}

	@Test
	public void shouldGetAndSetSortColumn() throws Exception {
		getDataModel().setSortColumn("column");
		assertThat(getDataModel().getSortColumn(), is("column"));
		assertThat(this.reset, is(true));
	}

	@Test
	public void shouldToggleExistingSortColumn() throws Exception {
		getDataModel().setSortColumn("column");
		getDataModel().setSortAscending(false);
		getDataModel().toggleSort("column");
		assertThat(getDataModel().getSortColumn(), is("column"));
		assertThat(getDataModel().isSortAscending(), is(true));
		getDataModel().toggleSort("column");
		assertThat(getDataModel().getSortColumn(), is("column"));
		assertThat(getDataModel().isSortAscending(), is(false));
	}

	@Test
	public void shouldToggleNewSortColumnSettingAscendingFromFalse() throws Exception {
		getDataModel().setSortColumn("column1");
		getDataModel().setSortAscending(false);
		getDataModel().toggleSort("column2");
		assertThat(getDataModel().getSortColumn(), is("column2"));
		assertThat(getDataModel().isSortAscending(), is(true));
	}

	@Test
	public void shouldToggleNewSortColumnSettingAscendingFromTrue() throws Exception {
		getDataModel().setSortColumn("column1");
		getDataModel().setSortAscending(true);
		getDataModel().toggleSort("column2");
		assertThat(getDataModel().getSortColumn(), is("column2"));
		assertThat(getDataModel().isSortAscending(), is(true));
	}

	@Test
	public void shouldResetRowIndexOnSortColumnChange() throws Exception {
		getDataModel().setRowIndex(0);
		getDataModel().setSortColumn("column");
		assertThat(getDataModel().getRowIndex(), is(-1));
		assertThat(this.reset, is(true));
	}

	@Test
	public void shouldGetAndSetFilters() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		getDataModel().setFilters(filters);
		assertThat(getDataModel().getFilters(), is(filters));
		assertThat(this.reset, is(true));
	}

	@Test
	public void shouldResetRowIndexOnFiltersChange() throws Exception {
		getDataModel().setRowIndex(0);
		getDataModel().setFilters(Collections.singletonMap("a", "b"));
		assertThat(getDataModel().getRowIndex(), is(-1));
		assertThat(this.reset, is(true));
	}
}