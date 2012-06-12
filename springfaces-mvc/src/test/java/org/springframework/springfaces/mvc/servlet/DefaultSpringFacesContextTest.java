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
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.faces.FacesException;
import javax.faces.FacesWrapper;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;

/**
 * Tests for {@link DefaultSpringFacesContext}.
 * 
 * @author Phillip Webb
 */
public class DefaultSpringFacesContextTest {

	private static ThreadLocal<DefaultSpringFacesContextTest> test = new ThreadLocal<DefaultSpringFacesContextTest>();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultSpringFacesContext springFacesContext;

	@Mock
	private LifecycleAccessor lifecycleAccessor;

	@Mock
	private WebApplicationContext webApplicationContext;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private Object handler;

	@Mock
	private FacesContext facesContext;

	@Before
	public void setup() {
		DefaultSpringFacesContextTest.test.set(this);
		MockitoAnnotations.initMocks(this);
		FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY, MockFacesContextFactory.class.getName());
		this.springFacesContext = new DefaultSpringFacesContext(this.lifecycleAccessor, this.webApplicationContext,
				this.request, this.response, this.handler);
	}

	@After
	public void cleanup() {
		DefaultSpringFacesContextTest.test.set(null);
	}

	@Test
	public void shouldNeedLifecycleAccessor() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("LifecycleAccessor must not be null");
		new DefaultSpringFacesContext(null, this.webApplicationContext, this.request, this.response, this.handler);
	}

	@Test
	public void shouldNeedWebApplicationContext() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("WebApplicationContext must not be null");
		new DefaultSpringFacesContext(this.lifecycleAccessor, null, this.request, this.response, this.handler);
	}

	@Test
	public void shouldNeedRequest() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Request must not be null");
		new DefaultSpringFacesContext(this.lifecycleAccessor, this.webApplicationContext, null, this.response,
				this.handler);
	}

	@Test
	public void shouldNeedResponse() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Response must not be null");
		new DefaultSpringFacesContext(this.lifecycleAccessor, this.webApplicationContext, this.request, null,
				this.handler);
	}

	@Test
	public void shouldNeedHandler() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Handler must not be null");
		new DefaultSpringFacesContext(this.lifecycleAccessor, this.webApplicationContext, this.request, this.response,
				null);
	}

	@Test
	public void shouldBeCurrentInstance() throws Exception {
		assertThat(SpringFacesContext.getCurrentInstance(),
				is(sameInstance((SpringFacesContext) this.springFacesContext)));
	}

	@Test
	public void shouldReleaseWithoutUse() throws Exception {
		this.springFacesContext.release();
	}

	@Test
	public void shouldReleaseDelegateOnRelease() throws Exception {
		triggerFacesContextLoad();
		this.springFacesContext.release();
		verify(this.facesContext).release();
	}

	@Test
	public void shouldGetHandler() throws Exception {
		assertThat(this.springFacesContext.getHandler(), is(sameInstance(this.handler)));
	}

	@Test
	public void shouldNotGetHandlerIfReleased() throws Exception {
		this.springFacesContext.release();
		expectSpringFacesContextReleasedException();
		this.springFacesContext.getHandler();
	}

	@Test
	public void shouldGetControllerIfNotHandlerMethod() throws Exception {
		assertThat(this.springFacesContext.getController(), is(sameInstance(this.handler)));
	}

	@Test
	public void shouldGetControllerIfHandlerMethod() throws Exception {
		Object controller = mock(Object.class, "controller");
		HandlerMethod handlerMethod = mock(HandlerMethod.class, "handler");
		this.springFacesContext = new DefaultSpringFacesContext(this.lifecycleAccessor, this.webApplicationContext,
				this.request, this.response, handlerMethod);
		given(handlerMethod.createWithResolvedBean()).willReturn(handlerMethod);
		given(handlerMethod.getBean()).willReturn(controller);
		assertThat(this.springFacesContext.getController(), is(sameInstance(controller)));
	}

	@Test
	public void shouldNotGetControllerIfReleased() throws Exception {
		this.springFacesContext.release();
		expectSpringFacesContextReleasedException();
		this.springFacesContext.getController();

	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldGetFacesContext() throws Exception {
		FacesContext actual = this.springFacesContext.getFacesContext();
		FacesWrapper<FacesContext> facesWrapper = (FacesWrapper<FacesContext>) actual;
		FacesContext wrapped = facesWrapper.getWrapped();
		assertThat(wrapped, is(sameInstance(this.facesContext)));
	}

	@Test
	public void shouldReferenceCountFacesContext() throws Exception {
		FacesContext first = this.springFacesContext.getFacesContext();
		first.getExternalContext();
		FacesContext second = this.springFacesContext.getFacesContext();
		second.getExternalContext();
		second.release();
		verify(this.facesContext, never()).release();
		first.release();
		verify(this.facesContext).release();
	}

	@Test
	public void shouldGetWebApplicationContext() throws Exception {
		assertThat(this.springFacesContext.getWebApplicationContext(), is(sameInstance(this.webApplicationContext)));
	}

	@Test
	public void shouldRender() throws Exception {
		ModelAndViewArtifact modelAndViewArtifact = new ModelAndViewArtifact("artifact");
		Lifecycle lifecycle = mock(Lifecycle.class);
		given(this.lifecycleAccessor.getLifecycle()).willReturn(lifecycle);
		this.springFacesContext.render(modelAndViewArtifact);
		verify(lifecycle).execute((FacesContext) any());
		verify(lifecycle).render((FacesContext) any());
	}

	@Test
	public void shouldNotRenderIfReleased() throws Exception {
		this.springFacesContext.release();
		ModelAndViewArtifact modelAndViewArtifact = new ModelAndViewArtifact("artifact");
		expectSpringFacesContextReleasedException();
		this.springFacesContext.render(modelAndViewArtifact);
	}

	@Test
	public void shouldNotHaveRenderingWhenNotRendering() throws Exception {
		assertThat(this.springFacesContext.getRendering(), is(nullValue()));
	}

	@Test
	public void shouldHaveRenderingWhenRendering() throws Exception {
		final ModelAndViewArtifact modelAndViewArtifact = new ModelAndViewArtifact("artifact");
		Lifecycle lifecycle = mock(Lifecycle.class);
		given(this.lifecycleAccessor.getLifecycle()).willReturn(lifecycle);
		willAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				assertThat(DefaultSpringFacesContextTest.this.springFacesContext.getRendering(),
						is(modelAndViewArtifact));
				return null;
			}
		}).given(lifecycle).render((FacesContext) any());
		this.springFacesContext.render(modelAndViewArtifact);
		assertThat(this.springFacesContext.getRendering(), is(nullValue()));
	}

	@Test
	public void shouldNotDoubleRender() throws Exception {
		final ModelAndViewArtifact modelAndViewArtifact = new ModelAndViewArtifact("theartifact");
		Lifecycle lifecycle = mock(Lifecycle.class);
		given(this.lifecycleAccessor.getLifecycle()).willReturn(lifecycle);
		willAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				DefaultSpringFacesContextTest.this.springFacesContext.render(new ModelAndViewArtifact("newartifact"));
				return null;
			}
		}).given(lifecycle).render((FacesContext) any());
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to render newartifact as the SpringFacesContext is "
				+ "already rendering theartifact");
		this.springFacesContext.render(modelAndViewArtifact);

	}

	@Test
	public void shouldClearRenderingEvenOnException() throws Exception {
		final ModelAndViewArtifact modelAndViewArtifact = new ModelAndViewArtifact("theartifact");
		Lifecycle lifecycle = mock(Lifecycle.class);
		given(this.lifecycleAccessor.getLifecycle()).willReturn(lifecycle);
		willThrow(new IllegalStateException()).given(lifecycle).render((FacesContext) any());
		this.thrown.expect(IllegalStateException.class);
		this.springFacesContext.render(modelAndViewArtifact);
		assertThat(this.springFacesContext.getRendering(), is(nullValue()));
	}

	private void triggerFacesContextLoad() {
		this.springFacesContext.getFacesContext().getExternalContext();
	}

	private void expectSpringFacesContextReleasedException() {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("The SpringFacesContext has been released");
	}

	public static class MockFacesContextFactory extends FacesContextFactory {

		@Override
		public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
				throws FacesException {
			return test.get().facesContext;
		}
	}
}
