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
package org.springframework.springfaces.mvc.servlet;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Tests for {@link FacesHandlerInterceptor}.
 * 
 * @author Phillip Webb
 */
public class FacesHandlerInterceptorTest {

	private MockFacesHandlerInterceptor interceptor;

	@Mock
	private LifecycleAccess lifecycleAccess;

	@Mock
	Object handler;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private WebApplicationContext webApplicationContext;

	@Mock
	private ServletContext servletContext;

	private Object initializedSpringFacesContextHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		given(this.request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE)).willReturn(
				this.webApplicationContext);
		this.interceptor = new MockFacesHandlerInterceptor();

	}

	@Test
	public void shouldPreHandlerNonPostback() throws Exception {
		boolean proceed = this.interceptor.preHandle(this.request, this.response, this.handler);
		assertThat(proceed, is(true));
		assertThat(this.initializedSpringFacesContextHandler, is(sameInstance(this.handler)));
	}

	@Test
	public void shouldPreHandlePostback() throws Exception {
		ViewArtifact viewArtifact = new ViewArtifact("test");
		Postback postback = new Postback(viewArtifact, this.handler);
		boolean proceed = this.interceptor.preHandle(this.request, this.response, postback);
		assertThat(proceed, is(true));
		assertThat(this.initializedSpringFacesContextHandler, is(sameInstance(this.handler)));
	}

	@Test
	public void shouldIgnoreNullSpringFacesContextAfterCompletion() throws Exception {
		assertThat(SpringFacesContext.getCurrentInstance(false), is(nullValue()));
		this.interceptor.afterCompletion(this.request, this.response, this.handler, null);
	}

	@Test
	public void shouldReleaseSpringFacesContextAfterCompletion() throws Exception {
		DefaultSpringFacesContext context = mock(DefaultSpringFacesContext.class);
		SpringFacesContextSetter.setCurrentInstance(context);
		assertThat(SpringFacesContext.getCurrentInstance(false), is(not(nullValue())));
		this.interceptor.afterCompletion(this.request, this.response, this.handler, null);
		verify(context).release();
	}

	@Test
	public void shouldDelegeSetServletContextToLifecycleAccess() throws Exception {
		this.interceptor.setServletContext(this.servletContext);
		verify(this.lifecycleAccess).setServletContext(this.servletContext);
	}

	@Test
	public void shouldDelegateSetLifecycleId() throws Exception {
		String lifecycleId = "id";
		this.interceptor.setLifecycleId(lifecycleId);
		verify(this.lifecycleAccess).setLifecycleId(lifecycleId);
	}

	private class MockFacesHandlerInterceptor extends FacesHandlerInterceptor {

		@Override
		protected LifecycleAccess createLifecycleAccess() {
			return FacesHandlerInterceptorTest.this.lifecycleAccess;
		}

		@Override
		protected void initializeSpringFacesContext(LifecycleAccess lifecycleAccess,
				WebApplicationContext webApplicationContext, HttpServletRequest request, HttpServletResponse response,
				Object handler) {
			assertThat(lifecycleAccess, is(FacesHandlerInterceptorTest.this.lifecycleAccess));
			assertThat(request, is(FacesHandlerInterceptorTest.this.request));
			assertThat(response, is(FacesHandlerInterceptorTest.this.response));
			FacesHandlerInterceptorTest.this.initializedSpringFacesContextHandler = handler;
		}
	}
}
