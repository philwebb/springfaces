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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Tests for {@link DefaultDispatcher}.
 * 
 * @author Phillip Webb
 */
public class DefaultDispatcherTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultDispatcher dispatcher;

	@Mock
	private DefaultDispatcher.Delegate delegate;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.dispatcher = new MockDefaultDispatcher();
	}

	@Test
	public void shouldDelegateRefresh() throws Exception {
		ApplicationContext context = mock(ApplicationContext.class);
		ContextRefreshedEvent event = new ContextRefreshedEvent(context);
		this.dispatcher.onApplicationEvent(event);
		verify(this.delegate).onRefresh(context);
	}

	@Test
	public void shouldDelegateGetHandler() throws Exception {
		this.dispatcher.getHandler(this.request);
		verify(this.delegate).getHandler(this.request);
	}

	@Test
	public void shouldDelegateResolveViewName() throws Exception {
		Locale locale = Locale.UK;
		Map<String, Object> model = Collections.<String, Object> singletonMap("key", "value");
		this.dispatcher.resolveViewName("viewName", model, locale, this.request);
		verify(this.delegate).resolveViewName("viewName", model, locale, this.request);
	}

	@Test
	public void shouldDelegateProcessHandlerException() throws Exception {
		Object handler = new Object();
		Exception ex = new Exception();
		this.dispatcher.processHandlerException(this.request, this.response, handler, ex);
		verify(this.delegate).processHandlerException(this.request, this.response, handler, ex);
	}

	@Test
	public void shouldDelegateSetDetectAllHandlerMappings() throws Exception {
		this.dispatcher.setDetectAllHandlerMappings(true);
		verify(this.delegate).setDetectAllHandlerMappings(true);
	}

	@Test
	public void shouldDelegateSetDetectAllHandlerAdapters() throws Exception {
		this.dispatcher.setDetectAllHandlerAdapters(true);
		verify(this.delegate).setDetectAllHandlerAdapters(true);
	}

	@Test
	public void shouldDelegateSetDetectAllHandlerExceptionResolvers() throws Exception {
		this.dispatcher.setDetectAllHandlerExceptionResolvers(true);
		verify(this.delegate).setDetectAllHandlerExceptionResolvers(true);
	}

	@Test
	public void shouldDelegateSetDetectAllViewResolvers() throws Exception {
		this.dispatcher.setDetectAllViewResolvers(true);
		verify(this.delegate).setDetectAllViewResolvers(true);
	}

	@Test
	public void shouldDelegateSetCleanupAfterInclude() throws Exception {
		this.dispatcher.setCleanupAfterInclude(true);
		verify(this.delegate).setCleanupAfterInclude(true);
	}

	private class MockDefaultDispatcher extends DefaultDispatcher {
		@Override
		protected Delegate createDelegate() {
			return DefaultDispatcherTest.this.delegate;
		}
	}

}
