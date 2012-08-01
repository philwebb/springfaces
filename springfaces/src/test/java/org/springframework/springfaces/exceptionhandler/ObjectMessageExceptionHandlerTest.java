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
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.Ordered;
import org.springframework.springfaces.message.NoSuchObjectMessageException;
import org.springframework.springfaces.message.ObjectMessageSource;

/**
 * Tests for {@link ObjectMessageExceptionHandler}.
 * 
 * @author Phillip Webb
 */
public class ObjectMessageExceptionHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ObjectMessageExceptionHandler handler = new ObjectMessageExceptionHandler();

	private Throwable exception = new RuntimeException();

	@Mock
	private ExceptionQueuedEvent event;

	@Mock
	private ExceptionQueuedEventContext exceptionQueuedEventContext;

	@Mock
	private FacesContext facesContext;

	@Mock
	private ObjectMessageSource messageSource;

	@Mock
	private ExternalContext externalContext;

	@Captor
	private ArgumentCaptor<FacesMessage> messageCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		given(this.event.getContext()).willReturn(this.exceptionQueuedEventContext);
		given(this.exceptionQueuedEventContext.getContext()).willReturn(this.facesContext);
		given(this.facesContext.getExternalContext()).willReturn(this.externalContext);
	}

	@Test
	public void shouldNeedMessageSource() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("MessageSource must not be null");
		this.handler.handle(this.exception, this.event);
	}

	@Test
	public void shouldHandleMappedMessage() throws Exception {
		this.handler.setMessageSource(this.messageSource);
		given(this.messageSource.getMessage(this.exception, null, null)).willReturn("message");
		boolean result = this.handler.handle(this.exception, this.event);
		assertThat(result, is(true));
		verify(this.facesContext).addMessage(isNull(String.class), this.messageCaptor.capture());
		assertThat(this.messageCaptor.getValue().getSummary(), is("message"));
	}

	@Test
	public void shouldNotHandleMissingMessage() throws Exception {
		this.handler.setMessageSource(this.messageSource);
		given(this.messageSource.getMessage(this.exception, null, null)).willThrow(
				new NoSuchObjectMessageException(this.exception, null));
		boolean result = this.handler.handle(this.exception, this.event);
		assertThat(result, is(false));
		verify(this.facesContext, never()).addMessage(anyString(), isA(FacesMessage.class));
	}

	@Test
	public void shouldHaveOrder() throws Exception {
		assertThat(this.handler, is(Ordered.class));
		assertThat(this.handler.getOrder(), is(-1));
	}
}
