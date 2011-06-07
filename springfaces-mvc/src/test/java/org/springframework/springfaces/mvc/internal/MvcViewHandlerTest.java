package org.springframework.springfaces.mvc.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

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
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.render.ModelAndViewArtifact;
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

	private MvcViewHandler handler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		FacesContextSetter.setCurrentInstance(context);
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		given(context.getAttributes()).willReturn(attributes);
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
		String viewId = "viewId";
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

	private void setupDestination(String viewId, Object destination) {
		DestinationAndModel destinationAndModel = mock(DestinationAndModel.class);
		given(destinationAndModel.getDestination()).willReturn(destination);
		given(destinationAndModelRegistry.get(context, viewId)).willReturn(destinationAndModel);
	}

	@Test
	public void shouldCreateViewForMVCRender() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		UIViewRoot viewRoot = mockUIViewRoot();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("mk", "v");
		ModelAndViewArtifact modelAndViewArtifact = new ModelAndViewArtifact("mvc", model);
		given(springFacesContext.getRendering()).willReturn(modelAndViewArtifact);
		given(delegate.createView(context, "mvc")).willReturn(viewRoot);
		UIViewRoot actual = handler.createView(context, "anything");
		assertSame(viewRoot, actual);
		verify(delegate).createView(context, "mvc");
		assertEquals("v", SpringFacesModel.loadFromViewScope(viewRoot).get("mk"));
	}

	private UIViewRoot mockUIViewRoot() {
		UIViewRoot uiViewRoot = mock(UIViewRoot.class);
		Map<String, Object> viewMap = new HashMap<String, Object>();
		given(uiViewRoot.getViewMap()).willReturn(viewMap);
		return uiViewRoot;
	}

	@Test
	public void shouldDelegateRestoreView() throws Exception {
		// FIXME
	}

	@Test
	public void shouldUseArtifactForRestoreViewOfMVCRender() throws Exception {
		// FIXME
	}

	@Test
	public void shouldDelegateGetActionURL() throws Exception {
		// FIXME
	}

	@Test
	public void shouldUseActionURLFromMVCRender() throws Exception {
		// FIXME
	}

	@Test
	public void shouldDelegateVDL() throws Exception {
		// FIXME
	}

	@Test
	public void shouldReturnNullVdlForResolvedDestination() throws Exception {
		// FIXME
	}

	@Test
	public void shouldDelegateRenderView() throws Exception {
		// FIXME
	}

	@Test
	public void shouldRenderNavigationResponse() throws Exception {
		// FIXME
	}

	@Test
	public void shouldDelegateGetBookmarkableUrl() throws Exception {
		// FIXME
	}

	@Test
	public void shouldGetResolvableBookmark() throws Exception {
		// FIXME
	}

	@Test
	public void shouldDelegateGetRedirectUrl() throws Exception {
		// FIXME
	}

	@Test
	public void shouldRedirectUsingResolvedBookmarkUrl() throws Exception {
		// FIXME
	}
}
