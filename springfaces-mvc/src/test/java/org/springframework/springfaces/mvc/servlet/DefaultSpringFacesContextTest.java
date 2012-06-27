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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.FacesWrapper;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.PartialViewContext;
import javax.faces.lifecycle.Lifecycle;
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
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.springfaces.mvc.servlet.view.FacesRenderedView;
import org.springframework.springfaces.mvc.servlet.view.FacesView;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.View;

/**
 * Tests for {@link DefaultSpringFacesContext}.
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

	@Mock
	private ExternalContext externalContext;

	@Mock
	private PartialViewContext partialViewContext;

	@Captor
	private ArgumentCaptor<FacesContext> facesContextCaptor;

	@Before
	public void setup() {
		DefaultSpringFacesContextTest.test.set(this);
		MockitoAnnotations.initMocks(this);
		FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY, MockFacesContextFactory.class.getName());
		this.springFacesContext = new DefaultSpringFacesContext(this.lifecycleAccessor, this.webApplicationContext,
				this.request, this.response, this.handler);
		given(this.facesContext.getExternalContext()).willReturn(this.externalContext);
		given(this.externalContext.getRequest()).willReturn(this.request);
		given(this.externalContext.getResponse()).willReturn(this.response);
		given(this.facesContext.getPartialViewContext()).willReturn(this.partialViewContext);
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
	public void shouldGetFacesContext() throws Exception {
		FacesContext actual = this.springFacesContext.getFacesContext();
		assertIsWrappedFacesContext(actual);
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
	public void shouldRenderNonFacesView() throws Exception {
		View view = mock(View.class);
		Map<String, Object> model = new HashMap<String, Object>();
		this.springFacesContext.render(view, model);
		verify(view).render(model, this.request, this.response);
	}

	@Test
	public void shouldRenderFacesRenderedView() throws Exception {
		FacesRenderedView view = mock(FacesRenderedView.class);
		Map<String, Object> model = new HashMap<String, Object>();
		this.springFacesContext.render(view, model);
		verify(view).render(eq(model), this.facesContextCaptor.capture());
		assertIsWrappedFacesContext(this.facesContextCaptor.getValue());
	}

	@Test
	public void shouldRenderFacesView() throws Exception {
		FacesView view = mock(FacesView.class);
		given(view.getViewArtifact()).willReturn(new ViewArtifact("artifact"));
		Map<String, Object> model = new HashMap<String, Object>();
		Lifecycle lifecycle = mock(Lifecycle.class);
		given(this.lifecycleAccessor.getLifecycle()).willReturn(lifecycle);
		this.springFacesContext.render(view, model);
		verify(lifecycle).execute(this.facesContextCaptor.capture());
		assertIsWrappedFacesContext(this.facesContextCaptor.getValue());
		verify(lifecycle).render(this.facesContextCaptor.capture());
		assertIsWrappedFacesContext(this.facesContextCaptor.getValue());
	}

	@Test
	public void shouldNotRenderIfReleased() throws Exception {
		this.springFacesContext.release();
		expectSpringFacesContextReleasedException();
		this.springFacesContext.render(mock(View.class), null);
	}

	@Test
	public void shouldNotHaveRenderingWhenNotRendering() throws Exception {
		assertThat(this.springFacesContext.getRendering(), is(nullValue()));
	}

	@Test
	public void shouldHaveRenderingWhenRendering() throws Exception {
		FacesView view = mock(FacesView.class);
		given(view.getViewArtifact()).willReturn(new ViewArtifact("artifact"));
		Map<String, Object> model = new HashMap<String, Object>();
		final ModelAndViewArtifact modelAndViewArtifact = new ModelAndViewArtifact("artifact", model);
		Lifecycle lifecycle = mock(Lifecycle.class);
		given(this.lifecycleAccessor.getLifecycle()).willReturn(lifecycle);
		willAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				assertThat(DefaultSpringFacesContextTest.this.springFacesContext.getRendering(),
						is(equalTo(modelAndViewArtifact)));
				return null;
			}
		}).given(lifecycle).render(any(FacesContext.class));
		this.springFacesContext.render(view, model);
		assertThat(this.springFacesContext.getRendering(), is(nullValue()));
	}

	@Test
	public void shouldClearRenderingEvenOnException() throws Exception {
		FacesView view = mock(FacesView.class);
		given(view.getViewArtifact()).willReturn(new ViewArtifact("theartifact"));
		Map<String, Object> model = new HashMap<String, Object>();
		Lifecycle lifecycle = mock(Lifecycle.class);
		given(this.lifecycleAccessor.getLifecycle()).willReturn(lifecycle);
		willThrow(new IllegalStateException()).given(lifecycle).render((FacesContext) any());
		this.thrown.expect(IllegalStateException.class);
		this.springFacesContext.render(view, model);
		assertThat(this.springFacesContext.getRendering(), is(nullValue()));
	}

	@Test
	public void shouldNotRenderMVCViewIfPartialRequest() throws Exception {
		View view = mock(View.class);
		Map<String, Object> model = new HashMap<String, Object>();
		given(this.partialViewContext.isPartialRequest()).willReturn(true);
		given(this.partialViewContext.isAjaxRequest()).willReturn(true);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to render MVC response to Faces AJAX request");
		this.springFacesContext.render(view, model);
	}

	@Test
	public void shouldSetViewRootIfDoubleRendering() throws Exception {
		FacesView view = mock(FacesView.class);
		given(view.getViewArtifact()).willReturn(new ViewArtifact("theartifact"));
		final Map<String, Object> model = new HashMap<String, Object>();

		Application application = mock(Application.class);
		ViewHandler viewHandler = mock(ViewHandler.class);
		final UIViewRoot newViewRoot = mock(UIViewRoot.class);
		given(this.facesContext.getApplication()).willReturn(application);
		given(application.getViewHandler()).willReturn(viewHandler);
		given(viewHandler.createView(any(FacesContext.class), eq("newartifact"))).willReturn(newViewRoot);

		Lifecycle lifecycle = mock(Lifecycle.class);
		given(this.lifecycleAccessor.getLifecycle()).willReturn(lifecycle);
		willAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				FacesView view2 = mock(FacesView.class);
				given(view2.getViewArtifact()).willReturn(new ViewArtifact("newartifact"));
				DefaultSpringFacesContextTest.this.springFacesContext.render(view2, model);
				verify(DefaultSpringFacesContextTest.this.facesContext).setViewRoot(newViewRoot);
				return null;
			}
		}).given(lifecycle).render((FacesContext) any());
		this.springFacesContext.render(view, model);
	}

	@SuppressWarnings("unchecked")
	private void assertIsWrappedFacesContext(FacesContext actual) {
		FacesWrapper<FacesContext> facesWrapper = (FacesWrapper<FacesContext>) actual;
		FacesContext wrapped = facesWrapper.getWrapped();
		assertThat(wrapped, is(sameInstance(this.facesContext)));
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
