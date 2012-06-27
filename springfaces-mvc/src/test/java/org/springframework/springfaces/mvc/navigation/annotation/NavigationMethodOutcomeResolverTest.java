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
package org.springframework.springfaces.mvc.navigation.annotation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter;
import org.springframework.springfaces.mvc.method.support.FacesContextMethodArgumentResolver;
import org.springframework.springfaces.mvc.method.support.FacesResponseCompleteReturnValueHandler;
import org.springframework.springfaces.mvc.method.support.SpringFacesModelMethodArgumentResolver;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.annotation.support.NavigationContextMethodArgumentResolver;
import org.springframework.springfaces.mvc.navigation.annotation.support.NavigationMethodReturnValueHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver;

/**
 * Tests for {@link NavigationMethodOutcomeResolver}.
 * @author Phillip Webb
 */
public class NavigationMethodOutcomeResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private NavigationMethodOutcomeResolver resolver;

	protected ServletInvocableHandlerMethod invocableNavigationMethod;

	protected InvocableHandlerMethod invocableBinderMethod;

	private NavigationControllerBean bean = new NavigationControllerBean();

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private ConfigurableBeanFactory beanFactory;

	@Mock
	private NavigationContext context;

	@Mock
	private FacesContext facesContext;

	@Mock
	private ExternalContext externalContext;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Captor
	private ArgumentCaptor<HandlerMethodArgumentResolverComposite> argumentResolvers;

	@Captor
	private ArgumentCaptor<HandlerMethodReturnValueHandlerComposite> returnValueHandlers;

	@Captor
	private ArgumentCaptor<WebDataBinderFactory> dataBinderFactory;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.resolver = new NavigationMethodOutcomeResolver() {
			@Override
			protected ServletInvocableHandlerMethod createInvocableNavigationMethod(Object handler, Method method) {
				NavigationMethodOutcomeResolverTest.this.invocableNavigationMethod = spy(super
						.createInvocableNavigationMethod(handler, method));
				return NavigationMethodOutcomeResolverTest.this.invocableNavigationMethod;
			}

			@Override
			protected InvocableHandlerMethod createInvocableBinderMethod(Object handler, Method method) {
				NavigationMethodOutcomeResolverTest.this.invocableBinderMethod = spy(super.createInvocableBinderMethod(
						handler, method));
				return NavigationMethodOutcomeResolverTest.this.invocableBinderMethod;
			};
		};
		setApplicationContextBean(this.bean, true);
		this.resolver.setBeanFactory(this.beanFactory);
		given(this.context.getOutcome()).willReturn("navigate");
		given(this.facesContext.getExternalContext()).willReturn(this.externalContext);
		given(this.externalContext.getRequest()).willReturn(this.request);
		given(this.externalContext.getResponse()).willReturn(this.response);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setApplicationContextBean(Object bean, boolean includeRequestMappingHandlerAdapter) {
		reset(this.applicationContext);
		given(this.applicationContext.getBeanNamesForType(Object.class)).willReturn(new String[] { "bean" });
		given(this.applicationContext.getType("bean")).willReturn((Class) bean.getClass());
		given(this.applicationContext.getBean("bean")).willReturn(bean);
		if (includeRequestMappingHandlerAdapter) {
			RequestMappingHandlerAdapter requestHandlerAdapter = createMockRequestHandlerAdapter();
			given(this.applicationContext.getBean(RequestMappingHandlerAdapter.class))
					.willReturn(requestHandlerAdapter);
		}
	}

	private RequestMappingHandlerAdapter createMockRequestHandlerAdapter() {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false);
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringHttpMessageConverter);
		messageConverters.add(new SourceHttpMessageConverter<Source>());
		messageConverters.add(new XmlAwareFormHttpMessageConverter());
		RequestMappingHandlerAdapter adapter = mock(RequestMappingHandlerAdapter.class);
		given(adapter.getMessageConverters()).willReturn(messageConverters);
		return adapter;
	}

	@Test
	public void shouldCreateDefaultMessageConverters() throws Exception {
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.afterPropertiesSet();
		List<HttpMessageConverter<?>> expected = new RequestMappingHandlerAdapter().getMessageConverters();
		List<HttpMessageConverter<?>> actual = this.resolver.getMessageConverters();
		assertClasses(expected, actual);
	}

	@Test
	public void shouldSupportCustomArgumentResolvers() throws Exception {
		HandlerMethodArgumentResolver customArgumentResolver = mock(HandlerMethodArgumentResolver.class);
		List<HandlerMethodArgumentResolver> customArgumentResolvers = Arrays.asList(customArgumentResolver);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.setCustomArgumentResolvers(customArgumentResolvers);
		this.resolver.afterPropertiesSet();
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableNavigationMethod).setHandlerMethodArgumentResolvers(this.argumentResolvers.capture());
		List<HandlerMethodArgumentResolver> actual = fieldValueAsList(this.argumentResolvers.getValue(),
				"argumentResolvers");
		assertThat(actual.size(), is(greaterThan(1)));
		assertThat(actual, hasItem(customArgumentResolver));
	}

	@Test
	public void shouldSupportArgumentResolvers() throws Exception {
		HandlerMethodArgumentResolver customArgumentResolver = mock(HandlerMethodArgumentResolver.class);
		List<HandlerMethodArgumentResolver> customArgumentResolvers = Arrays.asList(customArgumentResolver);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.setArgumentResolvers(customArgumentResolvers);
		this.resolver.afterPropertiesSet();
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableNavigationMethod).setHandlerMethodArgumentResolvers(this.argumentResolvers.capture());
		List<HandlerMethodArgumentResolver> actual = fieldValueAsList(this.argumentResolvers.getValue(),
				"argumentResolvers");
		assertThat(actual.size(), is(2));
		assertThat(actual, hasItem(customArgumentResolver));
	}

	@Test
	public void shouldSupportInitBinderArgumentResolvers() throws Exception {
		HandlerMethodArgumentResolver customArgumentResolver = mock(HandlerMethodArgumentResolver.class);
		List<HandlerMethodArgumentResolver> customArgumentResolvers = Arrays.asList(customArgumentResolver);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.setInitBinderArgumentResolvers(customArgumentResolvers);
		this.resolver.afterPropertiesSet();
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableBinderMethod).setHandlerMethodArgumentResolvers(this.argumentResolvers.capture());
		List<HandlerMethodArgumentResolver> actual = fieldValueAsList(this.argumentResolvers.getValue(),
				"argumentResolvers");
		assertThat(actual.size(), is(1));
		assertThat(actual, hasItem(customArgumentResolver));
	}

	@Test
	public void shouldSupportCustomReturnValueHandlers() throws Exception {
		HandlerMethodReturnValueHandler customReturnValueHandler = mock(HandlerMethodReturnValueHandler.class);
		List<HandlerMethodReturnValueHandler> customReturnValueHandlers = Arrays.asList(customReturnValueHandler);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.setCustomReturnValueHandlers(customReturnValueHandlers);
		this.resolver.afterPropertiesSet();
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableNavigationMethod).setHandlerMethodReturnValueHandlers(this.returnValueHandlers.capture());
		List<HandlerMethodReturnValueHandler> actual = fieldValueAsList(this.returnValueHandlers.getValue(),
				"returnValueHandlers");
		assertThat(actual.size(), is(greaterThan(1)));
		assertThat(actual, hasItem(customReturnValueHandler));
	}

	@Test
	public void shouldSupportReturnValueHandlers() throws Exception {
		HandlerMethodReturnValueHandler customReturnValueHandler = mock(HandlerMethodReturnValueHandler.class);
		given(customReturnValueHandler.supportsReturnType(any(MethodParameter.class))).willReturn(true);
		List<HandlerMethodReturnValueHandler> customReturnValueHandlers = Arrays.asList(customReturnValueHandler);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.setReturnValueHandlers(customReturnValueHandlers);
		this.resolver.afterPropertiesSet();
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableNavigationMethod).setHandlerMethodReturnValueHandlers(this.returnValueHandlers.capture());
		List<HandlerMethodReturnValueHandler> actual = fieldValueAsList(this.returnValueHandlers.getValue(),
				"returnValueHandlers");
		assertThat(actual.size(), is(1));
		assertThat(actual, hasItem(customReturnValueHandler));
	}

	@Test
	public void shouldSupportMessageConverters() throws Exception {
		StringHttpMessageConverter customMessageConverter = new StringHttpMessageConverter();
		List<HttpMessageConverter<?>> messageConverters = Arrays
				.<HttpMessageConverter<?>> asList(customMessageConverter);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.setMessageConverters(messageConverters);
		this.resolver.afterPropertiesSet();
		assertThat(this.resolver.getMessageConverters(), is(equalTo(messageConverters)));
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableNavigationMethod).setHandlerMethodReturnValueHandlers(this.returnValueHandlers.capture());
		List<HandlerMethodReturnValueHandler> returnHandlers = fieldValueAsList(this.returnValueHandlers.getValue(),
				"returnValueHandlers");
		int i = 0;
		for (HandlerMethodReturnValueHandler handler : returnHandlers) {
			handler = (HandlerMethodReturnValueHandler) unwrapFacesResponseCompleteReturnValueHandler(handler);
			if (handler instanceof AbstractMessageConverterMethodProcessor) {
				assertThat(new DirectFieldAccessor(handler).getPropertyValue("messageConverters"),
						is(equalTo((Object) messageConverters)));
				i++;
			}
		}
		assertThat(i, is(2));
	}

	@Test
	public void shouldSupportWebBindingInitializer() throws Exception {
		WebBindingInitializer webBindingInitializer = mock(WebBindingInitializer.class);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.setWebBindingInitializer(webBindingInitializer);
		this.resolver.afterPropertiesSet();
		assertThat(this.resolver.getWebBindingInitializer(), is(sameInstance(webBindingInitializer)));
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableNavigationMethod).setDataBinderFactory(this.dataBinderFactory.capture());
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		String objectName = "name";
		Object target = new Object();
		this.dataBinderFactory.getValue().createBinder(webRequest, target, objectName);
		verify(webBindingInitializer).initBinder(this.bean.binder, webRequest);
	}

	@Test
	public void shouldSupportParameterNameDiscoverer() throws Exception {
		ParameterNameDiscoverer parameterNameDiscoverer = mock(ParameterNameDiscoverer.class);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.setParameterNameDiscoverer(parameterNameDiscoverer);
		this.resolver.afterPropertiesSet();
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableNavigationMethod).setParameterNameDiscoverer(parameterNameDiscoverer);
		verify(this.invocableBinderMethod).setParameterNameDiscoverer(parameterNameDiscoverer);
	}

	@Test
	public void shouldDetectNavigationMethodsOnController() throws Exception {
		doTestDetectNavigationMethods(new ControllerBean(), 1, true);
	}

	@Test
	public void shouldDetectNavigationMethodsOnNavigationController() throws Exception {
		doTestDetectNavigationMethods(new NavigationControllerBean(), 1, false);
	}

	@Test
	public void shouldNotDetectNavigationMethodsOnComponent() throws Exception {
		doTestDetectNavigationMethods(new ComponentBean(), 0, false);
	}

	private void doTestDetectNavigationMethods(Object bean, int expectedSize, boolean expectController)
			throws Exception {
		setApplicationContextBean(bean, true);
		given(this.context.getController()).willReturn(bean);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.afterPropertiesSet();
		Set<NavigationMappingMethod> mappings = fieldValueAsSet(this.resolver, "navigationMethods");
		assertThat(mappings.size(), is(expectedSize));
		for (NavigationMappingMethod mapping : mappings) {
			assertThat(mapping.isControllerBeanMethod(), is(equalTo(expectController)));
		}
	}

	@Test
	public void shouldInitDefaultArgumentResolvers() throws Exception {
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.afterPropertiesSet();
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableNavigationMethod).setHandlerMethodArgumentResolvers(this.argumentResolvers.capture());
		List<HandlerMethodArgumentResolver> actual = fieldValueAsList(this.argumentResolvers.getValue(),
				"argumentResolvers");
		List<Class<?>> expected = Arrays.<Class<?>> asList(RequestHeaderMethodArgumentResolver.class,
				RequestHeaderMapMethodArgumentResolver.class, ServletCookieValueMethodArgumentResolver.class,
				ExpressionValueMethodArgumentResolver.class, FacesContextMethodArgumentResolver.class,
				ServletRequestMethodArgumentResolver.class, ServletResponseMethodArgumentResolver.class,
				SpringFacesModelMethodArgumentResolver.class, NavigationContextMethodArgumentResolver.class);
		assertClasses(expected, actual);
	}

	@Test
	public void shouldInitDefaultBinderArgumentResolvers() throws Exception {
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.afterPropertiesSet();
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableBinderMethod).setHandlerMethodArgumentResolvers(this.argumentResolvers.capture());
		List<HandlerMethodArgumentResolver> actual = fieldValueAsList(this.argumentResolvers.getValue(),
				"argumentResolvers");
		List<Class<?>> expected = Arrays.<Class<?>> asList(RequestParamMethodArgumentResolver.class,
				RequestParamMapMethodArgumentResolver.class, PathVariableMethodArgumentResolver.class,
				ExpressionValueMethodArgumentResolver.class, ServletRequestMethodArgumentResolver.class,
				ServletResponseMethodArgumentResolver.class, RequestParamMethodArgumentResolver.class);
		assertClasses(expected, actual);
	}

	@Test
	public void shouldInitDefaultReturnValueHandlers() throws Exception {
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.afterPropertiesSet();
		this.resolver.resolve(this.facesContext, this.context);
		verify(this.invocableNavigationMethod).setHandlerMethodReturnValueHandlers(this.returnValueHandlers.capture());
		List<HandlerMethodReturnValueHandler> actual = fieldValueAsList(this.returnValueHandlers.getValue(),
				"returnValueHandlers");
		List<Class<?>> expected = Arrays.<Class<?>> asList(RequestResponseBodyMethodProcessor.class,
				ModelAndViewMethodReturnValueHandler.class, HttpEntityMethodProcessor.class,
				NavigationMethodReturnValueHandler.class);
		assertClasses(expected, actual);
	}

	@Test
	public void shouldSupportCanResolve() throws Exception {
		Object controllerBean = new ControllerBean();
		Object otherBean = new Object();
		setApplicationContextBean(controllerBean, true);
		given(this.context.getController()).willReturn(controllerBean, otherBean);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.afterPropertiesSet();
		assertThat(this.resolver.canResolve(this.facesContext, this.context), is(true));
		assertThat(this.resolver.canResolve(this.facesContext, this.context), is(false));
	}

	@Test
	public void shouldResolveToNullWhenNotIsResolveView() throws Exception {
		final Object view = new Object();
		NavigationOutcome resolved = doCustomResolve(view, null, true);
		assertThat(resolved, is(nullValue()));
	}

	@Test
	public void shouldNotWrapResolvedNavigationOutcome() throws Exception {
		final NavigationOutcome view = new NavigationOutcome("");
		NavigationOutcome resolved = doCustomResolve(view, null, false);
		assertThat(resolved, is(sameInstance(view)));
	}

	@Test
	public void shouldResolvedNavigationOutcomeWithModel() throws Exception {
		final Object view = "";
		final Map<String, ?> model = Collections.singletonMap("k", "v");
		NavigationOutcome resolved = doCustomResolve(view, model, false);
		assertThat(resolved.getDestination(), is(sameInstance(view)));
		assertThat(resolved.getImplicitModel().get("k"), is(equalTo((Object) "v")));
	}

	@Test
	public void shouldFailIfNoRequestMappingHandlerAdapterInSpringContext() throws Exception {
		setApplicationContextBean(this.bean, false);
		this.resolver.setApplicationContext(this.applicationContext);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to configure messageConverters using RequestMappingHandlerAdapter bean, "
				+ "please configure messageConverters directly");
		this.resolver.afterPropertiesSet();
	}

	private NavigationOutcome doCustomResolve(final Object view, final Map<String, ?> model,
			final boolean requestHandled) throws Exception {
		HandlerMethodReturnValueHandler customReturnValueHandler = new HandlerMethodReturnValueHandler() {
			public boolean supportsReturnType(MethodParameter returnType) {
				return true;
			}

			public void handleReturnValue(Object returnValue, MethodParameter returnType,
					ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
				mavContainer.setRequestHandled(requestHandled);
				mavContainer.setView(view);
				mavContainer.addAllAttributes(model);
			}
		};
		List<HandlerMethodReturnValueHandler> customReturnValueHandlers = Arrays.asList(customReturnValueHandler);
		this.resolver.setApplicationContext(this.applicationContext);
		this.resolver.setReturnValueHandlers(customReturnValueHandlers);
		this.resolver.afterPropertiesSet();
		return this.resolver.resolve(this.facesContext, this.context);
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> fieldValueAsList(Object object, String field) {
		return (List<T>) new DirectFieldAccessor(object).getPropertyValue(field);
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> fieldValueAsSet(Object object, String field) {
		return (Set<T>) new DirectFieldAccessor(object).getPropertyValue(field);
	}

	private static void assertClasses(Collection<?> expected, Collection<?> actual) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (Object o : actual) {
			o = unwrapFacesResponseCompleteReturnValueHandler(o);
			classes.add((o instanceof Class ? (Class<?>) o : o.getClass()));
		}
		for (Object o : expected) {
			o = unwrapFacesResponseCompleteReturnValueHandler(o);
			Class<?> objectClass = (o instanceof Class ? (Class<?>) o : o.getClass());
			classes.remove(objectClass);
		}
		assertThat(classes.size(), is(0));
	}

	private static Object unwrapFacesResponseCompleteReturnValueHandler(Object handler) {
		if (handler instanceof FacesResponseCompleteReturnValueHandler) {
			return new DirectFieldAccessor(handler).getPropertyValue("handler");
		}
		return handler;
	}

	@NavigationController
	public static class NavigationControllerBean {
		public WebDataBinder binder;

		@NavigationMapping
		public void onNavigate() {
		}

		@InitBinder
		public void initBinder(WebDataBinder binder) {
			this.binder = binder;
		}
	}

	@Controller
	public static class ControllerBean {
		@NavigationMapping
		public void onNavigate() {
		}
	}

	@Component
	public static class ComponentBean {
		@NavigationMapping
		public void onNavigate() {
		}
	}
}
