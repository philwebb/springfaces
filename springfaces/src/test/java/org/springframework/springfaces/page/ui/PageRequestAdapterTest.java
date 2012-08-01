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
package org.springframework.springfaces.page.ui;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.springframework.springfaces.page.model.PagedDataModelState;

/**
 * Tests for {@link PageRequestAdapter}.
 * 
 * @author Phillip Webb
 */
public class PageRequestAdapterTest {

	private PagedDataModelState state = new PagedDataModelState(3);
	private PageRequestAdapter adapter = new PageRequestAdapter(this.state);

	@Test
	public void shouldCalculatePageNumber() throws Exception {
		this.state.setRowIndex(0);
		assertThat(this.adapter.getPageNumber(), is(0));
		this.state.setRowIndex(1);
		assertThat(this.adapter.getPageNumber(), is(0));
		this.state.setRowIndex(2);
		assertThat(this.adapter.getPageNumber(), is(0));
		this.state.setRowIndex(3);
		assertThat(this.adapter.getPageNumber(), is(1));
		this.state.setRowIndex(4);
		assertThat(this.adapter.getPageNumber(), is(1));
		this.state.setRowIndex(5);
		assertThat(this.adapter.getPageNumber(), is(1));
	}

	@Test
	public void shouldGetPageSize() throws Exception {
		assertThat(this.adapter.getPageSize(), is(3));
	}

	@Test
	public void shouldCalculateOffset() throws Exception {
		this.state.setRowIndex(0);
		assertThat(this.adapter.getOffset(), is(0));
		this.state.setRowIndex(1);
		assertThat(this.adapter.getOffset(), is(0));
		this.state.setRowIndex(2);
		assertThat(this.adapter.getOffset(), is(0));
		this.state.setRowIndex(3);
		assertThat(this.adapter.getOffset(), is(3));
		this.state.setRowIndex(4);
		assertThat(this.adapter.getOffset(), is(3));
		this.state.setRowIndex(5);
		assertThat(this.adapter.getOffset(), is(3));
	}

	@Test
	public void shouldGetSortColumn() throws Exception {
		String sortColumn = "column";
		this.state.setSortColumn(sortColumn);
		assertThat(this.adapter.getSortColumn(), is(equalTo(sortColumn)));
	}

	@Test
	public void shouldGetSortAscending() throws Exception {
		boolean sortAscending = !this.state.isSortAscending();
		this.state.setSortAscending(sortAscending);
		assertThat(this.adapter.isSortAscending(), is(equalTo(sortAscending)));
	}

	@Test
	public void shouldGetFilters() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		this.state.setFilters(filters);
		assertThat(this.adapter.getFilters(), is(equalTo(filters)));
	}
}
