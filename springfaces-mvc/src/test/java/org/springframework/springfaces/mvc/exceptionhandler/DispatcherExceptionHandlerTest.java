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
package org.springframework.springfaces.mvc.exceptionhandler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.servlet.Dispatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.util.WebUtils;

/**
 * Tests for {@link DispatcherExceptionHandler}.
 * @author Phillip Webb
 */
public class DispatcherExceptionHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DispatcherExceptionHandler exceptionHandler;

	@Mock
	private Dispatcher dispatcher;

	@Mock
	private ExceptionQueuedEvent event;

	@Mock
	private SpringFacesContext context;

	@Mock
	private FacesContext facesContext;

	@Mock
	private ExternalContext externalContext;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private Object handler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.exceptionHandler = new DispatcherExceptionHandler(this.dispatcher);
		given(this.context.getFacesContext()).willReturn(this.facesContext);
		given(this.facesContext.getExternalContext()).willReturn(this.externalContext);
		given(this.externalContext.getRequest()).willReturn(this.request);
		given(this.externalContext.getResponse()).willReturn(this.response);
		given(this.context.getHandler()).willReturn(this.handler);
		SpringFacesContextSetter.setCurrentInstance(this.context);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldNeedDispatcher() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Dispatcher must not be null");
		new DispatcherExceptionHandler(null);
	}

	@Test
	public void shouldNotHandleIfNoSpringFacesContext() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(null);
		Exception exception = mock(Exception.class);
		boolean handled = this.exceptionHandler.handle(exception, this.event);
		assertThat(handled, is(false));
	}

	@Test
	public void shouldNotHandleIfDispatcherReturnsNull() throws Exception {
		Exception exception = new Exception();
		boolean handled = this.exceptionHandler.handle(exception, this.event);
		assertThat(handled, is(false));
	}

	@Test
	public void shouldNotHandleIfDispatcherThrows() throws Exception {
		Exception exception = new Exception();
		given(this.dispatcher.processHandlerException(this.request, this.response, this.exceptionHandler, exception))
				.willThrow(exception);
		boolean handled = this.exceptionHandler.handle(exception, this.event);
		assertThat(handled, is(false));
	}

	@Test
	public void shouldHandleViaDispatcher() throws Exception {
		Exception exception = new Exception();
		View view = mock(View.class);
		Map<String, Object> model = new HashMap<String, Object>();
		ModelAndView modelAndView = new ModelAndView(view, model);
		given(this.dispatcher.processHandlerException(this.request, this.response, this.handler, exception))
				.willReturn(modelAndView);
		boolean handled = this.exceptionHandler.handle(exception, this.event);
		verify(this.request).removeAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE);
		verify(this.request).removeAttribute(WebUtils.ERROR_EXCEPTION_TYPE_ATTRIBUTE);
		verify(this.request).removeAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE);
		verify(this.request).removeAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
		verify(this.request).removeAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE);
		verify(this.request).removeAttribute(WebUtils.ERROR_SERVLET_NAME_ATTRIBUTE);
		verify(this.context).render(view, model);
		assertThat(handled, is(true));
	}

	@Test
	public void shouldResolvViewReferences() throws Exception {
		Exception exception = new Exception();
		Map<String, Object> model = new HashMap<String, Object>();
		ModelAndView modelAndView = new ModelAndView("view", model);
		given(this.dispatcher.processHandlerException(this.request, this.response, this.handler, exception))
				.willReturn(modelAndView);
		View view = mock(View.class);
		given(this.dispatcher.resolveViewName("view", model, null, this.request)).willReturn(view);
		boolean handled = this.exceptionHandler.handle(exception, this.event);
		assertThat(modelAndView.getView(), is(view));
		assertThat(handled, is(true));
	}
}
