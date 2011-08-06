package org.springframework.springfaces.page.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.springfaces.page.model.PagedDataModelState;

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

	// FIXME

}
