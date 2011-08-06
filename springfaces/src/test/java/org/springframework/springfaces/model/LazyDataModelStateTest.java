package org.springframework.springfaces.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link LazyDataModelState}.
 * 
 * @author Phillip Webb
 */
public class LazyDataModelStateTest {

	@Test
	public void shouldDefaultToNoRow() throws Exception {
		LazyDataModelState state = new LazyDataModelState();
		assertThat(state.getRowIndex(), is(-1));
	}

	@Test
	public void shouldSupportSet() throws Exception {
		LazyDataModelState state = new LazyDataModelState();
		state.setRowIndex(10);
		assertThat(state.getRowIndex(), is(10));
	}
}
