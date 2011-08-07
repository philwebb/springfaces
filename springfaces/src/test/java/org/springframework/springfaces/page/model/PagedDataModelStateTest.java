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
 * 
 * @author Phillip Webb
 */
public class PagedDataModelStateTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private PagedDataModelState state = new PagedDataModelState(10);

	@Test
	public void shouldHaveSensibleDefaults() throws Exception {
		assertThat(state.getPageSize(), is(10));
		assertThat(state.getRowIndex(), is(-1));
		assertThat(state.getSortColumn(), is(nullValue()));
		assertThat(state.isSortAscending(), is(true));
		assertThat(state.getFilters(), is(Collections.<String, String> emptyMap()));
	}

	@Test
	public void shouldNeedAPositivePageSizeOnCreate() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("PageSize must be a positive number");
		new PagedDataModelState(-2);
	}

	@Test
	public void shouldNeedAPositivePageSize() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("PageSize must be a positive number");
		state.setPageSize(-2);
	}

	@Test
	public void shouldSerialize() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(state);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		Object object = ois.readObject();
		assertThat(object, is(instanceOf(PagedDataModelState.class)));
	}

	@Test
	public void shouldGetSetPageSize() throws Exception {
		int pageSize = 100;
		state.setPageSize(pageSize);
		assertThat(state.getPageSize(), is(equalTo(pageSize)));
	}

	@Test
	public void shouldGetSetRowIndex() throws Exception {
		int rowIndex = 100;
		state.setRowIndex(rowIndex);
		assertThat(state.getRowIndex(), is(equalTo(rowIndex)));
	}

	@Test
	public void shouldGetSetSortColumn() throws Exception {
		String sortColumn = "column";
		state.setSortColumn(sortColumn);
		assertThat(state.getSortColumn(), is(equalTo(sortColumn)));
	}

	@Test
	public void shouldGetSetSortAscending() throws Exception {
		boolean sortAscending = !state.isSortAscending();
		state.setSortAscending(sortAscending);
		assertThat(state.isSortAscending(), is(equalTo(sortAscending)));
	}

	@Test
	public void shouldGetSetFilter() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		state.setFilters(filters);
		assertThat(state.getFilters(), is(equalTo(filters)));
	}

}
