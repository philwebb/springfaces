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
package org.springframework.springfaces.mvc.config;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.springfaces.convert.SpringFacesConverterSupport;
import org.springframework.springfaces.exceptionhandler.ObjectMessageExceptionHandler;
import org.springframework.springfaces.exceptionhandler.SpringFacesExceptionHandlerSupport;
import org.springframework.springfaces.expression.el.FacesStandardEvaluationContextPostProcessor;
import org.springframework.springfaces.mvc.converter.GenericFacesConverter;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolverChain;
import org.springframework.springfaces.mvc.navigation.ImplicitNavigationOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolverChain;
import org.springframework.springfaces.mvc.navigation.annotation.NavigationMethodOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.requestmapped.RequestMappedRedirectDestinationViewResolver;
import org.springframework.springfaces.mvc.render.ClientFacesViewStateHandler;
import org.springframework.springfaces.mvc.servlet.DefaultDestinationViewResolver;
import org.springframework.springfaces.mvc.servlet.DefaultDispatcher;
import org.springframework.springfaces.mvc.servlet.DispatcherAwareBeanPostProcessor;
import org.springframework.springfaces.mvc.servlet.FacesHandlerInterceptor;
import org.springframework.springfaces.mvc.servlet.FacesPostbackHandler;
import org.springframework.springfaces.mvc.servlet.FacesResourceRequestHandler;
import org.springframework.springfaces.mvc.servlet.MvcExceptionHandler;
import org.springframework.springfaces.mvc.servlet.SpringFacesFactories;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableRedirectViewIdResolver;
import org.springframework.springfaces.validator.SpringFacesValidatorSupport;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

/**
 * Tests for {@link SpringFacesMvcNamespaceHandler}.
 * 
 * @author Phillip Webb
 */
public class SpringFacesMvcNamespaceHandlerTest {

	private StaticWebApplicationContext applicationContext;

	@Before
	public void setup() {
		this.applicationContext = loadApplicationContext(new ClassPathResource("testSpringFacesMvcNamespace.xml",
				getClass()));
	}

	@Test
	public void shouldSetupIntegration() throws Exception {
		assertHasBean(SpringFacesIntegration.class);
		assertHasBean(SpringFacesValidatorSupport.class);
		assertHasBean(SpringFacesConverterSupport.class);
		assertHasBean(SpringFacesExceptionHandlerSupport.class);
		assertHasBean(ObjectMessageExceptionHandler.class);
		assertHasBean(FacesStandardEvaluationContextPostProcessor.class);
	}

	@Test
	public void shouldSetupConversionService() {
		assertHasBean(GenericConversionService.class);
		assertHasBean(GenericFacesConverter.class);
	}

	@Test
	public void shouldSetupMvcSupport() throws Exception {
		assertHasBean(DefaultDispatcher.class);
		assertHasBean(DispatcherAwareBeanPostProcessor.class);
		assertHasBean(ClientFacesViewStateHandler.class);
		assertHasBean(FacesPostbackHandler.class);
		assertHasBean(MvcExceptionHandler.class);
		List<DestinationViewResolver> viewResolvers = this.applicationContext.getBean(
				DestinationViewResolverChain.class).getResolvers();
		assertThat(viewResolvers.get(0), is(RequestMappedRedirectDestinationViewResolver.class));
		assertThat(viewResolvers.get(1), is(DefaultDestinationViewResolver.class));
		List<NavigationOutcomeResolver> navigationResolvers = this.applicationContext.getBean(
				NavigationOutcomeResolverChain.class).getResolvers();
		assertThat(navigationResolvers.get(0), is(ImplicitNavigationOutcomeResolver.class));
		assertThat(navigationResolvers.get(1), is(NavigationMethodOutcomeResolver.class));
		assertHasBean(SpringFacesFactories.class);
		MappedInterceptor mappedInterceptor = getMappedInterceptor(FacesHandlerInterceptor.class);
		assertThat(mappedInterceptor.getPathPatterns(), is(nullValue()));
		assertThat(mappedInterceptor.getInterceptor(), is(FacesHandlerInterceptor.class));
	}

	@Test
	public void shouldSetupMvcViewResolver() throws Exception {
		assertHasBean(BookmarkableRedirectViewIdResolver.class);
	}

	@Test
	public void shouldSetupResources() throws Exception {
		assertHasBean(HttpRequestHandlerAdapter.class);
		Map<String, FacesResourceRequestHandler> handler = this.applicationContext
				.getBeansOfType(FacesResourceRequestHandler.class);
		String handlerName = handler.keySet().iterator().next();
		SimpleUrlHandlerMapping urlHandlerMapping = this.applicationContext.getBean(SimpleUrlHandlerMapping.class);
		Map<String, ?> urlMap = urlHandlerMapping.getUrlMap();
		assertThat(urlMap.size(), is(1));
		assertThat(urlMap.get("/javax.faces.resource/**"), is((Object) handlerName));
	}

	private MappedInterceptor getMappedInterceptor(Class<?> interceptorClass) {
		for (MappedInterceptor interceptor : this.applicationContext.getBeansOfType(MappedInterceptor.class).values()) {
			if (interceptorClass.isInstance(interceptor.getInterceptor())) {
				return interceptor;
			}
		}
		return null;
	}

	private void assertHasBean(Class<?> beanClass) {
		assertThat(this.applicationContext.getBeansOfType(beanClass).size(), is(1));
	}

	public static StaticWebApplicationContext loadApplicationContext(Resource resource) {
		StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
		applicationContext.setServletContext(new MockServletContext());
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(applicationContext.getDefaultListableBeanFactory());
		reader.loadBeanDefinitions(resource);
		applicationContext.refresh();
		return applicationContext;
	}

}
