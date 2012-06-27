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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test for {@link PagedDataModelState}.
 * @author Phillip Webb
 */
public class PagedDataModelStateTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private PagedDataModelState state = new PagedDataModelState(10);

	@Test
	public void shouldHaveSensibleDefaults() throws Exception {
		assertThat(this.state.getPageSize(), is(10));
		assertThat(this.state.getRowIndex(), is(-1));
		assertThat(this.state.getSortColumn(), is(nullValue()));
		assertThat(this.state.isSortAscending(), is(true));
		assertThat(this.state.getFilters(), is(Collections.<String, String> emptyMap()));
	}

	@Test
	public void shouldNeedAPositivePageSizeOnCreate() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("PageSize must be a positive number");
		new PagedDataModelState(-2);
	}

	@Test
	public void shouldNeedAPositivePageSize() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("PageSize must be a positive number");
		this.state.setPageSize(-2);
	}

	@Test
	public void shouldSerialize() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this.state);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		Object object = ois.readObject();
		assertThat(object, is(instanceOf(PagedDataModelState.class)));
	}

	@Test
	public void shouldGetSetPageSize() throws Exception {
		int pageSize = 100;
		this.state.setPageSize(pageSize);
		assertThat(this.state.getPageSize(), is(equalTo(pageSize)));
	}

	@Test
	public void shouldGetSetRowIndex() throws Exception {
		int rowIndex = 100;
		this.state.setRowIndex(rowIndex);
		assertThat(this.state.getRowIndex(), is(equalTo(rowIndex)));
	}

	@Test
	public void shouldGetSetSortColumn() throws Exception {
		String sortColumn = "column";
		this.state.setSortColumn(sortColumn);
		assertThat(this.state.getSortColumn(), is(equalTo(sortColumn)));
	}

	@Test
	public void shouldGetSetSortAscending() throws Exception {
		boolean sortAscending = !this.state.isSortAscending();
		this.state.setSortAscending(sortAscending);
		assertThat(this.state.isSortAscending(), is(equalTo(sortAscending)));
	}

	@Test
	public void shouldGetSetFilter() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		this.state.setFilters(filters);
		assertThat(this.state.getFilters(), is(equalTo(filters)));
	}

}
