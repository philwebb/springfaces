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
package org.springframework.springfaces.mvc.internal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.exceptionhandler.ExceptionHandler;

/**
 * Tests for {@link MvcExceptionHandler}.
 * 
 * @author Phillip Webb
 */
@SuppressWarnings("deprecation")
public class MvcExceptionHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private MvcExceptionHandler facesHandler;

	private List<ExceptionHandler> exceptionHandlers;

	@Mock
	private SpringFacesContext context;

	@Mock
	private FacesContext facesContext;

	@Mock
	private javax.faces.context.ExceptionHandler wrapped;

	@Mock
	private ExceptionHandler handler1;

	@Mock
	private ExceptionHandler handler2;

	@Mock
	private ExceptionHandler handler3;

	private ArrayList<ExceptionQueuedEvent> events;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesContextSetter.setCurrentInstance(this.context);
		this.events = new ArrayList<ExceptionQueuedEvent>();
		given(this.wrapped.getUnhandledExceptionQueuedEvents()).willReturn(this.events);
		given(this.wrapped.getRootCause(isA(Throwable.class))).willAnswer(new Answer<Throwable>() {
			public Throwable answer(InvocationOnMock invocation) throws Throwable {
				return (Throwable) invocation.getArguments()[0];
			}
		});
		given(this.context.getFacesContext()).willReturn(this.facesContext);
		this.exceptionHandlers = new ArrayList<ExceptionHandler>();
		this.exceptionHandlers.add(this.handler1);
		this.exceptionHandlers.add(this.handler2);
		this.exceptionHandlers.add(this.handler3);
		this.facesHandler = new MvcExceptionHandler(this.wrapped, this.exceptionHandlers);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldNeedWrapped() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Wrapped must not be null");
		new MvcExceptionHandler(null, null);
	}

	@Test
	public void shouldSupportNullExceptionHandlers() throws Exception {
		new MvcExceptionHandler(this.wrapped, null);
		this.facesHandler.handle();
	}

	@Test
	public void shouldCallWrappedWhenHasSpringFacesContext() throws Exception {
		this.facesHandler.handle();
		verify(this.wrapped).handle();
	}

	@Test
	public void shouldCallWrappedWhenNoSpringFacesContext() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(null);
		this.facesHandler.handle();
		verify(this.wrapped).handle();
	}

	@Test
	public void shouldUnwapException() throws Exception {
		RuntimeException root = new RuntimeException();
		Exception exception = root;
		exception = new FacesException(exception);
		exception = new ELException(exception);
		exception = new EvaluationException(exception);
		assertThat(this.facesHandler.getRootCause(exception), is(sameInstance((Throwable) root)));
	}

	@Test
	public void shouldCallHandlersInOrderForEachEvent() throws Exception {
		Exception exception1 = new Exception();
		Exception exception2 = new Exception();
		this.events.add(new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext, exception1)));
		this.events.add(new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext, exception2)));
		this.facesHandler.handle();
		InOrder ordered = inOrder(this.handler1, this.handler2, this.handler3);
		ordered.verify(this.handler1).handle(this.context, exception1);
		ordered.verify(this.handler2).handle(this.context, exception1);
		ordered.verify(this.handler3).handle(this.context, exception1);
		ordered.verify(this.handler1).handle(this.context, exception2);
		ordered.verify(this.handler2).handle(this.context, exception2);
		ordered.verify(this.handler3).handle(this.context, exception2);
	}

	@Test
	public void shouldRemoveEventOnHandle() throws Exception {
		Exception exception = new Exception();
		this.events.add(new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext, exception)));
		given(this.handler1.handle(this.context, exception)).willReturn(true);
		this.facesHandler.handle();
		assertThat(this.events.size(), is(0));
	}

	@Test
	public void shouldNotCallSubsequentHandlers() throws Exception {
		Exception exception = new Exception();
		this.events.add(new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext, exception)));
		given(this.handler2.handle(this.context, exception)).willReturn(true);
		this.facesHandler.handle();
		verify(this.handler3, never()).handle(this.context, exception);
	}

	@Test
	public void shouldRethrowException() throws Exception {
		Exception exception = new Exception();
		this.events.add(new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext, exception)));
		given(this.handler2.handle(this.context, exception)).willThrow(new IllegalStateException("expected"));
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("expected");
		this.facesHandler.handle();
	}
}
