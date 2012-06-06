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
package org.springframework.springfaces.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.faces.component.UIInput;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.sun.faces.facelets.component.UIRepeat;

/**
 * Tests for {@link FacesUtils}.
 * 
 * @author Phillip Webb
 */
public class FacesUtilsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private FacesContext context;

	private Map<String, Object> requestMap = new HashMap<String, Object>();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(this.context.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequestMap()).willAnswer(new Answer<Map<String, Object>>() {
			public Map<String, Object> answer(InvocationOnMock invocation) throws Throwable {
				return FacesUtilsTest.this.requestMap;
			}
		});
	}

	@Test
	public void shouldNeedFacesContextForFindLocale() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("FacesContext must not be null");
		FacesUtils.getLocale(null);
	}

	@Test
	public void shouldFindLocaleFromViewRoot() throws Exception {
		FacesContext facesContext = mock(FacesContext.class);
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(facesContext.getViewRoot()).willReturn(viewRoot);
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(viewRoot.getLocale()).willReturn(Locale.CANADA);
		given(externalContext.getRequestLocale()).willReturn(Locale.GERMAN);
		assertThat(FacesUtils.getLocale(facesContext), is(equalTo(Locale.CANADA)));
	}

	@Test
	public void shouldFindLocaleFromRequestWhenNoViewRoot() throws Exception {
		FacesContext facesContext = mock(FacesContext.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequestLocale()).willReturn(Locale.GERMAN);
		assertThat(FacesUtils.getLocale(facesContext), is(equalTo(Locale.GERMAN)));
	}

	@Test
	public void shouldNeedComponentForFindParentOfType() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Component must not be null");
		FacesUtils.findParentOfType(null, UIPanel.class);
	}

	@Test
	public void shouldNeedParentTyoeFirFindParentIfType() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("ParentType must not be null");
		FacesUtils.findParentOfType(new UIInput(), null);

	}

	@Test
	public void shouldFindParentOfType() throws Exception {
		UIPanel p1 = new UIPanel();
		UIPanel p2 = new UIPanel();
		UIInput input = new UIInput();
		p2.setParent(p1);
		input.setParent(p2);
		UIPanel actual = FacesUtils.findParentOfType(input, UIPanel.class);
		assertThat(actual, is(sameInstance(p2)));
	}

	@Test
	public void shouldReturnNullIfNotSuitableParent() throws Exception {
		UIPanel p1 = new UIPanel();
		UIInput input = new UIInput();
		input.setParent(p1);
		UIRepeat actual = FacesUtils.findParentOfType(input, UIRepeat.class);
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void shouldNeedContextForDoWithRequestScopeVariable() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Context must not be null");
		FacesUtils.doWithRequestScopeVariable(null, "variableName", "value", new MockCallable<String>(""));
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void shouldNeedCallableForDoWithRequestScopeVariable() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Callable must not be null");
		FacesUtils.doWithRequestScopeVariable(this.context, "variableName", "value", (Callable) null);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldDoWithNullRequestScopeVariable() throws Exception {
		this.requestMap = mock(Map.class);
		MockCallable<String> callable = new MockCallable<String>("result");
		String actualResult = FacesUtils.doWithRequestScopeVariable(this.context, null, "value", callable);
		assertThat(actualResult, is("result"));
		verify(this.requestMap, never()).put(anyString(), any());
	}

	@Test
	public void shouldDoWithRequestScopeVariable() throws Exception {
		Object initialRequestValue = "old";
		Object valueAtTimeOfCall = "new";
		this.requestMap.put("v", initialRequestValue);
		MockCallable<String> callable = new MockCallable<String>("result");
		String actualResult = FacesUtils.doWithRequestScopeVariable(this.context, "v", valueAtTimeOfCall, callable);
		assertThat("result not returned", actualResult, is("result"));
		assertThat("old value not restored", this.requestMap.get("v"), is(initialRequestValue));
		callable.assertCalled();
		assertThat("value not replaced", callable.requestMapAtTimeOfCall.get("v"), is(valueAtTimeOfCall));

	}

	@Test
	public void shouldDoWithRequestScopeVariableWithNullValue() throws Exception {
		this.requestMap.put("v", "old");
		MockCallable<String> callable = new MockCallable<String>("result");
		FacesUtils.doWithRequestScopeVariable(this.context, "v", null, callable);
		assertThat("old value not restored", this.requestMap.get("v"), is((Object) "old"));
		callable.assertCalled();
		assertThat("value not cleared", callable.requestMapAtTimeOfCall.get("v"), is(nullValue()));
	}

	@Test
	public void shouldNeedRunnableForDoWithRequestScopeVariable() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Runnable must not be null");
		FacesUtils.doWithRequestScopeVariable(this.context, "variableName", "value", (Runnable) null);
	}

	@Test
	public void shouldDoWithRequestScopeVariableRunnable() throws Exception {
		Object initialRequestValue = "old";
		Object valueAtTimeOfCall = "new";
		this.requestMap.put("v", initialRequestValue);
		final MockCallable<String> callable = new MockCallable<String>("result");
		FacesUtils.doWithRequestScopeVariable(this.context, "v", valueAtTimeOfCall, new Runnable() {
			public void run() {
				try {
					callable.call();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		assertThat("old value not restored", this.requestMap.get("v"), is(initialRequestValue));
		callable.assertCalled();
		assertThat("value not replaced", callable.requestMapAtTimeOfCall.get("v"), is(valueAtTimeOfCall));
	}

	@Test
	public void shouldWrapCheckedExceptionOnDoWithRequestScopeVariable() throws Exception {
		try {
			FacesUtils.doWithRequestScopeVariable(this.context, "v", "value", new Callable<String>() {
				public String call() throws Exception {
					throw new Exception("error");
				}
			});
			fail("Did not throw");
		} catch (RuntimeException e) {
			assertThat(e.getCause(), is(Exception.class));
			assertThat(e.getMessage(), is("error"));
		}
	}

	private class MockCallable<V> implements Callable<V> {

		private final V result;

		Map<String, Object> requestMapAtTimeOfCall;

		public MockCallable(V result) {
			this.result = result;
		}

		public V call() throws Exception {
			this.requestMapAtTimeOfCall = new HashMap<String, Object>(FacesUtilsTest.this.requestMap);
			return this.result;
		}

		public void assertCalled() {
			assertThat("Expected to be called", this.requestMapAtTimeOfCall, is(notNull()));
		}
	}
}
