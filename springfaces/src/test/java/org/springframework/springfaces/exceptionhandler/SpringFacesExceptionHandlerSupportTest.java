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
package org.springframework.springfaces.exceptionhandler;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.springfaces.exceptionhandler.SpringFacesExceptionHandlerSupport.SpringFacesExceptionHandler;

/**
 * Tests for {@link SpringFacesExceptionHandlerSupport}.
 * @author Phillip Webb
 */
@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public class SpringFacesExceptionHandlerSupportTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private javax.faces.context.ExceptionHandler wrapped;

	@Mock
	private FacesContext facesContext;

	private SpringFacesExceptionHandler exceptionHandler;

	private ExceptionHandler handler1 = mockExceptionHandler(1);

	private ExceptionHandler handler2 = mockExceptionHandler(2);

	private ExceptionHandler handler3 = mockExceptionHandler(3);

	private ArrayList<ExceptionQueuedEvent> events;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesExceptionHandlerSupport support = new SpringFacesExceptionHandlerSupport();
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		this.events = new ArrayList<ExceptionQueuedEvent>();
		given(this.wrapped.getUnhandledExceptionQueuedEvents()).willReturn(this.events);
		given(this.wrapped.getRootCause(any(Throwable.class))).will(new Answer<Throwable>() {
			public Throwable answer(InvocationOnMock invocation) throws Throwable {
				return (Throwable) invocation.getArguments()[0];
			}
		});
		Map<String, ExceptionHandler> beans = new LinkedHashMap<String, ExceptionHandler>();
		beans.put("handler3", this.handler3);
		beans.put("handler2", this.handler2);
		beans.put("handler1", this.handler1);
		given(applicationContext.getBeansOfType(ExceptionHandler.class, true, true)).willReturn(beans);
		support.setApplicationContext(applicationContext);
		support.onApplicationEvent(new ContextRefreshedEvent(applicationContext));
		this.exceptionHandler = (SpringFacesExceptionHandler) support.newWrapper(ExceptionHandler.class, this.wrapped);
	}

	private ExceptionHandler mockExceptionHandler(int order) {
		ExceptionHandler hander = mock(ExceptionHandler.class, withSettings().extraInterfaces(Ordered.class));
		given(((Ordered) hander).getOrder()).willReturn(order);
		return hander;
	}

	@Test
	public void shouldCreateHandler() throws Exception {
		assertThat(this.exceptionHandler, is(not(nullValue())));
	}

	@Test
	public void shouldCallWrappedHandler() throws Exception {
		this.exceptionHandler.handle();
		verify(this.wrapped).handle();
	}

	@Test
	public void shouldUnwapException() throws Exception {
		RuntimeException root = new RuntimeException();
		Exception exception = root;
		exception = new FacesException(exception);
		exception = new ELException(exception);
		exception = new EvaluationException(exception);
		assertThat(this.exceptionHandler.getRootCause(exception), is(sameInstance((Throwable) root)));
	}

	@Test
	public void shouldCallHandlersInOrderForEachEvent() throws Exception {
		Exception exception1 = new Exception();
		ExceptionQueuedEvent event1 = new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext,
				exception1));
		Exception exception2 = new Exception();
		ExceptionQueuedEvent event2 = new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext,
				exception2));
		this.events.add(event1);
		this.events.add(event2);
		this.exceptionHandler.handle();
		InOrder ordered = inOrder(this.handler1, this.handler2, this.handler3);
		ordered.verify(this.handler1).handle(exception1, event1);
		ordered.verify(this.handler2).handle(exception1, event1);
		ordered.verify(this.handler3).handle(exception1, event1);
		ordered.verify(this.handler1).handle(exception2, event2);
		ordered.verify(this.handler2).handle(exception2, event2);
		ordered.verify(this.handler3).handle(exception2, event2);
	}

	@Test
	public void shouldRemoveEventOnHandle() throws Exception {
		Exception exception = new Exception();
		ExceptionQueuedEvent event = new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext,
				exception));
		this.events.add(event);
		given(this.handler1.handle(exception, event)).willReturn(true);
		this.exceptionHandler.handle();
		assertThat(this.events.size(), is(0));
	}

	@Test
	public void shouldNotCallSubsequentHandlers() throws Exception {
		Exception exception = new Exception();
		ExceptionQueuedEvent event = new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext,
				exception));
		this.events.add(event);
		given(this.handler2.handle(exception, event)).willReturn(true);
		this.exceptionHandler.handle();
		verify(this.handler3, never()).handle(exception, event);
	}

	@Test
	public void shouldRethrowException() throws Exception {
		Exception exception = new Exception();
		ExceptionQueuedEvent event = new ExceptionQueuedEvent(new ExceptionQueuedEventContext(this.facesContext,
				exception));
		this.events.add(event);
		given(this.handler2.handle(exception, event)).willThrow(new IllegalStateException("expected"));
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("expected");
		this.exceptionHandler.handle();
	}
}
