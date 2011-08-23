package org.springframework.springfaces.page.ui;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.FacesContextSetter;
import org.springframework.springfaces.page.model.PagedDataRows;
import org.springframework.springfaces.page.model.PrimeFacesPagedDataModel;

/**
 * Tests for {@link UIPagedData}.
 * 
 * @author Phillip Webb
 */
public class UIPagedDataTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private UIPagedData uiPagedData = new UIPagedData();

	private FacesContext context;

	private Map<String, Object> requestMap = new HashMap<String, Object>();

	private PageRequest pageRequest;

	@Before
	public void setup() {
		context = mock(FacesContext.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(context.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequestMap()).willReturn(requestMap);
		FacesContextSetter.setCurrentInstance(context);
	}

	@After
	public void releaseFacesContext() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldGetFamily() throws Exception {
		assertThat(uiPagedData.getFamily(), is(equalTo("spring.faces.PagedData")));
	}

	@Test
	public void shouldGetDefaultVarOfPagedData() throws Exception {
		assertThat(uiPagedData.getVar(), is(equalTo("pagedData")));
	}

	@Test
	public void shouldGetVarIfSpecified() throws Exception {
		uiPagedData.setVar("myVar");
		assertThat(uiPagedData.getVar(), is(equalTo("myVar")));
	}

	@Test
	public void shouldGetDefaultPageSizeOfTen() throws Exception {
		assertThat(uiPagedData.getPageSize(), is(equalTo(10)));
	}

	@Test
	public void shouldGetPageSizeIfSpecified() throws Exception {
		uiPagedData.setPageSize(12);
		assertThat(uiPagedData.getPageSize(), is(equalTo(12)));
	}

	@Test
	public void shouldSetupPageDataOnRestoreState() throws Exception {
		Object state = uiPagedData.saveState(context);
		uiPagedData.restoreState(context, state);
		assertTrue(requestMap.containsKey("pagedData"));
	}

	@Test
	public void shouldSetupPageDataOnEncodeEnd() throws Exception {
		uiPagedData.encodeEnd(context);
		assertTrue(requestMap.containsKey("pagedData"));
	}

	@Test
	public void shouldSupportCustomVaraibleName() throws Exception {
		uiPagedData.setVar("custom");
		uiPagedData.encodeEnd(context);
		assertTrue(requestMap.containsKey("custom"));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldPassPageSizeToRows() throws Exception {
		uiPagedData.setPageSize(12);
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		assertThat(rows.getPageSize(), is(equalTo(12)));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldUseValueExpressionToGetData() throws Exception {
		List<String> valueResult = Collections.singletonList("a");
		uiPagedData.setValueExpression("value", mockExpression(valueResult));
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		rows.setRowIndex(0);
		assertThat(rows.getRowData(), is(equalTo((Object) "a")));
		assertThat(rows.getRowCount(), is(equalTo(-1)));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldUseRowExpressionToGetRowCount() throws Exception {
		uiPagedData.setValueExpression("value", mockExpression(Collections.singletonList("a")));
		uiPagedData.setValueExpression("rowCount", mockExpression(100));
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		assertThat(rows.getRowCount(), is(equalTo(100)));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldHavePageRequest() throws Exception {
		List<String> valueResult = Arrays.asList("a", "b", "c");
		uiPagedData.setPageSize(2);
		uiPagedData.setValueExpression("value", mockExpression(valueResult));
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		rows.setRowIndex(2);
		rows.isRowAvailable();
		assertThat(pageRequest.getOffset(), is(equalTo(2)));
		assertThat(pageRequest.getPageNumber(), is(equalTo(1)));
		assertThat(pageRequest.getPageSize(), is(equalTo(2)));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldResorePreviouslySetPageRequest() throws Exception {
		requestMap.put("pageRequest", "custom");
		uiPagedData.setValueExpression("value", mockExpression(Collections.singletonList("a")));
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		rows.setRowIndex(0);
		rows.getRowData();
		assertThat(requestMap.get("pageRequest"), is(equalTo((Object) "custom")));
		assertNotNull(pageRequest);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldCleanupPageRequestOnException() throws Exception {
		ValueExpression expression = mock(ValueExpression.class);
		given(expression.getValue(any(ELContext.class))).willThrow(new RuntimeException());
		uiPagedData.setValueExpression("value", expression);
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		rows.setRowIndex(0);
		try {
			thrown.expect(RuntimeException.class);
			rows.getRowData();
		} finally {
			assertFalse(requestMap.containsKey("pageRequest"));
		}
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldSupportSpringData() throws Exception {
		Page page = mock(Page.class);
		given(page.getContent()).willReturn(Collections.singletonList("a"));
		given(page.getTotalElements()).willReturn(100L);
		uiPagedData.setValueExpression("value", mockExpression(page));
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		rows.setRowIndex(0);
		assertThat(rows.getRowData(), is(equalTo((Object) "a")));
		assertThat(rows.getRowCount(), is(equalTo(100)));
		assertThat(pageRequest, is(notNullValue()));
		assertThat(pageRequest, is(Pageable.class));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldSupportPrimeFaces() throws Exception {
		uiPagedData.setValueExpression("value", mockExpression(Collections.singletonList("a")));
		uiPagedData.setValueExpression("rowCount", mockExpression(100));
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		assertThat(rows, is(PrimeFacesPagedDataModel.class));
	}

	@Test
	public void shouldNeedPositivePageSize() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("PageSize must be a positive number");
		uiPagedData.setPageSize(0);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldDefaultToNullSortColumn() throws Exception {
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		assertThat(rows.getSortColumn(), is(nullValue()));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldPassSortColumnToDataRows() throws Exception {
		uiPagedData.setSortColumn("sort");
		uiPagedData.encodeEnd(context);
		PagedDataRows rows = (PagedDataRows) requestMap.get("pagedData");
		assertThat(rows.getSortColumn(), is(equalTo("sort")));
	}

	private ValueExpression mockExpression(final Object result) {
		ValueExpression binding = mock(ValueExpression.class);
		given(binding.getValue(any(ELContext.class))).willAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				pageRequest = (PageRequest) requestMap.get("pageRequest");
				return result;
			}
		});
		return binding;
	}

}
