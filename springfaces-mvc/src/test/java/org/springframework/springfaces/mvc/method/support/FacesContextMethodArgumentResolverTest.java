package org.springframework.springfaces.mvc.method.support;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockMethodParameter;

import java.util.Locale;

import javax.faces.application.Application;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.springfaces.mvc.FacesContextSetter;

/**
 * Tests for {@link FacesContextMethodArgumentResolver}.
 * 
 * @author Phillip Webb
 */
public class FacesContextMethodArgumentResolverTest {

	@Mock
	private FacesContext facesContext;

	@Mock
	private ExternalContext externalContext;

	@Mock
	private PartialViewContext partialViewContext;

	@Mock
	private Application application;

	@Mock
	private ResourceHandler resourceHandler;

	@Mock
	private ExceptionHandler exceptionHandler;

	@Mock
	private UIViewRoot viewRoot;

	private Locale locale = Locale.UK;

	private FacesContextMethodArgumentResolver resolver = new FacesContextMethodArgumentResolver();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		setupMocks(true);
		FacesContextSetter.setCurrentInstance(facesContext);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldNotSupportWhenNoFacesContext() throws Exception {
		FacesContextSetter.setCurrentInstance(null);
		assertFalse(resolver.supportsParameter(mockMethodParameter(FacesContext.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(ExternalContext.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(PartialViewContext.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(Application.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(ResourceHandler.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(ExceptionHandler.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(UIViewRoot.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(Locale.class)));
	}

	@Test
	public void shouldResolveFacesContext() throws Exception {
		MethodParameter parameter = mockMethodParameter(FacesContext.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(facesContext, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldResolveExternalContext() throws Exception {
		MethodParameter parameter = mockMethodParameter(ExternalContext.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(externalContext, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldResolvePartialViewContext() throws Exception {
		MethodParameter parameter = mockMethodParameter(PartialViewContext.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(partialViewContext, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldResolveApplication() throws Exception {
		MethodParameter parameter = mockMethodParameter(Application.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(application, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldResolveResourceHandler() throws Exception {
		MethodParameter parameter = mockMethodParameter(ResourceHandler.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(resourceHandler, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldResolveExceptionHandler() throws Exception {
		MethodParameter parameter = mockMethodParameter(ExceptionHandler.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(exceptionHandler, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldResolveUIViewRoot() throws Exception {
		MethodParameter parameter = mockMethodParameter(UIViewRoot.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(viewRoot, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldResolveLocale() throws Exception {
		MethodParameter parameter = mockMethodParameter(Locale.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(locale, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldNotSupportWhenNoViewRoot() throws Exception {
		setupMocks(false);
		assertFalse(resolver.supportsParameter(mockMethodParameter(UIViewRoot.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(Locale.class)));
	}

	private void setupMocks(boolean hasViewRoot) {
		reset(facesContext, application, viewRoot);
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(facesContext.getPartialViewContext()).willReturn(partialViewContext);
		given(facesContext.getApplication()).willReturn(application);
		given(facesContext.getExceptionHandler()).willReturn(exceptionHandler);
		if (hasViewRoot) {
			given(facesContext.getViewRoot()).willReturn(viewRoot);
		}
		given(application.getResourceHandler()).willReturn(resourceHandler);
		given(viewRoot.getLocale()).willReturn(locale);
	}

}
