package org.springframework.springfaces.mvc.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
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
		FacesContextSetter.setCurrentInstance(context);
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		given(context.getAttributes()).willReturn(attributes);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(context.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequestContextPath()).willReturn("/rc");
		given(externalContext.getRequestServletPath()).willReturn("/sp");
		given(externalContext.getRequestPathInfo()).willReturn("/si");
		given(externalContext.getRequest()).willReturn(request);
		given(externalContext.getResponse()).willReturn(response);
		PartialViewContext partialViewContext = mock(PartialViewContext.class);
		given(context.getPartialViewContext()).willReturn(partialViewContext);

		handler = new MvcViewHandler(delegate, destinationViewResolver) {
			protected DestinationAndModelRegistry newDestinationAndModelRegistry() {
				return destinationAndModelRegistry;
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
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("DestinationViewResolver must not be null");
		new MvcViewHandler(delegate, null);
	}

	@Test
	public void shouldRequireDestinationViewResolver() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Delegate ViewResolver must not be null");
		new MvcViewHandler(null, destinationViewResolver);
	}

	@Test
	public void shouldWrapDelegate() throws Exception {
		assertSame(delegate, handler.getWrapped());
	}

	@Test
	public void shouldDelegateCreateViewIfNoSpringFacesContext() throws Exception {
		handler.createView(context, viewId);
		verify(delegate).createView(context, viewId);
	}

	@Test
	public void shouldCreateViewForNavigationResponse() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		setupDestination("test", mock(View.class));
		given(context.getCurrentPhaseId()).willReturn(PhaseId.INVOKE_APPLICATION);
		UIViewRoot view = handler.createView(context, "/test");
		verify(delegate, never()).createView(eq(context), anyString());
		assertTrue(view instanceof NavigationResponseUIViewRoot);
	}

	@Test
	public void shouldNotCreateViewForNavigationResponseIfNotInCorrectPhase() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		setupDestination("test", mock(View.class));
		given(context.getCurrentPhaseId()).willReturn(PhaseId.RENDER_RESPONSE);
		UIViewRoot view = handler.createView(context, "/test");
		verify(delegate).createView(context, "/test");
		assertFalse(view instanceof NavigationResponseUIViewRoot);
	}

	private DestinationAndModel setupDestination(String viewId, Object destination) {
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		given(destinationAndModel.getDestination()).willReturn(destination);
		given(destinationAndModelRegistry.get(context, viewId)).willReturn(destinationAndModel);
		return destinationAndModel;
	}

	@Test
	public void shouldCreateViewForMVCRender() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		ModelAndViewArtifact modelAndViewArtifact = mockModelAndViewArtifact();
		given(springFacesContext.getRendering()).willReturn(modelAndViewArtifact);
		given(delegate.createView(context, "mvc")).willReturn(viewRoot);
		UIViewRoot actual = handler.createView(context, "anything");
		assertSame(viewRoot, actual);
		verify(delegate).createView(context, "mvc");
		assertEquals("v", SpringFacesModelHolder.getModel(viewRoot).get("mk"));
	}

	@Test
	public void shouldDelegateRestoreView() throws Exception {
		handler.restoreView(context, viewId);
		verify(delegate).restoreView(context, viewId);
	}

	@Test
	public void shouldUseArtifactForRestoreViewOfMVCRender() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		ModelAndViewArtifact modelAndViewArtifact = mockModelAndViewArtifact();
		given(springFacesContext.getRendering()).willReturn(modelAndViewArtifact);
		handler.restoreView(context, "anything");
		verify(delegate).restoreView(context, "mvc");
	}

	@Test
	public void shouldDelegateGetActionURL() throws Exception {
		handler.getActionURL(context, viewId);
		verify(delegate).getActionURL(context, viewId);
	}

	@Test
	public void shouldUseSelfPostbackForActionURLFromMVCRender() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		ModelAndViewArtifact modelAndViewArtifact = mockModelAndViewArtifact();
		given(springFacesContext.getRendering()).willReturn(modelAndViewArtifact);
		given(delegate.createView(context, "mvc")).willReturn(viewRoot);
		handler.createView(context, "anything");
		String actionUrl = handler.getActionURL(context, "mvc");
		assertEquals("/rc/sp/si", actionUrl);
	}

	@Test
	public void shouldUseSelfPostbackForActionURLFromMVCRenderWithoutPartialState() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		ModelAndViewArtifact modelAndViewArtifact = mockModelAndViewArtifact();
		given(springFacesContext.getRendering()).willReturn(modelAndViewArtifact);
		given(delegate.createView(context, "mvc")).willReturn(viewRoot);
		handler.restoreView(context, "anything");
		String actionUrl = handler.getActionURL(context, "mvc");
		assertEquals("/rc/sp/si", actionUrl);
	}

	@Test
	public void shouldDelegateVDL() throws Exception {
		handler.getViewDeclarationLanguage(context, viewId);
		verify(delegate).getViewDeclarationLanguage(context, viewId);
	}

	@Test
	public void shouldReturnNullVdlForResolvedDestination() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		given(destinationAndModelRegistry.get(context, viewId)).willReturn(destinationAndModel);
		ViewDeclarationLanguage vdl = handler.getViewDeclarationLanguage(context, viewId);
		verify(delegate, never()).getViewDeclarationLanguage(eq(context), anyString());
		assertNull(vdl);
	}

	@Test
	public void shouldDelegateRenderView() throws Exception {
		UIViewRoot viewToRender = mockUIViewRootWithModelSupport();
		handler.renderView(context, viewToRender);
		verify(delegate).renderView(context, viewToRender);
	}

	@Test
	public void shouldRenderNavigationResponse() throws Exception {
		NavigationResponseUIViewRoot viewToRender = mock(NavigationResponseUIViewRoot.class);
		handler.renderView(context, viewToRender);
		verify(viewToRender).encodeAll(context);
		verify(delegate, never()).renderView(context, viewToRender);
	}

	@Test
	public void shouldDelegateGetBookmarkableUrl() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		handler.getBookmarkableURL(context, viewId, parameters, includeViewParams);
		verify(delegate).getBookmarkableURL(context, viewId, parameters, includeViewParams);
	}

	@Test
	public void shouldGetResolvableBookmark() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		BookmarkableView destination = mock(BookmarkableView.class);
		given(destinationAndModel.getDestination()).willReturn(destination);
		given(destination.getBookmarkUrl(anyModel(), any(HttpServletRequest.class))).willReturn("/bookmark");
		given(destinationAndModelRegistry.get(context, viewId)).willReturn(destinationAndModel);
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		String bookmark = handler.getBookmarkableURL(context, viewId, parameters, includeViewParams);
		assertEquals("/bookmark", bookmark);
	}

	@Test
	public void shouldRequireBookmarkInterfaceIfResolved() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		View destination = mock(View.class);
		given(destinationAndModel.getDestination()).willReturn(destination);
		given(destinationAndModelRegistry.get(context, viewId)).willReturn(destinationAndModel);
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("must be an instance of interface " + BookmarkableView.class.getName());
		handler.getBookmarkableURL(context, viewId, parameters, includeViewParams);
	}

	@Test
	public void shouldDelegateGetRedirectUrl() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		handler.getRedirectURL(context, viewId, parameters, includeViewParams);
		verify(delegate).getRedirectURL(context, viewId, parameters, includeViewParams);
	}

	@Test
	public void shouldRedirectUsingResolvedBookmarkUrl() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		BookmarkableView destination = mock(BookmarkableView.class);
		given(destinationAndModel.getDestination()).willReturn(destination);
		given(destination.getBookmarkUrl(anyModel(), any(HttpServletRequest.class))).willReturn("/bookmark");
		given(destinationAndModelRegistry.get(context, viewId)).willReturn(destinationAndModel);
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		boolean includeViewParams = false;
		String redirect = handler.getRedirectURL(context, viewId, parameters, includeViewParams);
		assertEquals("/bookmark", redirect);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldResolveDestination() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		Object destination = "resolvableDestination";
		DestinationAndModel destinationAndModel = setupDestination("test", destination);
		given(context.getCurrentPhaseId()).willReturn(PhaseId.INVOKE_APPLICATION);
		View view = mock(View.class);
		Map<String, Object> model = Collections.<String, Object> singletonMap("m", "v");
		ModelAndView modelAndView = new ModelAndView(view, model);
		given(
				destinationViewResolver.resolveDestination(eq(destination), any(Locale.class),
						any(SpringFacesModel.class))).willReturn(modelAndView);
		UIViewRoot createdView = handler.createView(context, "/test");
		assertSame(view, ((NavigationResponseUIViewRoot) createdView).getModelAndView().getView());
		verify(destinationAndModel).getModel(any(FacesContext.class), anyMap(), eq(model));
	}

	@Test
	public void shouldRenderViewOnNavigationResponseEncode() throws Exception {
		View view = mock(View.class);
		Map<String, Object> model = new HashMap<String, Object>();
		ModelAndView modelAndView = new ModelAndView(view, model);
		NavigationResponseUIViewRoot viewRoot = new NavigationResponseUIViewRoot(viewId, null, modelAndView);
		viewRoot.encodeAll(context);
		verify(view).render(model, request, response);
	}
}
