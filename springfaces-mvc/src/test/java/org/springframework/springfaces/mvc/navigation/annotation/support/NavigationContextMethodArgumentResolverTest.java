package org.springframework.springfaces.mvc.navigation.annotation.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockMethodParameter;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Tests for {@link NavigationContextMethodArgumentResolver}.
 * 
 * @author Phillip Webb
 */
public class NavigationContextMethodArgumentResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private NavigationContextMethodArgumentResolver resolver;

	@Mock
	private NavigationContext navigationContext;

	@Mock
	private ModelAndViewContainer mavContainer;

	@Mock
	private NativeWebRequest webRequest;

	@Mock
	private WebDataBinderFactory binderFactory;

	@Mock
	private HtmlCommandButton component;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		resolver = new NavigationContextMethodArgumentResolver(navigationContext);
		given(navigationContext.getComponent()).willReturn(component);
		given(navigationContext.getOutcome()).willReturn("outcome");
	}

	@Test
	public void shouldNeedNavigationContext() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("NavigationContext must not be null");
		new NavigationContextMethodArgumentResolver(null);
	}

	@Test
	public void shouldSupportNavigationContext() throws Exception {
		assertTrue(resolver.supportsParameter(mockMethodParameter(NavigationContext.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(ExtendsNavigationContext.class)));
		assertSame(navigationContext, resolver.resolveArgument(mockMethodParameter(NavigationContext.class),
				mavContainer, webRequest, binderFactory));
	}

	@Test
	public void shouldSupportComponent() throws Exception {
		assertTrue(resolver.supportsParameter(mockMethodParameter(HtmlCommandButton.class)));
		assertTrue(resolver.supportsParameter(mockMethodParameter(UICommand.class)));
		assertTrue(resolver.supportsParameter(mockMethodParameter(UIComponent.class)));
		assertFalse(resolver.supportsParameter(mockMethodParameter(HtmlCommandLink.class)));
		assertSame(component,
				resolver.resolveArgument(mockMethodParameter(UICommand.class), mavContainer, webRequest, binderFactory));
	}

	@Test
	public void shouldSupportOutcome() throws Exception {
		assertTrue(resolver.supportsParameter(mockMethodParameter(String.class)));
		assertEquals("outcome",
				resolver.resolveArgument(mockMethodParameter(String.class), mavContainer, webRequest, binderFactory));
	}

	private static interface ExtendsNavigationContext extends NavigationContext {
	}
}
