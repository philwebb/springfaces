package org.springframework.springfaces.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link DefaultDataModelRowSet}.
 * 
 * @author Phillip Webb
 */
public class DefaultDataModelRowSetTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DataModelRowSet<String> empty = DefaultDataModelRowSet.emptySet(-1);

	@Test
	public void shouldNotHaveRowCountWhenEmpty() throws Exception {
		assertThat(this.empty.getTotalRowCount(), is(-1L));
	}

	@Test
	public void shouldNotHaveRowDataWhenEmpty() throws Exception {
		this.thrown.expect(NoRowAvailableException.class);
		this.empty.getRowData(0);
	}

	@Test
	public void shouldContainRowWhenEmpty() throws Exception {
		assertThat(this.empty.contains(-1), is(true));
	}

	@Test
	public void shouldContainSpecificRowWhenEmpty() throws Exception {
		this.empty = DefaultDataModelRowSet.emptySet(2);
		assertThat(this.empty.contains(1), is(false));
		assertThat(this.empty.contains(2), is(true));
		assertThat(this.empty.contains(3), is(false));
	}

	@Test
	public void shouldSupportListContent() throws Exception {
		List<String> contents = Arrays.asList("a", "b", "c");
		DefaultDataModelRowSet<String> dataSet = new DefaultDataModelRowSet<String>(contents);
		assertThat(dataSet.getTotalRowCount(), is(-1L));
		assertThat(dataSet.contains(-1), is(false));
		assertThat(dataSet.contains(0), is(true));
		assertThat(dataSet.contains(1), is(true));
		assertThat(dataSet.contains(2), is(true));
		assertThat(dataSet.contains(3), is(false));
		assertThat(dataSet.isRowAvailable(-1), is(false));
		assertThat(dataSet.isRowAvailable(0), is(true));
		assertThat(dataSet.isRowAvailable(1), is(true));
		assertThat(dataSet.isRowAvailable(2), is(true));
		assertThat(dataSet.isRowAvailable(3), is(false));
		assertThat(dataSet.getRowData(0), is("a"));
		assertThat(dataSet.getRowData(1), is("b"));
		assertThat(dataSet.getRowData(2), is("c"));
	}

	@Test
	public void shouldSupportOffsetListContent() throws Exception {
		List<String> contents = Arrays.asList("a", "b", "c");
		DefaultDataModelRowSet<String> dataSet = new DefaultDataModelRowSet<String>(1, contents);
		assertThat(dataSet.getTotalRowCount(), is(-1L));
		assertThat(dataSet.contains(-1), is(false));
		assertThat(dataSet.contains(0), is(false));
		assertThat(dataSet.contains(1), is(true));
		assertThat(dataSet.contains(2), is(true));
		assertThat(dataSet.contains(3), is(true));
		assertThat(dataSet.contains(4), is(false));
		assertThat(dataSet.isRowAvailable(-1), is(false));
		assertThat(dataSet.isRowAvailable(0), is(false));
		assertThat(dataSet.isRowAvailable(1), is(true));
		assertThat(dataSet.isRowAvailable(2), is(true));
		assertThat(dataSet.isRowAvailable(3), is(true));
		assertThat(dataSet.isRowAvailable(4), is(false));
		assertThat(dataSet.getRowData(1), is("a"));
		assertThat(dataSet.getRowData(2), is("b"));
		assertThat(dataSet.getRowData(3), is("c"));
	}

	@Test
	public void shouldSupportSpecifiedTotalRowCount() throws Exception {
		List<String> contents = Arrays.asList("a", "b", "c");
		DefaultDataModelRowSet<String> dataSet = new DefaultDataModelRowSet<String>(0, contents, 3);
		assertThat(dataSet.getTotalRowCount(), is(3L));
	}

	@Test
	public void shouldSupportListContentWithSize() throws Exception {
		List<String> contents = Arrays.asList("a", "b", "c");
		DefaultDataModelRowSet<String> dataSet = new DefaultDataModelRowSet<String>(0, contents, 5, -1);
		assertThat(dataSet.getTotalRowCount(), is(-1L));
		assertThat(dataSet.contains(-1), is(false));
		assertThat(dataSet.contains(0), is(true));
		assertThat(dataSet.contains(1), is(true));
		assertThat(dataSet.contains(2), is(true));
		assertThat(dataSet.contains(3), is(true));
		assertThat(dataSet.contains(4), is(true));
		assertThat(dataSet.contains(5), is(false));
		assertThat(dataSet.isRowAvailable(-1), is(false));
		assertThat(dataSet.isRowAvailable(0), is(true));
		assertThat(dataSet.isRowAvailable(1), is(true));
		assertThat(dataSet.isRowAvailable(2), is(true));
		assertThat(dataSet.isRowAvailable(3), is(false));
		assertThat(dataSet.isRowAvailable(4), is(false));
		assertThat(dataSet.isRowAvailable(5), is(false));
		assertThat(dataSet.getRowData(0), is("a"));
		assertThat(dataSet.getRowData(1), is("b"));
		assertThat(dataSet.getRowData(2), is("c"));
	}

}
