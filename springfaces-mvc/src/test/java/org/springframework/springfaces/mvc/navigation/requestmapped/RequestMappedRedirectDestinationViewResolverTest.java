package org.springframework.springfaces.mvc.navigation.requestmapped;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import java.lang.reflect.Method;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.View;

/**
 * Tests for {@link RequestMappedRedirectDestinationViewResolver}.
 * 
 * @author Phillip Webb
 */
public class RequestMappedRedirectDestinationViewResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@InjectMocks
	private RequestMappedRedirectDestinationViewResolver resolver = new RequestMappedRedirectDestinationViewResolver() {
		protected View createView(RequestMappedRedirectViewContext context, Object handler, Method handlerMethod) {
			createdViewContext = context;
			createdViewHandler = handler;
			createdViewHandlerMethod = handlerMethod;
			return null;
		};
	};

	@Mock
	private SpringFacesContext springFacesContext;

	@Mock
	private ApplicationContext applicationContext;

	private ControllerBean controllerBean = new ControllerBean();

	protected RequestMappedRedirectViewContext createdViewContext;

	protected Object createdViewHandler;

	protected Method createdViewHandlerMethod;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		given(springFacesContext.getHandler()).willReturn(controllerBean);
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		given(applicationContext.getBean("bean")).willReturn(controllerBean);
		given(applicationContext.getBean("exotic@be.an")).willReturn(controllerBean);
		given(applicationContext.getBean("missing")).willThrow(new NoSuchBeanDefinitionException("missing"));
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldOnlyResolveStrings() throws Exception {
		assertNull(resolver.resolveDestination(new Object(), Locale.UK));
		assertNull(resolver.resolveDestination(new Integer(4), Locale.US));
	}

	@Test
	public void shouldNotResolveIfNotPrefixedString() throws Exception {
		assertNull(resolver.resolveDestination("bean.method", Locale.UK));
	}

	@Test
	public void shouldResolveAgainstCurrentHandler() throws Exception {
		resolver.resolveDestination("@method", Locale.UK);
		assertEquals(controllerBean, createdViewHandler);
		assertTrue(createdViewHandlerMethod.getName().equals("method"));
	}

	@Test
	public void shouldFailIfCurrentHandlerIsNull() throws Exception {
		reset(springFacesContext);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@method' : "
				+ "Unable to locate SpringFaces MVC handler");
		resolver.resolveDestination("@method", Locale.UK);
	}

	@Test
	public void shouldResolveAgainstSpecificBean() throws Exception {
		resolver.resolveDestination("@bean.method", Locale.UK);
		assertEquals(controllerBean, createdViewHandler);
		assertTrue(createdViewHandlerMethod.getName().equals("method"));
	}

	@Test
	public void shouldSupportCustomPrefix() throws Exception {
		resolver.setPrefix("resove:");
		resolver.resolveDestination("resove:bean.method", Locale.UK);
		assertEquals(controllerBean, createdViewHandler);
		assertTrue(createdViewHandlerMethod.getName().equals("method"));
	}

	@Test
	public void shouldResolveWithExoticBeanNames() throws Exception {
		resolver.resolveDestination("@exotic@be.an.method", Locale.UK);
		assertEquals(controllerBean, createdViewHandler);
		assertTrue(createdViewHandlerMethod.getName().equals("method"));
	}

	@Test
	public void shouldFailIfMethodIsNotRequestMapped() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@notMapped' : "
				+ "Unable to find @RequestMapping annotated method 'notMapped'");
		resolver.resolveDestination("@notMapped", Locale.UK);
	}

	@Test
	public void shouldFailIfMethodIsMissing() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@doesNotExist' : "
				+ "Unable to find @RequestMapping annotated method 'doesNotExist'");
		resolver.resolveDestination("@doesNotExist", Locale.UK);
	}

	@Test
	public void shouldFailWithOverloadedMethods() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@overloaded' : "
				+ "More than one @RequestMapping annotated method with the name 'overloaded' exists");
		resolver.resolveDestination("@overloaded", Locale.UK);
	}

	@Test
	public void shouldFailIfBeanIsMissing() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@missing.method' : "
				+ "No bean named 'missing' is defined");
		resolver.resolveDestination("@missing.method", Locale.UK);
	}

	@Test
	public void shouldSupportContextSetters() throws Exception {
		WebArgumentResolver webArgumentResolver = mock(WebArgumentResolver.class);
		WebArgumentResolver[] customArgumentResolvers = new WebArgumentResolver[] { webArgumentResolver };
		PathMatcher pathMatcher = mock(PathMatcher.class);
		WebBindingInitializer webBindingInitializer = mock(WebBindingInitializer.class);
		ParameterNameDiscoverer parameterNameDiscoverer = mock(ParameterNameDiscoverer.class);
		resolver.setCustomArgumentResolvers(customArgumentResolvers);
		resolver.setPathMatcher(pathMatcher);
		resolver.setWebBindingInitializer(webBindingInitializer);
		resolver.setParameterNameDiscoverer(parameterNameDiscoverer);
		resolver.setDispatcherServletPath("/cdp");
		resolver.resolveDestination("@method", Locale.UK);
		assertSame(customArgumentResolvers, createdViewContext.getCustomArgumentResolvers());
		assertSame(pathMatcher, createdViewContext.getPathMatcher());
		assertSame(webBindingInitializer, createdViewContext.getWebBindingInitializer());
		assertSame(parameterNameDiscoverer, createdViewContext.getParameterNameDiscoverer());
		assertEquals("/cdp", createdViewContext.getDispatcherServletPath());
	}

	@Controller
	public static class ControllerBean {

		// Standard mapping
		@RequestMapping("/mapped")
		public void method() {
		}

		// Overloaded version, but not @RequestMapping
		public void method(String s) {
		}

		// No @RequestMapping
		public void notMapped() {
		}

		@RequestMapping("/overload1")
		public void overloaded() {
		}

		@RequestMapping("/overload2")
		public void overloaded(String s) {
		}
	}
}
