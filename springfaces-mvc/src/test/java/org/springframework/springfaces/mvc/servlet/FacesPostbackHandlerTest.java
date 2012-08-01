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
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.FacesViewStateHandler;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.springfaces.mvc.servlet.view.FacesView;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * Tests for {@link FacesPostbackHandler}.
 * 
 * @author Phillip Webb
 */
public class FacesPostbackHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private FacesPostbackHandler postbackHandler;

	@Mock
	private FacesViewStateHandler stateHandler;

	@Mock
	private Dispatcher dispatcher;

	@Mock
	private HttpServletRequest request;

	@Mock
	private Object originalHandler;

	private StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();

	private FacesHandlerInterceptor facesHandlerInterceptor;

	@Mock
	private SpringFacesContext springFacesContext;

	@Captor
	private ArgumentCaptor<View> viewCaptor;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		given(this.dispatcher.getHandler(any(HttpServletRequest.class))).willAnswer(
				new Answer<HandlerExecutionChain>() {

					public HandlerExecutionChain answer(InvocationOnMock invocation) throws Throwable {
						HttpServletRequest request = (HttpServletRequest) invocation.getArguments()[0];
						// Call the postback handler to simulate what the real dispatcher does
						HandlerExecutionChain handler = FacesPostbackHandlerTest.this.postbackHandler
								.getHandler(request);
						if (handler != null) {
							return handler;
						}
						if ("GET".equals(request.getMethod())) {
							return new HandlerExecutionChain(FacesPostbackHandlerTest.this.originalHandler);
						}
						throw new IllegalStateException();
					}
				});
		given(this.request.getMethod()).willReturn("POST");
		given(this.request.getContextPath()).willReturn("/context");
		given(this.request.getServletPath()).willReturn("/servlet");
		given(this.request.getRequestURI()).willReturn("http://localhost:8080/context/path");
		this.postbackHandler = new FacesPostbackHandler(this.stateHandler);
		this.postbackHandler.setDispatcher(this.dispatcher);
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldNeedStateHandler() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("StateHandler must not be null");
		new FacesPostbackHandler(null);
	}

	@Test
	public void shouldBeHighestPrecedence() throws Exception {
		assertThat(this.postbackHandler.getOrder(), is(Ordered.HIGHEST_PRECEDENCE));
	}

	@Test
	public void shouldFailOnMultipleFacesHandlerInterceptors() throws Exception {
		this.applicationContext.registerBeanDefinition("bean1",
				newInterceptorBeanDefinition(new FacesHandlerInterceptor()));
		this.applicationContext.registerBeanDefinition("bean2",
				newInterceptorBeanDefinition(new FacesHandlerInterceptor()));
		this.applicationContext.refresh();
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Multiple FacesHandlerInterceptor registered within the web context");
		this.postbackHandler.setApplicationContext(this.applicationContext);
	}

	@Test
	public void shouldFailOnNoFacesHandlerInterceptors() throws Exception {
		this.applicationContext.refresh();
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("No FacesHandlerInterceptor registered within the web context");
		this.postbackHandler.setApplicationContext(this.applicationContext);
	}

	@Test
	public void shouldDetectFacesHandlerInterceptors() throws Exception {
		setupFacesHandlerInterceptor();
		HandlerInterceptor[] interceptors = this.postbackHandler.getHandlerInterceptors();
		assertThat(interceptors.length, is(1));
		assertThat(interceptors[0], is((HandlerInterceptor) this.facesHandlerInterceptor));
	}

	@Test
	public void shouldReturnNullHandlerWhenNoState() throws Exception {
		setupFacesHandlerInterceptor();
		Object handler = this.postbackHandler.getHandler(this.request);
		assertThat(handler, is(nullValue()));
	}

	@Test
	public void shouldGetHandlerWhenHasState() throws Exception {
		setupFacesHandlerInterceptor();
		ViewArtifact viewArtifact = new ViewArtifact("artifact");
		given(this.stateHandler.read(this.request)).willReturn(viewArtifact);
		HandlerExecutionChain handler = this.postbackHandler.getHandler(this.request);
		Postback postback = (Postback) handler.getHandler();
		assertThat(postback, is(not(nullValue())));
		assertThat(postback.getHandler(), is(this.originalHandler));
	}

	@Test
	public void shouldSupportPostback() throws Exception {
		Postback postback = mock(Postback.class);
		Object nonPostback = mock(Object.class);
		assertThat(this.postbackHandler.supports(postback), is(true));
		assertThat(this.postbackHandler.supports(nonPostback), is(false));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldRenderOnHandle() throws Exception {
		ViewArtifact viewArtifact = new ViewArtifact("artifact");
		Postback handler = new Postback(viewArtifact, this.originalHandler);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ModelAndView result = this.postbackHandler.handle(this.request, response, handler);
		verify(this.springFacesContext).render(this.viewCaptor.capture(), (Map<String, Object>) isNull());
		assertThat(((FacesView) this.viewCaptor.getValue()).getViewArtifact(), is(viewArtifact));
		assertThat(result, is(nullValue()));
	}

	@Test
	public void shouldNotHaveLastModified() throws Exception {
		Object handler = mock(Object.class);
		assertThat(this.postbackHandler.getLastModified(this.request, handler), is(-1L));
	}

	private void setupFacesHandlerInterceptor() {
		this.facesHandlerInterceptor = new FacesHandlerInterceptor();
		this.applicationContext.registerBeanDefinition("bean",
				newInterceptorBeanDefinition(this.facesHandlerInterceptor));
		this.applicationContext.refresh();
		this.postbackHandler.setApplicationContext(this.applicationContext);
	}

	private GenericBeanDefinition newInterceptorBeanDefinition(HandlerInterceptor interceptor) {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(MappedInterceptor.class);
		ConstructorArgumentValues constructorArgs = new ConstructorArgumentValues();
		constructorArgs.addIndexedArgumentValue(0, new String[] {});
		constructorArgs.addIndexedArgumentValue(1, interceptor);
		beanDefinition.setConstructorArgumentValues(constructorArgs);
		return beanDefinition;
	}
}
