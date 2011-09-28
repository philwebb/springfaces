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
	private PageRequestAdapter adapter = new PageRequestAdapter(state);

	@Test
	public void shouldCalculatePageNumber() throws Exception {
		state.setRowIndex(0);
		assertThat(adapter.getPageNumber(), is(0));
		state.setRowIndex(1);
		assertThat(adapter.getPageNumber(), is(0));
		state.setRowIndex(2);
		assertThat(adapter.getPageNumber(), is(0));
		state.setRowIndex(3);
		assertThat(adapter.getPageNumber(), is(1));
		state.setRowIndex(4);
		assertThat(adapter.getPageNumber(), is(1));
		state.setRowIndex(5);
		assertThat(adapter.getPageNumber(), is(1));
	}

	@Test
	public void shouldGetPageSize() throws Exception {
		assertThat(adapter.getPageSize(), is(3));
	}

	@Test
	public void shouldCalculateOffset() throws Exception {
		state.setRowIndex(0);
		assertThat(adapter.getOffset(), is(0));
		state.setRowIndex(1);
		assertThat(adapter.getOffset(), is(0));
		state.setRowIndex(2);
		assertThat(adapter.getOffset(), is(0));
		state.setRowIndex(3);
		assertThat(adapter.getOffset(), is(3));
		state.setRowIndex(4);
		assertThat(adapter.getOffset(), is(3));
		state.setRowIndex(5);
		assertThat(adapter.getOffset(), is(3));
	}

	@Test
	public void shouldGetSortColumn() throws Exception {
		String sortColumn = "column";
		state.setSortColumn(sortColumn);
		assertThat(adapter.getSortColumn(), is(equalTo(sortColumn)));
	}

	@Test
	public void shouldGetSortAscending() throws Exception {
		boolean sortAscending = !state.isSortAscending();
		state.setSortAscending(sortAscending);
		assertThat(adapter.isSortAscending(), is(equalTo(sortAscending)));
	}

	@Test
	public void shouldGetFilters() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		state.setFilters(filters);
		assertThat(adapter.getFilters(), is(equalTo(filters)));
	}
}
