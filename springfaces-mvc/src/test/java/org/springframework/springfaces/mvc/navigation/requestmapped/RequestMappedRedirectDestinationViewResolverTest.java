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
package org.springframework.springfaces.mvc.navigation.requestmapped;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Locale;

import javax.faces.context.FacesContext;

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
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.stereotype.Controller;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.ModelAndView;
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
		@Override
		protected View createView(RequestMappedRedirectViewContext context, Object handler, Method handlerMethod) {
			RequestMappedRedirectDestinationViewResolverTest.this.createdViewContext = context;
			RequestMappedRedirectDestinationViewResolverTest.this.createdViewHandler = handler;
			RequestMappedRedirectDestinationViewResolverTest.this.createdViewHandlerMethod = handlerMethod;
			return RequestMappedRedirectDestinationViewResolverTest.this.resolvedView;
		};
	};

	@Mock
	private FacesContext facesContext;

	@Mock
	private SpringFacesContext springFacesContext;

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private View resolvedView;

	private ControllerBean controllerBean = new ControllerBean();

	protected RequestMappedRedirectViewContext createdViewContext;

	protected Object createdViewHandler;

	protected Method createdViewHandlerMethod;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		given(this.springFacesContext.getController()).willReturn(this.controllerBean);
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		given(this.applicationContext.getBean("bean")).willReturn(this.controllerBean);
		given(this.applicationContext.getBean("exotic@be.an")).willReturn(this.controllerBean);
		given(this.applicationContext.getBean("missing")).willThrow(new NoSuchBeanDefinitionException("missing"));
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldOnlyResolveStrings() throws Exception {
		assertThat(this.resolver.resolveDestination(this.facesContext, new Object(), Locale.UK, null), is(nullValue()));
		assertThat(this.resolver.resolveDestination(this.facesContext, new Integer(4), Locale.US, null),
				is(nullValue()));
	}

	@Test
	public void shouldNotResolveIfNotPrefixedString() throws Exception {
		assertThat(this.resolver.resolveDestination(this.facesContext, "bean.method", Locale.UK, null), is(nullValue()));
	}

	@Test
	public void shouldResolveAgainstCurrentHandler() throws Exception {
		this.resolver.resolveDestination(this.facesContext, "@method", Locale.UK, null);
		assertThat(this.createdViewHandler, is(equalTo((Object) this.controllerBean)));
		assertThat(this.createdViewHandlerMethod.getName(), is(equalTo("method")));
	}

	@Test
	public void shouldFailIfCurrentHandlerIsNull() throws Exception {
		reset(this.springFacesContext);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@method' : "
				+ "Unable to locate SpringFaces MVC Controller");
		this.resolver.resolveDestination(this.facesContext, "@method", Locale.UK, null);
	}

	@Test
	public void shouldResolveAgainstSpecificBean() throws Exception {
		this.resolver.resolveDestination(this.facesContext, "@bean.method", Locale.UK, null);
		assertThat(this.createdViewHandler, is(equalTo((Object) this.controllerBean)));
		assertThat(this.createdViewHandlerMethod.getName(), is(equalTo("method")));
	}

	@Test
	public void shouldSupportCustomPrefix() throws Exception {
		this.resolver.setPrefix("resove:");
		this.resolver.resolveDestination(this.facesContext, "resove:bean.method", Locale.UK, null);
		assertThat(this.createdViewHandler, is(equalTo((Object) this.controllerBean)));
		assertThat(this.createdViewHandlerMethod.getName(), is(equalTo("method")));
	}

	@Test
	public void shouldResolveWithExoticBeanNames() throws Exception {
		this.resolver.resolveDestination(this.facesContext, "@exotic@be.an.method", Locale.UK, null);
		assertThat(this.createdViewHandler, is(equalTo((Object) this.controllerBean)));
		assertThat(this.createdViewHandlerMethod.getName(), is(equalTo("method")));
	}

	@Test
	public void shouldFailIfMethodIsNotRequestMapped() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@notMapped' : "
				+ "Unable to find @RequestMapping annotated method 'notMapped'");
		this.resolver.resolveDestination(this.facesContext, "@notMapped", Locale.UK, null);
	}

	@Test
	public void shouldFailIfMethodIsMissing() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@doesNotExist' : "
				+ "Unable to find @RequestMapping annotated method 'doesNotExist'");
		this.resolver.resolveDestination(this.facesContext, "@doesNotExist", Locale.UK, null);
	}

	@Test
	public void shouldFailWithOverloadedMethods() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@overloaded' : "
				+ "More than one @RequestMapping annotated method with the name 'overloaded' exists");
		this.resolver.resolveDestination(this.facesContext, "@overloaded", Locale.UK, null);
	}

	@Test
	public void shouldFailIfBeanIsMissing() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to resolve @RequestMapped view from destination '@missing.method' : "
				+ "No bean named 'missing' is defined");
		this.resolver.resolveDestination(this.facesContext, "@missing.method", Locale.UK, null);
	}

	@Test
	public void shouldSupportContextSetters() throws Exception {
		WebArgumentResolver webArgumentResolver = mock(WebArgumentResolver.class);
		WebArgumentResolver[] customArgumentResolvers = new WebArgumentResolver[] { webArgumentResolver };
		PathMatcher pathMatcher = mock(PathMatcher.class);
		WebBindingInitializer webBindingInitializer = mock(WebBindingInitializer.class);
		ParameterNameDiscoverer parameterNameDiscoverer = mock(ParameterNameDiscoverer.class);
		this.resolver.setCustomArgumentResolvers(customArgumentResolvers);
		this.resolver.setPathMatcher(pathMatcher);
		this.resolver.setWebBindingInitializer(webBindingInitializer);
		this.resolver.setParameterNameDiscoverer(parameterNameDiscoverer);
		this.resolver.setDispatcherServletPath("/cdp");
		this.resolver.resolveDestination(this.facesContext, "@method", Locale.UK, null);
		assertSame(customArgumentResolvers, this.createdViewContext.getCustomArgumentResolvers());
		assertSame(pathMatcher, this.createdViewContext.getPathMatcher());
		assertSame(webBindingInitializer, this.createdViewContext.getWebBindingInitializer());
		assertSame(parameterNameDiscoverer, this.createdViewContext.getParameterNameDiscoverer());
		assertThat(this.createdViewContext.getDispatcherServletPath(), is(equalTo("/cdp")));
	}

	@Test
	public void shouldPropagateMap() throws Exception {
		SpringFacesModel model = new SpringFacesModel(Collections.singletonMap("k", "v"));
		ModelAndView resolved = this.resolver.resolveDestination(this.facesContext, "@method", Locale.UK, model);
		assertSame(this.resolvedView, resolved.getView());
		assertThat(resolved.getModel().get("k"), is(equalTo((Object) "v")));
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
