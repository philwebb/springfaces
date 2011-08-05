package org.springframework.springfaces.page.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link EmptyPagedDataModelPage}.
 * 
 * @author Phillip Webb
 */
public class EmptyPagedDataModelPageTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private PagedDataModelPage<String> empty = new EmptyPagedDataModelPage<String>(-1);

	@Test
	public void shouldNotHaveRowCount() throws Exception {
		assertThat(empty.getTotalRowCount(), is(-1L));
	}

	@Test
	public void shouldNotHaveRowData() throws Exception {
		thrown.expect(NoRowAvailableException.class);
		empty.getRowData(0);
	}

	@Test
	public void shouldNotContainRows() throws Exception {
		assertThat(empty.containsRowIndex(0), is(false));
	}
}
