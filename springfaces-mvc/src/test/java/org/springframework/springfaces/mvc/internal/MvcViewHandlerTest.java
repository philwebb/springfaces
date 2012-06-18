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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockUIViewRootWithModelSupport;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewDeclarationLanguage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.internal.MvcViewHandler.NavigationResponseUIViewRoot;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableView;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Tests for {@link MvcViewHandler}.
 * 
 * @author Phillip Webb
 */
public class MvcViewHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ViewHandler delegate;

	@Mock
	private DestinationViewResolver destinationViewResolver;

	@Mock
	private FacesContext context;

	@Mock
	private SpringFacesContext springFacesContext;

	@Mock
	private DestinationAndModelRegistry destinationAndModelRegistry;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	private String viewId = "viewId";

	private MvcViewHandler handler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		FacesContextSetter.setCurrentInstance(this.context);
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		given(this.context.getAttributes()).willReturn(attributes);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(this.context.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequestContextPath()).willReturn("/rc");
		given(externalContext.getRequestServletPath()).willReturn("/sp");
		given(externalContext.getRequestPathInfo()).willReturn("/si");
		given(externalContext.getRequest()).willReturn(this.request);
		given(externalContext.getResponse()).willReturn(this.response);
		PartialViewContext partialViewContext = mock(PartialViewContext.class);
		given(this.context.getPartialViewContext()).willReturn(partialViewContext);

		this.handler = new MvcViewHandler(this.delegate, this.destinationViewResolver) {
			@Override
			protected DestinationAndModelRegistry newDestinationAndModelRegistry() {
				return MvcViewHandlerTest.this.destinationAndModelRegistry;
			};
		};
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	private ModelAndViewArtifact mockModelAndViewArtifact() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("mk", "v");
		ModelAndViewArtifact modelAndViewArtifact = new ModelAndViewArtifact("mvc", model);
		return modelAndViewArtifact;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, ?> anyModel() {
		return anyMap();
	}

	@Test
	public void shouldRequireDelegate() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("DestinationViewResolver must not be null");
		new MvcViewHandler(this.delegate, null);
	}

	@Test
	public void shouldRequireDestinationViewResolver() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Delegate ViewResolver must not be null");
		new MvcViewHandler(null, this.destinationViewResolver);
	}

	@Test
	public void shouldWrapDelegate() throws Exception {
		assertThat(this.handler.getWrapped(), is(sameInstance(this.delegate)));
	}

	@Test
	public void shouldDelegateCreateViewIfNoSpringFacesContext() throws Exception {
		this.handler.createView(this.context, this.viewId);
		verify(this.delegate).createView(this.context, this.viewId);
	}

	@Test
	public void shouldCreateViewForNavigationResponse() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		setupDestination("test", mock(View.class));
		given(this.context.getCurrentPhaseId()).willReturn(PhaseId.INVOKE_APPLICATION);
		UIViewRoot view = this.handler.createView(this.context, "/test");
		verify(this.delegate, never()).createView(eq(this.context), anyString());
		assertThat(view, is(instanceOf(NavigationResponseUIViewRoot.class)));
	}

	@Test
	public void shouldNotCreateViewForNavigationResponseIfNotInCorrectPhase() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		setupDestination("test", mock(View.class));
		given(this.context.getCurrentPhaseId()).willReturn(PhaseId.RENDER_RESPONSE);
		UIViewRoot view = this.handler.createView(this.context, "/test");
		verify(this.delegate).createView(this.context, "/test");
		assertThat(view, is(not(instanceOf(NavigationResponseUIViewRoot.class))));
	}

	private DestinationAndModel setupDestination(String viewId, Object destination) {
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		given(destinationAndModel.getDestination()).willReturn(destination);
		given(this.destinationAndModelRegistry.get(this.context, viewId)).willReturn(destinationAndModel);
		return destinationAndModel;
	}

	@Test
	public void shouldCreateViewForMVCRender() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		ModelAndViewArtifact modelAndViewArtifact = mockModelAndViewArtifact();
		given(this.springFacesContext.getRendering()).willReturn(modelAndViewArtifact);
		given(this.delegate.createView(this.context, "mvc")).willReturn(viewRoot);
		UIViewRoot actual = this.handler.createView(this.context, "anything");
		assertThat(actual, is(sameInstance(viewRoot)));
		verify(this.delegate).createView(this.context, "mvc");
		assertThat(SpringFacesModelHolder.getModel(viewRoot).get("mk"), is(equalTo((Object) "v")));
	}

	@Test
	public void shouldDelegateRestoreView() throws Exception {
		this.handler.restoreView(this.context, this.viewId);
		verify(this.delegate).restoreView(this.context, this.viewId);
	}

	@Test
	public void shouldUseArtifactForRestoreViewOfMVCRender() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		ModelAndViewArtifact modelAndViewArtifact = mockModelAndViewArtifact();
		given(this.springFacesContext.getRendering()).willReturn(modelAndViewArtifact);
		this.handler.restoreView(this.context, "anything");
		verify(this.delegate).restoreView(this.context, "mvc");
	}

	@Test
	public void shouldDelegateGetActionURL() throws Exception {
		this.handler.getActionURL(this.context, this.viewId);
		verify(this.delegate).getActionURL(this.context, this.viewId);
	}

	@Test
	public void shouldUseSelfPostbackForActionURLFromMVCRender() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		ModelAndViewArtifact modelAndViewArtifact = mockModelAndViewArtifact();
		given(this.springFacesContext.getRendering()).willReturn(modelAndViewArtifact);
		given(this.delegate.createView(this.context, "mvc")).willReturn(viewRoot);
		this.handler.createView(this.context, "anything");
		String actionUrl = this.handler.getActionURL(this.context, "mvc");
		assertThat(actionUrl, is(equalTo("/rc/sp/si")));
	}

	@Test
	public void shouldUseSelfPostbackForActionURLFromMVCRenderWithoutPartialState() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		ModelAndViewArtifact modelAndViewArtifact = mockModelAndViewArtifact();
		given(this.springFacesContext.getRendering()).willReturn(modelAndViewArtifact);
		given(this.delegate.createView(this.context, "mvc")).willReturn(viewRoot);
		this.handler.restoreView(this.context, "anything");
		String actionUrl = this.handler.getActionURL(this.context, "mvc");
		assertThat(actionUrl, is(equalTo("/rc/sp/si")));
	}

	@Test
	public void shouldDelegateVDL() throws Exception {
		this.handler.getViewDeclarationLanguage(this.context, this.viewId);
		verify(this.delegate).getViewDeclarationLanguage(this.context, this.viewId);
	}

	@Test
	public void shouldReturnNullVdlForResolvedDestination() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		given(this.destinationAndModelRegistry.get(this.context, this.viewId)).willReturn(destinationAndModel);
		ViewDeclarationLanguage vdl = this.handler.getViewDeclarationLanguage(this.context, this.viewId);
		verify(this.delegate, never()).getViewDeclarationLanguage(eq(this.context), anyString());
		assertThat(vdl, is(nullValue()));
	}

	@Test
	public void shouldDelegateRenderView() throws Exception {
		UIViewRoot viewToRender = mockUIViewRootWithModelSupport();
		this.handler.renderView(this.context, viewToRender);
		verify(this.delegate).renderView(this.context, viewToRender);
	}

	@Test
	public void shouldRenderNavigationResponse() throws Exception {
		NavigationResponseUIViewRoot viewToRender = mock(NavigationResponseUIViewRoot.class);
		this.handler.renderView(this.context, viewToRender);
		verify(viewToRender).encodeAll(this.context);
		verify(this.delegate, never()).renderView(this.context, viewToRender);
	}

	@Test
	public void shouldDelegateGetBookmarkableUrl() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		this.handler.getBookmarkableURL(this.context, this.viewId, parameters, includeViewParams);
		verify(this.delegate).getBookmarkableURL(this.context, this.viewId, parameters, includeViewParams);
	}

	@Test
	public void shouldGetResolvableBookmark() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		BookmarkableView destination = mock(BookmarkableView.class);
		given(destinationAndModel.getDestination()).willReturn(destination);
		given(destination.getBookmarkUrl(anyModel(), any(HttpServletRequest.class))).willReturn("/bookmark");
		given(this.destinationAndModelRegistry.get(this.context, this.viewId)).willReturn(destinationAndModel);
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		String bookmark = this.handler.getBookmarkableURL(this.context, this.viewId, parameters, includeViewParams);
		assertThat(bookmark, is(equalTo("/bookmark")));
	}

	@Test
	public void shouldRequireBookmarkInterfaceIfResolved() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		View destination = mock(View.class);
		given(destinationAndModel.getDestination()).willReturn(destination);
		given(this.destinationAndModelRegistry.get(this.context, this.viewId)).willReturn(destinationAndModel);
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("must be an instance of interface " + BookmarkableView.class.getName());
		this.handler.getBookmarkableURL(this.context, this.viewId, parameters, includeViewParams);
	}

	@Test
	public void shouldDelegateGetRedirectUrl() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		this.handler.getRedirectURL(this.context, this.viewId, parameters, includeViewParams);
		verify(this.delegate).getRedirectURL(this.context, this.viewId, parameters, includeViewParams);
	}

	@Test
	public void shouldRedirectUsingResolvedBookmarkUrl() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		BookmarkableView destination = mock(BookmarkableView.class);
		given(destinationAndModel.getDestination()).willReturn(destination);
		given(destination.getBookmarkUrl(anyModel(), any(HttpServletRequest.class))).willReturn("/bookmark");
		given(this.destinationAndModelRegistry.get(this.context, this.viewId)).willReturn(destinationAndModel);
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		String redirect = this.handler.getRedirectURL(this.context, this.viewId, parameters, includeViewParams);
		assertThat(redirect, is(equalTo("/bookmark")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldResolveDestination() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		Object destination = "resolvableDestination";
		DestinationAndModel destinationAndModel = setupDestination("test", destination);
		given(this.context.getCurrentPhaseId()).willReturn(PhaseId.INVOKE_APPLICATION);
		View view = mock(View.class);
		Map<String, Object> model = Collections.<String, Object> singletonMap("m", "v");
		ModelAndView modelAndView = new ModelAndView(view, model);
		given(
				this.destinationViewResolver.resolveDestination(eq(this.context), eq(destination), any(Locale.class),
						any(SpringFacesModel.class))).willReturn(modelAndView);
		UIViewRoot createdView = this.handler.createView(this.context, "/test");
		assertThat(((NavigationResponseUIViewRoot) createdView).getModelAndView().getView(), is(sameInstance(view)));
		verify(destinationAndModel).getModel(any(FacesContext.class), anyMap(), eq(model));
	}

	@Test
	public void shouldRenderViewOnNavigationResponseEncode() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		View view = mock(View.class);
		Map<String, Object> model = new HashMap<String, Object>();
		ModelAndView modelAndView = new ModelAndView(view, model);
		NavigationResponseUIViewRoot viewRoot = new NavigationResponseUIViewRoot(this.viewId, null, modelAndView);
		viewRoot.encodeAll(this.context);
		verify(this.springFacesContext).render(view, model);
	}
}
