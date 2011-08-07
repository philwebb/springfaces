package org.springframework.springfaces.page.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
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
		request = new SpringDataPageRequest(delegate);
	}

	@Test
	public void shouldDelegateGetPageNumber() throws Exception {
		Integer pageNumber = 10;
		given(delegate.getPageNumber()).willReturn(pageNumber);
		assertThat(request.getPageNumber(), is(pageNumber));
	}

	@Test
	public void shouldDelegateGetPageSize() throws Exception {
		Integer pageSize = 10;
		given(delegate.getPageSize()).willReturn(pageSize);
		assertThat(request.getPageSize(), is(pageSize));
	}

	@Test
	public void shouldDelegateGetOffset() throws Exception {
		Integer offset = 10;
		given(delegate.getOffset()).willReturn(offset);
		assertThat(request.getOffset(), is(offset));
	}

	@Test
	public void shouldDelegateGetSortColumn() throws Exception {
		String sortColumn = "column";
		given(delegate.getSortColumn()).willReturn(sortColumn);
		assertThat(request.getSortColumn(), is(sortColumn));
	}

	@Test
	public void shouldDelegateIsSortAscending() throws Exception {
		boolean sortAscending = false;
		given(delegate.isSortAscending()).willReturn(sortAscending);
		assertThat(request.isSortAscending(), is(sortAscending));

	}

	@Test
	public void shouldDelegateGetFilters() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		given(delegate.getFilters()).willReturn(filters);
		assertThat(request.getFilters(), is(filters));

	}

	@Test
	public void shouldBuildSpringDataSortFromSortColumnAndSortAscending() throws Exception {
		given(delegate.getSortColumn()).willReturn("column");
		given(delegate.isSortAscending()).willReturn(true);
		Sort sort = request.getSort();
		Iterator<Order> orderIterator = sort.iterator();
		Order order = orderIterator.next();
		assertFalse("Sort should only conain a single item", orderIterator.hasNext());
		assertThat(order.getDirection(), is(Sort.Direction.ASC));
		assertThat(order.getProperty(), is("column"));
	}

	@Test
	public void shouldReturnNullSortForEmptySortColumn() throws Exception {
		given(delegate.getSortColumn()).willReturn("");
		assertThat(request.getSort(), is(nullValue()));
	}

	@Test
	public void shouldReturnNullSortForNullSortColumn() throws Exception {
		assertThat(request.getSort(), is(nullValue()));
	}
}
