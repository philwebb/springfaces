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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.springfaces.mvc.method.support.FacesContextMethodArgumentResolver;
import org.springframework.springfaces.mvc.method.support.FacesResponseCompleteReturnValueHandler;
import org.springframework.springfaces.mvc.method.support.SpringFacesModelMethodArgumentResolver;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.annotation.support.NavigationContextMethodArgumentResolver;
import org.springframework.springfaces.mvc.navigation.annotation.support.NavigationMethodReturnValueHandler;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethodSelector;
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
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;

/**
 * {@link NavigationOutcomeResolver} that resolves JSF navigation outcomes using methods annotated with
 * {@code @NavigationMapping}. This resolver will search for {@code @NavigationMapping} methods from {@code @Controller}
 * or {@code @NavigationController} beans.
 * 
 * @author Phillip Webb
 * @see AbstractHandlerMethodMapping
 * @see RequestMappingHandlerAdapter
 */
public class NavigationMethodOutcomeResolver extends ApplicationObjectSupport implements NavigationOutcomeResolver,
		BeanFactoryAware, InitializingBean {

	private List<HandlerMethodArgumentResolver> customArgumentResolvers;

	private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;

	private List<HttpMessageConverter<?>> messageConverters;

	private WebBindingInitializer webBindingInitializer;

	private ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	private ConfigurableBeanFactory beanFactory;

	private final Map<Class<?>, Set<Method>> initBinderMethodCache = new ConcurrentHashMap<Class<?>, Set<Method>>();

	private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

	private List<HandlerMethodArgumentResolver> argumentResolvers;

	private HandlerMethodArgumentResolverComposite initBinderArgumentResolvers;

	private Set<NavigationMappingMethod> navigationMethods = new TreeSet<NavigationMappingMethod>();

	/**
	 * Set one or more custom argument resolvers to use with {@link NavigationMapping} and {@link InitBinder} methods.
	 * <p>
	 * Generally custom argument resolvers are invoked first. However this excludes default argument resolvers that rely
	 * on the presence of annotations (e.g. {@code @Value} etc.) Those resolvers can only be customized via
	 * {@link #setArgumentResolvers(List)}
	 * <p>
	 * An existing {@link WebArgumentResolver} can either adapted with {@link ServletWebArgumentResolverAdapter} or
	 * preferably converted to a {@link HandlerMethodArgumentResolver} instead.
	 * @param argumentResolvers the argument resolvers for {@link NavigationMapping} and {@link InitBinder} method.
	 */
	public void setCustomArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		this.customArgumentResolvers = argumentResolvers;
	}

	/**
	 * Set the argument resolvers to use with {@link NavigationMapping} and methods. This is an optional property
	 * providing full control over all argument resolvers in contrast to {@link #setCustomArgumentResolvers(List)},
	 * which does not override default registrations.
	 * @param argumentResolvers argument resolvers for {@link RequestMapping} and {@link ModelAttribute} methods
	 */
	public void setArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		if (argumentResolvers != null) {
			this.argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
			this.argumentResolvers.addAll(argumentResolvers);
		}
	}

	/**
	 * Set the argument resolvers to use with {@link InitBinder} methods. This is an optional property providing full
	 * control over all argument resolvers for {@link InitBinder} methods in contrast to
	 * {@link #setCustomArgumentResolvers(List)}, which does not override default registrations.
	 * @param argumentResolvers argument resolvers for {@link InitBinder} methods
	 */
	public void setInitBinderArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		if (argumentResolvers != null) {
			this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite();
			this.initBinderArgumentResolvers.addResolvers(argumentResolvers);
		}
	}

	/**
	 * Set custom return value handlers to use to handle the return values of {@link NavigationMapping} methods.
	 * <p>
	 * Generally custom return value handlers are invoked first. However this excludes default return value handlers
	 * that rely on the presence of annotations like {@code @Value}, and others. Those handlers can only be customized
	 * via {@link #setReturnValueHandlers(List)}.
	 * @param returnValueHandlers custom return value handlers for {@link NavigationMapping} methods
	 */
	public void setCustomReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		this.customReturnValueHandlers = returnValueHandlers;
	}

	/**
	 * Set the {@link HandlerMethodReturnValueHandler}s to use to use with {@link NavigationMapping} methods. This is an
	 * optional property providing full control over all return value handlers in contrast to
	 * {@link #setCustomReturnValueHandlers(List)}, which does not override default registrations.
	 * @param returnValueHandlers the return value handlers for {@link NavigationMapping} methods
	 */
	public void setReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		if (returnValueHandlers != null) {
			this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
			this.returnValueHandlers.addHandlers(returnValueHandlers);
		}
	}

	/**
	 * Set the message body converters to use.
	 * <p>
	 * These converters are used to convert from and to HTTP responses.
	 * @param messageConverters the message converters
	 */
	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		this.messageConverters = messageConverters;
	}

	/**
	 * Return the message body converters that this adapter has been configured with.
	 * @return the message converters
	 */
	public List<HttpMessageConverter<?>> getMessageConverters() {
		return this.messageConverters;
	}

	/**
	 * Set a WebBindingInitializer to apply configure every DataBinder instance this controller uses.
	 * @param webBindingInitializer the web binding initializer
	 */
	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	/**
	 * Return the WebBindingInitializer which applies pre-configured configuration to {@link DataBinder} instances.
	 * @return the web binder initializer
	 */
	public WebBindingInitializer getWebBindingInitializer() {
		return this.webBindingInitializer;
	}

	/**
	 * Set the ParameterNameDiscoverer to use for resolving method parameter names if needed (e.g. for default attribute
	 * names).
	 * <p>
	 * Default is a {@link org.springframework.core.LocalVariableTableParameterNameDiscoverer}.
	 * @param parameterNameDiscoverer The parameter name discoverer
	 */
	public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	@Override
	protected void initApplicationContext() throws BeansException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Looking for navigation mappings in application context: " + getApplicationContext());
		}
		this.navigationMethods = new HashSet<NavigationMappingMethod>();
		for (String beanName : getApplicationContext().getBeanNamesForType(Object.class)) {
			Class<?> beanType = getApplicationContext().getType(beanName);
			if (isNavigationBean(beanType)) {
				detectNavigationMethods(beanName, beanType);
			}
		}
	}

	private void detectNavigationMethods(final String beanName, final Class<?> beanType) {
		Set<Method> methods = HandlerMethodSelector.selectMethods(beanType, new MethodFilter() {
			public boolean matches(Method method) {
				return AnnotationUtils.findAnnotation(method, NavigationMapping.class) != null;
			}
		});
		boolean controllerBeanMethod = isControllerBean(beanType);
		for (Method method : methods) {
			this.navigationMethods.add(new NavigationMappingMethod(beanName, beanType, method, controllerBeanMethod));
		}
	}

	/**
	 * Determine if the specified bean type should be scanned for {@link NavigationMapping} methods.
	 * @param beanType the bean type
	 * @return <tt>true</tt> if the bean type should be scanned
	 */
	protected boolean isNavigationBean(Class<?> beanType) {
		return (isControllerBean(beanType) || (AnnotationUtils.findAnnotation(beanType, NavigationController.class) != null));
	}

	/**
	 * Determine if the specified bean type should be considered a MVC controller bean.
	 * @param beanType the bean type
	 * @return <tt>true</tt> if the bean is a MVC controller
	 */
	protected boolean isControllerBean(Class<?> beanType) {
		return (AnnotationUtils.findAnnotation(beanType, Controller.class) != null);
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	public void afterPropertiesSet() {
		initMessageConverters();
		initArgumentResolvers();
		initReturnValueHandlers();
		initInitBinderArgumentResolvers();
	}

	private void initMessageConverters() {
		if (this.messageConverters == null) {
			try {
				RequestMappingHandlerAdapter adapter = getApplicationContext().getBean(
						RequestMappingHandlerAdapter.class);
				Assert.state(adapter != null, "Unable to find RequestMappingHandlerAdapter bean");
				this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
				this.messageConverters.addAll(adapter.getMessageConverters());
			} catch (Exception e) {
				throw new IllegalStateException(
						"Unable to configure messageConverters using RequestMappingHandlerAdapter bean, "
								+ "please configure messageConverters directly", e);
			}
		}
	}

	private void initArgumentResolvers() {
		if (this.argumentResolvers != null) {
			return;
		}
		this.argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();

		// Annotation-based resolvers
		this.argumentResolvers.add(new RequestHeaderMethodArgumentResolver(this.beanFactory));
		this.argumentResolvers.add(new RequestHeaderMapMethodArgumentResolver());
		this.argumentResolvers.add(new ServletCookieValueMethodArgumentResolver(this.beanFactory));
		this.argumentResolvers.add(new ExpressionValueMethodArgumentResolver(this.beanFactory));

		// Custom resolvers
		if (this.customArgumentResolvers != null) {
			this.argumentResolvers.addAll(this.customArgumentResolvers);
		}

		// Type-based resolvers
		this.argumentResolvers.add(new FacesContextMethodArgumentResolver());
		this.argumentResolvers.add(new ServletRequestMethodArgumentResolver());
		this.argumentResolvers.add(new ServletResponseMethodArgumentResolver());
		this.argumentResolvers.add(new SpringFacesModelMethodArgumentResolver());
	}

	private void initInitBinderArgumentResolvers() {
		if (this.initBinderArgumentResolvers != null) {
			return;
		}
		this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite();

		// Annotation-based resolvers
		this.initBinderArgumentResolvers.addResolver(new RequestParamMethodArgumentResolver(this.beanFactory, false));
		this.initBinderArgumentResolvers.addResolver(new RequestParamMapMethodArgumentResolver());
		this.initBinderArgumentResolvers.addResolver(new PathVariableMethodArgumentResolver());
		this.initBinderArgumentResolvers.addResolver(new ExpressionValueMethodArgumentResolver(this.beanFactory));

		// Custom resolvers
		this.initBinderArgumentResolvers.addResolvers(this.customArgumentResolvers);

		// Type-based resolvers
		this.initBinderArgumentResolvers.addResolver(new ServletRequestMethodArgumentResolver());
		this.initBinderArgumentResolvers.addResolver(new ServletResponseMethodArgumentResolver());

		// Default-mode resolution
		this.initBinderArgumentResolvers.addResolver(new RequestParamMethodArgumentResolver(this.beanFactory, true));
	}

	private void initReturnValueHandlers() {
		if (this.returnValueHandlers != null) {
			return;
		}
		this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();

		// Annotation-based handlers
		this.returnValueHandlers.addHandler(new FacesResponseCompleteReturnValueHandler(
				new RequestResponseBodyMethodProcessor(this.messageConverters)));

		// Custom return value handlers
		this.returnValueHandlers.addHandlers(this.customReturnValueHandlers);

		// Type-based handlers
		this.returnValueHandlers.addHandler(new ModelAndViewMethodReturnValueHandler());
		this.returnValueHandlers.addHandler(new FacesResponseCompleteReturnValueHandler(new HttpEntityMethodProcessor(
				this.messageConverters)));

		// Default handler
		this.returnValueHandlers.addHandler(new NavigationMethodReturnValueHandler());
	}

	public boolean canResolve(FacesContext facesContext, NavigationContext context) {
		for (NavigationMappingMethod navigationMethod : this.navigationMethods) {
			if (navigationMethod.canResolve(context)) {
				return true;
			}
		}
		return false;
	}

	public NavigationOutcome resolve(FacesContext facesContext, NavigationContext context) throws Exception {
		for (NavigationMappingMethod navigationMethod : this.navigationMethods) {
			if (navigationMethod.canResolve(context)) {
				return resolve(facesContext, navigationMethod, context);
			}
		}
		throw new IllegalStateException("Unable to find annotated method to resolve navigation");
	}

	private NavigationOutcome resolve(FacesContext facesContext, NavigationMappingMethod navigationMethod,
			NavigationContext context) throws Exception {

		Object bean = getApplicationContext().getBean(navigationMethod.getBeanName());
		Class<?> beanType = ClassUtils.getUserClass(bean.getClass());

		WebDataBinderFactory binderFactory = createDataBinderFactory(bean, beanType);

		HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();
		argumentResolvers.addResolvers(this.argumentResolvers);
		argumentResolvers.addResolver(new NavigationContextMethodArgumentResolver(context));
		ServletInvocableHandlerMethod invocable = createInvocableNavigationMethod(bean, navigationMethod.getMethod());
		invocable.setDataBinderFactory(binderFactory);
		invocable.setHandlerMethodArgumentResolvers(argumentResolvers);
		invocable.setParameterNameDiscoverer(this.parameterNameDiscoverer);
		invocable.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);

		ExternalContext externalContext = facesContext.getExternalContext();
		ServletWebRequest request = new ServletWebRequest((HttpServletRequest) externalContext.getRequest(),
				(HttpServletResponse) externalContext.getResponse());
		ModelAndViewContainer modelAndViewContainer = new ModelAndViewContainer();
		invocable.invokeAndHandle(request, modelAndViewContainer);
		if (modelAndViewContainer.isRequestHandled()) {
			return null;
		}
		// NOTE: in this instance we are subverting the use of the model and view container, the view here actually
		// refers to the result of the method.
		Object result = modelAndViewContainer.getView();
		if (result == null) {
			return null;
		}
		if (result instanceof NavigationOutcome) {
			return (NavigationOutcome) result;
		}
		return new NavigationOutcome(result, modelAndViewContainer.getModel());
	}

	private WebDataBinderFactory createDataBinderFactory(Object bean, Class<?> handlerType) {
		List<InvocableHandlerMethod> initBinderMethods = new ArrayList<InvocableHandlerMethod>();

		Set<Method> binderMethods = this.initBinderMethodCache.get(handlerType);
		if (binderMethods == null) {
			binderMethods = HandlerMethodSelector.selectMethods(handlerType,
					RequestMappingHandlerAdapter.INIT_BINDER_METHODS);
			this.initBinderMethodCache.put(handlerType, binderMethods);
		}

		for (Method method : binderMethods) {
			InvocableHandlerMethod binderMethod = createInvocableBinderMethod(bean, method);
			binderMethod.setHandlerMethodArgumentResolvers(this.initBinderArgumentResolvers);
			binderMethod.setDataBinderFactory(new DefaultDataBinderFactory(this.webBindingInitializer));
			binderMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
			initBinderMethods.add(binderMethod);
		}

		return new ServletRequestDataBinderFactory(initBinderMethods, this.webBindingInitializer);
	}

	/**
	 * Factory method used to create a {@link ServletInvocableHandlerMethod}.
	 * @param handler the handler
	 * @param method the navigation mapping method to invoke
	 * @return a new {@link ServletInvocableHandlerMethod} instance
	 */
	protected ServletInvocableHandlerMethod createInvocableNavigationMethod(Object handler, Method method) {
		return new ServletInvocableHandlerMethod(handler, method);
	}

	/**
	 * Factory method used to create a {@link InvocableHandlerMethod}.
	 * @param handler the handler
	 * @param method the binder method to invoke
	 * @return a new {@link InvocableHandlerMethod} instance
	 */
	protected InvocableHandlerMethod createInvocableBinderMethod(Object handler, Method method) {
		return new InvocableHandlerMethod(handler, method);
	}
}
