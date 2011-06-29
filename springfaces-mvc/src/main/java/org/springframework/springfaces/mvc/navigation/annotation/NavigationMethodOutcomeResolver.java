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
import javax.xml.transform.Source;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter;
import org.springframework.springfaces.mvc.method.support.FacesContextMethodArgumentResolver;
import org.springframework.springfaces.mvc.method.support.FacesResponseReturnValueHandler;
import org.springframework.springfaces.mvc.method.support.SpringFacesModelMethodArgumentResolver;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.annotation.support.NavigationMethodReturnValueHandler;
import org.springframework.stereotype.Controller;
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
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethodSelector;
import org.springframework.web.method.annotation.support.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.support.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.support.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.support.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.annotation.support.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.mvc.method.annotation.support.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.support.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.support.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.support.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.support.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.support.ServletResponseMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.support.ServletWebArgumentResolverAdapter;

/**
 * @see AbstractHandlerMethodMapping
 * @see RequestMappingHandlerAdapter
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 */
public class NavigationMethodOutcomeResolver extends ApplicationObjectSupport implements NavigationOutcomeResolver,
		BeanFactoryAware, InitializingBean {

	// FIXME DC

	private List<HandlerMethodArgumentResolver> customArgumentResolvers;

	private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;

	private List<HttpMessageConverter<?>> messageConverters;

	private WebBindingInitializer webBindingInitializer;

	private ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	private ConfigurableBeanFactory beanFactory;

	private final Map<Class<?>, Set<Method>> initBinderMethodCache = new ConcurrentHashMap<Class<?>, Set<Method>>();

	private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

	private HandlerMethodArgumentResolverComposite argumentResolvers;

	private HandlerMethodArgumentResolverComposite initBinderArgumentResolvers;

	private Set<NavigationMappingMethod> navigationMethods = new TreeSet<NavigationMappingMethod>();

	/**
	 * Create a {@link NavigationMethodOutcomeResolver} instance.
	 */
	public NavigationMethodOutcomeResolver() {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false); // See SPR-7316
		messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringHttpMessageConverter);
		messageConverters.add(new SourceHttpMessageConverter<Source>());
		messageConverters.add(new XmlAwareFormHttpMessageConverter());
	}

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
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
			this.argumentResolvers.addResolvers(argumentResolvers);
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
		return messageConverters;
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
		return webBindingInitializer;
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
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for navigation mappings in application context: " + getApplicationContext());
		}
		navigationMethods = new HashSet<NavigationMappingMethod>();
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
			navigationMethods.add(new NavigationMappingMethod(beanName, beanType, method, controllerBeanMethod));
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

	protected boolean isControllerBean(Class<?> beanType) {
		return (AnnotationUtils.findAnnotation(beanType, Controller.class) != null);
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	public void afterPropertiesSet() {
		initArgumentResolvers();
		initReturnValueHandlers();
		initInitBinderArgumentResolvers();
	}

	private void initArgumentResolvers() {
		if (argumentResolvers != null) {
			return;
		}

		initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite();

		// Annotation-based resolvers
		argumentResolvers.addResolver(new RequestHeaderMethodArgumentResolver(beanFactory));
		argumentResolvers.addResolver(new RequestHeaderMapMethodArgumentResolver());
		argumentResolvers.addResolver(new ServletCookieValueMethodArgumentResolver(beanFactory));
		argumentResolvers.addResolver(new ExpressionValueMethodArgumentResolver(beanFactory));

		// Custom resolvers
		argumentResolvers.addResolvers(customArgumentResolvers);

		// Type-based resolvers
		argumentResolvers.addResolver(new FacesContextMethodArgumentResolver());
		argumentResolvers.addResolver(new ServletRequestMethodArgumentResolver());
		argumentResolvers.addResolver(new ServletResponseMethodArgumentResolver());
		argumentResolvers.addResolver(new SpringFacesModelMethodArgumentResolver());
	}

	private void initInitBinderArgumentResolvers() {
		if (initBinderArgumentResolvers != null) {
			return;
		}

		initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite();

		// Annotation-based resolvers
		initBinderArgumentResolvers.addResolver(new RequestParamMethodArgumentResolver(beanFactory, false));
		initBinderArgumentResolvers.addResolver(new RequestParamMapMethodArgumentResolver());
		initBinderArgumentResolvers.addResolver(new PathVariableMethodArgumentResolver());
		initBinderArgumentResolvers.addResolver(new ExpressionValueMethodArgumentResolver(beanFactory));

		// Custom resolvers
		initBinderArgumentResolvers.addResolvers(customArgumentResolvers);

		// Type-based resolvers
		initBinderArgumentResolvers.addResolver(new ServletRequestMethodArgumentResolver());
		initBinderArgumentResolvers.addResolver(new ServletResponseMethodArgumentResolver());

		// Default-mode resolution
		initBinderArgumentResolvers.addResolver(new RequestParamMethodArgumentResolver(beanFactory, true));
	}

	private void initReturnValueHandlers() {
		if (returnValueHandlers != null) {
			return;
		}

		returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();

		// Annotation-based handlers
		returnValueHandlers.addHandler(new FacesResponseReturnValueHandler(new RequestResponseBodyMethodProcessor(
				messageConverters)));

		// Custom return value handlers
		returnValueHandlers.addHandlers(customReturnValueHandlers);

		// Type-based handlers
		returnValueHandlers.addHandler(new FacesResponseReturnValueHandler(new HttpEntityMethodProcessor(
				messageConverters)));

		// Default handler
		returnValueHandlers.addHandler(new NavigationMethodReturnValueHandler());
	}

	public boolean canResolve(FacesContext facesContext, NavigationContext context) {
		for (NavigationMappingMethod navigationMethod : navigationMethods) {
			if (navigationMethod.canResolve(context)) {
				return true;
			}
		}
		return false;
	}

	public NavigationOutcome resolve(FacesContext facesContext, NavigationContext context) throws Exception {
		for (NavigationMappingMethod navigationMethod : navigationMethods) {
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

		ServletInvocableHandlerMethod invocable = new ServletInvocableHandlerMethod(bean, navigationMethod.getMethod());
		invocable.setDataBinderFactory(binderFactory);
		invocable.setHandlerMethodArgumentResolvers(argumentResolvers);
		invocable.setParameterNameDiscoverer(parameterNameDiscoverer);
		invocable.setHandlerMethodReturnValueHandlers(returnValueHandlers);

		ExternalContext externalContext = facesContext.getExternalContext();
		NativeWebRequest request = new ServletWebRequest((HttpServletRequest) externalContext.getRequest(),
				(HttpServletResponse) externalContext.getResponse());
		ModelAndViewContainer modelAndViewContainer = new ModelAndViewContainer();
		invocable.invokeAndHandle(request, modelAndViewContainer);
		Object result = modelAndViewContainer.getView();
		if (result == null) {
			return null;
		}
		// FIXME push this into the default handler?
		// FIXME support ModelAndView types

		if (result instanceof NavigationOutcome) {
			return (NavigationOutcome) result;
		}
		return new NavigationOutcome(result, modelAndViewContainer.getModel());
	}

	private WebDataBinderFactory createDataBinderFactory(Object bean, Class<?> handlerType) {
		List<InvocableHandlerMethod> initBinderMethods = new ArrayList<InvocableHandlerMethod>();

		Set<Method> binderMethods = initBinderMethodCache.get(handlerType);
		if (binderMethods == null) {
			binderMethods = HandlerMethodSelector.selectMethods(handlerType,
					RequestMappingHandlerAdapter.INIT_BINDER_METHODS);
			initBinderMethodCache.put(handlerType, binderMethods);
		}

		for (Method method : binderMethods) {
			InvocableHandlerMethod binderMethod = new InvocableHandlerMethod(bean, method);
			binderMethod.setHandlerMethodArgumentResolvers(this.initBinderArgumentResolvers);
			binderMethod.setDataBinderFactory(new DefaultDataBinderFactory(this.webBindingInitializer));
			binderMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);

			initBinderMethods.add(binderMethod);
		}

		return new ServletRequestDataBinderFactory(initBinderMethods, this.webBindingInitializer);
	}

}
