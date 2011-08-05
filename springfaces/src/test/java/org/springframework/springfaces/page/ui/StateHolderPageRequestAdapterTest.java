package org.springframework.springfaces.page.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.springfaces.page.model.DefaultPagedDataModelStateHolder;
import org.springframework.springfaces.page.model.PagedDataModelStateHolder;

public class StateHolderPageRequestAdapterTest {

	private PagedDataModelStateHolder stateHolder = new DefaultPagedDataModelStateHolder(3);
	private StateHolderPageRequestAdapter adapter = new StateHolderPageRequestAdapter(stateHolder);

	@Test
	public void shouldCalculatePageNumber() throws Exception {
		stateHolder.setRowIndex(0);
		assertThat(adapter.getPageNumber(), is(0));
		stateHolder.setRowIndex(1);
		assertThat(adapter.getPageNumber(), is(0));
		stateHolder.setRowIndex(2);
		assertThat(adapter.getPageNumber(), is(0));
		stateHolder.setRowIndex(3);
		assertThat(adapter.getPageNumber(), is(1));
		stateHolder.setRowIndex(4);
		assertThat(adapter.getPageNumber(), is(1));
		stateHolder.setRowIndex(5);
		assertThat(adapter.getPageNumber(), is(1));
	}

}
