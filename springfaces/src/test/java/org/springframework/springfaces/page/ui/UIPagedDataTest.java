/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.page.ui;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
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
		this.context = mock(FacesContext.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(this.context.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequestMap()).willReturn(this.requestMap);
		FacesContextSetter.setCurrentInstance(this.context);
	}

	@After
	public void releaseFacesContext() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldGetFamily() throws Exception {
		assertThat(this.uiPagedData.getFamily(), is(equalTo("spring.faces.PagedData")));
	}

	@Test
	public void shouldGetDefaultVarOfPagedData() throws Exception {
		assertThat(this.uiPagedData.getVar(), is(equalTo("pagedData")));
	}

	@Test
	public void shouldGetVarIfSpecified() throws Exception {
		this.uiPagedData.setVar("myVar");
		assertThat(this.uiPagedData.getVar(), is(equalTo("myVar")));
	}

	@Test
	public void shouldGetDefaultPageSizeOfTen() throws Exception {
		assertThat(this.uiPagedData.getPageSize(), is(equalTo(10)));
	}

	@Test
	public void shouldGetPageSizeIfSpecified() throws Exception {
		this.uiPagedData.setPageSize(12);
		assertThat(this.uiPagedData.getPageSize(), is(equalTo(12)));
	}

	@Test
	public void shouldSetupPageDataOnRestoreState() throws Exception {
		Object state = this.uiPagedData.saveState(this.context);
		this.uiPagedData.restoreState(this.context, state);
		assertThat(this.requestMap, hasKey("pagedData"));
	}

	@Test
	public void shouldSetupPageDataOnEncodeEnd() throws Exception {
		this.uiPagedData.encodeEnd(this.context);
		assertThat(this.requestMap, hasKey("pagedData"));
	}

	@Test
	public void shouldSupportCustomVaraibleName() throws Exception {
		this.uiPagedData.setVar("custom");
		this.uiPagedData.encodeEnd(this.context);
		assertThat(this.requestMap, hasKey("custom"));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldPassPageSizeToRows() throws Exception {
		this.uiPagedData.setPageSize(12);
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		assertThat(rows.getPageSize(), is(equalTo(12)));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldUseValueExpressionToGetData() throws Exception {
		List<String> valueResult = Collections.singletonList("a");
		this.uiPagedData.setValueExpression("value", mockExpression(valueResult));
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		rows.setRowIndex(0);
		assertThat(rows.getRowData(), is(equalTo((Object) "a")));
		assertThat(rows.getRowCount(), is(equalTo(-1)));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldUseRowExpressionToGetRowCount() throws Exception {
		this.uiPagedData.setValueExpression("value", mockExpression(Collections.singletonList("a")));
		this.uiPagedData.setValueExpression("rowCount", mockExpression(100));
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		assertThat(rows.getRowCount(), is(equalTo(100)));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldHavePageRequest() throws Exception {
		List<String> valueResult = Arrays.asList("a", "b", "c");
		this.uiPagedData.setPageSize(2);
		this.uiPagedData.setValueExpression("value", mockExpression(valueResult));
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		rows.setRowIndex(2);
		rows.isRowAvailable();
		assertThat(this.pageRequest.getOffset(), is(equalTo(2)));
		assertThat(this.pageRequest.getPageNumber(), is(equalTo(1)));
		assertThat(this.pageRequest.getPageSize(), is(equalTo(2)));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldResorePreviouslySetPageRequest() throws Exception {
		this.requestMap.put("pageRequest", "custom");
		this.uiPagedData.setValueExpression("value", mockExpression(Collections.singletonList("a")));
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		rows.setRowIndex(0);
		rows.getRowData();
		assertThat(this.requestMap.get("pageRequest"), is(equalTo((Object) "custom")));
		assertThat(this.pageRequest, is(not(nullValue())));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldCleanupPageRequestOnException() throws Exception {
		ValueExpression expression = mock(ValueExpression.class);
		given(expression.getValue(any(ELContext.class))).willThrow(new RuntimeException());
		this.uiPagedData.setValueExpression("value", expression);
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		rows.setRowIndex(0);
		try {
			this.thrown.expect(RuntimeException.class);
			rows.getRowData();
		} finally {
			assertThat(this.requestMap, not(hasKey("pageRequest")));
		}
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldSupportSpringData() throws Exception {
		Page page = mock(Page.class);
		given(page.getContent()).willReturn(Collections.singletonList("a"));
		given(page.getTotalElements()).willReturn(100L);
		this.uiPagedData.setValueExpression("value", mockExpression(page));
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		rows.setRowIndex(0);
		assertThat(rows.getRowData(), is(equalTo((Object) "a")));
		assertThat(rows.getRowCount(), is(equalTo(100)));
		assertThat(this.pageRequest, is(notNullValue()));
		assertThat(this.pageRequest, is(Pageable.class));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldSupportPrimeFaces() throws Exception {
		this.uiPagedData.setValueExpression("value", mockExpression(Collections.singletonList("a")));
		this.uiPagedData.setValueExpression("rowCount", mockExpression(100));
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		assertThat(rows, is(PrimeFacesPagedDataModel.class));
	}

	@Test
	public void shouldNeedPositivePageSize() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("PageSize must be a positive number");
		this.uiPagedData.setPageSize(0);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldDefaultToNullSortColumn() throws Exception {
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		assertThat(rows.getSortColumn(), is(nullValue()));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldPassSortColumnToDataRows() throws Exception {
		this.uiPagedData.setSortColumn("sort");
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		assertThat(rows.getSortColumn(), is(equalTo("sort")));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldDefaultToNullSortAscending() throws Exception {
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		assertThat(this.uiPagedData.getSortAscending(), is(nullValue()));
		assertThat(rows.isSortAscending(), is(true));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldPassSortAscendingToDataRows() throws Exception {
		this.uiPagedData.setSortAscending(false);
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		assertThat(rows.isSortAscending(), is(false));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldFailIfNullReturnedFromValueExpression() throws Exception {
		this.uiPagedData.setValueExpression("value", mockExpression(null));
		this.uiPagedData.encodeEnd(this.context);
		PagedDataRows rows = (PagedDataRows) this.requestMap.get("pagedData");
		rows.setRowIndex(0);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("UIPageData value returned null result");
		rows.getRowData();
	}

	private ValueExpression mockExpression(final Object result) {
		ValueExpression binding = mock(ValueExpression.class);
		given(binding.getValue(any(ELContext.class))).willAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				UIPagedDataTest.this.pageRequest = (PageRequest) UIPagedDataTest.this.requestMap.get("pageRequest");
				return result;
			}
		});
		return binding;
	}

}
