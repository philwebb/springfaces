package org.springframework.springfaces.page.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.model.LazyDataLoader;
import org.springframework.springfaces.model.LazyDataModel;
import org.springframework.springfaces.model.LazyDataModelState;
import org.springframework.springfaces.model.LazyDataModelTest;

/**
 * Tests for {@link PagedDataModel}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class PagedDataModelTest extends LazyDataModelTest {

	protected LazyDataModelState newLazyDataModelState() {
		return new PagedDataModelState(10);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected LazyDataModel newLazyDataModel(LazyDataLoader loader, LazyDataModelState state) {
		return new PagedDataModel(loader, (PagedDataModelState) state);
	}

	@SuppressWarnings("unchecked")
	protected PagedDataModel<String> getDataModel() {
		return (PagedDataModel<String>) super.getDataModel();
	}

	@Test
	public void shouldGetAndSetPageSize() throws Exception {
		getDataModel().setPageSize(100);
		assertThat(getDataModel().getPageSize(), is(100));
	}

	@Test
	public void shouldResetRowIndexOnPageSizeChange() throws Exception {
		getDataModel().setRowIndex(0);
		getDataModel().setPageSize(99);
		assertThat(getDataModel().getRowIndex(), is(-1));
	}

	@Test
	public void shouldGetAndSetSortAscending() throws Exception {
		getDataModel().setSortAscending(true);
		assertThat(getDataModel().isSortAscending(), is(true));
	}

	@Test
	public void shouldResetRowIndexOnSortAscendingChange() throws Exception {
		getDataModel().setRowIndex(0);
		getDataModel().setSortAscending(false);
		assertThat(getDataModel().getRowIndex(), is(-1));
	}

	@Test
	public void shouldGetAndSetSortColumn() throws Exception {
		getDataModel().setSortColumn("column");
		assertThat(getDataModel().getSortColumn(), is("column"));
	}

	@Test
	public void shouldResetRowIndexOnSortColumnChange() throws Exception {
		getDataModel().setRowIndex(0);
		getDataModel().setSortColumn("column");
		assertThat(getDataModel().getRowIndex(), is(-1));
	}

	@Test
	public void shouldGetAndSetFilters() throws Exception {
		Map<String, String> filters = Collections.singletonMap("a", "b");
		getDataModel().setFilters(filters);
		assertThat(getDataModel().getFilters(), is(filters));
	}

	@Test
	public void shouldResetRowIndexOnFiltersChange() throws Exception {
		getDataModel().setRowIndex(0);
		getDataModel().setFilters(Collections.singletonMap("a", "b"));
		assertThat(getDataModel().getRowIndex(), is(-1));
	}

	// FIXME test that resets also trigger fresh data load

}
