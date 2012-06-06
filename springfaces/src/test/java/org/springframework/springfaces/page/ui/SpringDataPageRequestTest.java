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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * Tests for {@link SpringDataPageRequest}.
 * 
 * @author Phillip Webb
 */
public class SpringDataPageRequestTest {

	@Mock
	private PageRequest delegate;

	private SpringDataPageRequest request;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.request = new SpringDataPageRequest(this.delegate);
	}

	@Test
	public void shouldDelegateGetPageNumber() throws Exception {
		Integer pageNumber = 10;
		given(this.delegate.getPageNumber()).willReturn(pageNumber);
		assertThat(this.request.getPageNumber(), is(pageNumber));
	}

	@Test
	public void shouldDelegateGetPageSize() throws Exception {
		Integer pageSize = 10;
		given(this.delegate.getPageSize()).willReturn(pageSize);
		assertThat(this.request.getPageSize(), is(pageSize));
	}

	@Test
	public void shouldDelegateGetOffset() throws Exception {
		Integer offset = 10;
		given(this.delegate.getOffset()).willReturn(offset);
		assertThat(this.request.getOffset(), is(offset));
	}

	@Test
	public void shouldDelegateGetSortColumn() throws Exception {
		String sortColumn = "column";
		given(this.delegate.getSortColumn()).willReturn(sortColumn);
		assertThat(this.request.getSortColumn(), is(sortColumn));
	}

	@Test
	public void shouldDelegateIsSortAscending() throws Exception {
		boolean sortAscending = false;
		given(this.delegate.isSortAscending()).willReturn(sortAscending);
		assertThat(this.request.isSortAscending(), is(sortAscending));

	}

	@Test
	public void shouldDelegateGetFilters() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		given(this.delegate.getFilters()).willReturn(filters);
		assertThat(this.request.getFilters(), is(filters));

	}

	@Test
	public void shouldBuildSpringDataSortFromSortColumnAndSortAscending() throws Exception {
		given(this.delegate.getSortColumn()).willReturn("column");
		given(this.delegate.isSortAscending()).willReturn(true);
		Sort sort = this.request.getSort();
		Iterator<Order> orderIterator = sort.iterator();
		Order order = orderIterator.next();
		assertThat("Sort should only conain a single item", orderIterator.hasNext(), is(false));
		assertThat(order.getDirection(), is(Sort.Direction.ASC));
		assertThat(order.getProperty(), is("column"));
	}

	@Test
	public void shouldReturnNullSortForEmptySortColumn() throws Exception {
		given(this.delegate.getSortColumn()).willReturn("");
		assertThat(this.request.getSort(), is(nullValue()));
	}

	@Test
	public void shouldReturnNullSortForNullSortColumn() throws Exception {
		assertThat(this.request.getSort(), is(nullValue()));
	}
}
