package org.springframework.springfaces.mvc.navigation.requestmapped;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.View;

/**
 * Creates a {@link RequestMappedRedirectView redirect} for destinations of the form <tt>"@method"</tt> or
 * <tt>"@bean.method"</tt> by inspecting {@link RequestMapping @RequestMapping} annotated methods of all MVC
 * {@link Controller Controllers}.
 * <p>
 * By default, any string destination prefixed with <tt>"@"</tt> will be resolved by this class (the prefix can be
 * {@link #setPrefix changed} if necessary). The destination should specify the name of the {@link Controller} method
 * that will be used create the redirect URL. If the name does not specify a bean then the
 * {@link SpringFacesContext#getHandler() current} handler will be used.
 * <p>
 * For example <tt>"@hotelsController.show"</tt> would resolve against the <tt>show()</tt> method of the
 * <tt>hotelsController</tt> bean. Where as <tt>"@search"</tt> would resolve against the <tt>search()</tt> method of the
 * current handler.
 * <p>
 * NOTE: The method that the destination references must be annotated with
 * <tt>{@link RequestMapping @RequestMapping}</tt>
 * <p>
 * Resolved destinations will expose model elements by inspecting arguments and annotations of the method in order to
 * create a complete and valid URL (see {@link RequestMappedRedirectView} for details).
 * 
 * @author Phillip Webb
 */
public class RequestMappedRedirectDestinationViewResolver implements DestinationViewResolver, ApplicationContextAware {

	/**
	 * A cache of destination name to {@link HandlerTypeAndMethod} to save expensive reflection calls.
	 */
	private Map<String, HandlerTypeAndMethod> cachedDestinations = new HashMap<String, HandlerTypeAndMethod>();

	/**
	 * Context details maintained and required for the {@link RequestMappedRedirectView}.
	 */
	private RequestMappedRedirectViewContext context = new RequestMappedRedirectViewContext();

	private ApplicationContext applicationContext;

	private String prefix = "@";

	public View resolveDestination(Object destination, Locale locale) throws Exception {
		if ((destination instanceof String) && ((String) destination).startsWith(prefix)) {
			try {
				return resolvePrefixedDestination(((String) destination).substring(prefix.length()), locale);
			} catch (RuntimeException e) {
				throw new IllegalStateException("Unable to resolve @RequestMapped view from destination '"
						+ destination + "'", e);
			}
		}
		return null;
	}

	/**
	 * Resolve a {@link #setPrefix prefixed} destination.
	 * @param destination the destination (not including any prefix)
	 * @param locale the locale in which to resolve the view
	 * @return a resolved view
	 * @throws Exception if the view cannot be resolved
	 */
	private View resolvePrefixedDestination(String destination, Locale locale) throws Exception {
		HandlerTypeAndMethod handlerTypeAndMethod = cachedDestinations.get(destination);
		if (handlerTypeAndMethod == null) {
			handlerTypeAndMethod = resolveClassAndMethod(destination);
			cachedDestinations.put(destination, handlerTypeAndMethod);
		}
		return createView(context, handlerTypeAndMethod.getHandlerType(), handlerTypeAndMethod.getMethod());
	}

	/**
	 * Factory method used to create the actual view once a handler and method have been resolved. The default
	 * implementation of this method returns a {@link RequestMappedRedirectView}.
	 * @param context the {@link RequestMappedRedirectViewContext context} for the created view
	 * @param handlerType the handler type resolved from the destination
	 * @param handlerMethod the handler method resolved from the destiantion
	 * @return A view instance
	 */
	protected View createView(RequestMappedRedirectViewContext context, Class<?> handlerType, Method handlerMethod) {
		return new RequestMappedRedirectView(context, handlerType, handlerMethod);
	}

	/**
	 * Obtain a {@link HandlerTypeAndMethod} for the specified handler.
	 * @param destination the destination (excluding any prefix)
	 * @return a {@link HandlerTypeAndMethod} resolved from the destination
	 * @throws IllegalStateException if the destination cannot be resolved
	 */
	protected HandlerTypeAndMethod resolveClassAndMethod(String destination) {
		Object handler;
		String handlerMethodName;
		int lastDot = destination.lastIndexOf(".");
		if (lastDot == -1) {
			handler = SpringFacesContext.getCurrentInstance(true).getHandler();
			Assert.state(handler != null, "Unable to locate SpringFaces MVC handler");
			handlerMethodName = destination;
		} else {
			handler = applicationContext.getBean(destination.substring(0, lastDot));
			handlerMethodName = destination.substring(lastDot + 1);
		}
		Assert.state(StringUtils.hasLength(handlerMethodName), "No method name specified as part of destination");
		Method handlerMethod = getHandlerMethod(handler, handlerMethodName);
		return new HandlerTypeAndMethod(handler.getClass(), handlerMethod);
	}

	/**
	 * Search the specified handler to find a {@link RequestMapping} annotated method by name.
	 * @param handler the handler
	 * @param handlerMethodName the name of the method to find
	 * @return the method
	 * @throws IllegalStateException on error locating the method
	 */
	private Method getHandlerMethod(Object handler, String handlerMethodName) {
		Method requestMappedMethod = null;
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(handler.getClass());
		for (Method method : methods) {
			if (method.getName().equals(handlerMethodName)
					&& (AnnotationUtils.findAnnotation(method, RequestMapping.class) != null)) {
				Assert.state(requestMappedMethod == null,
						"More than one @RequestMapping annotated method with the name '" + handlerMethodName
								+ "' exist in " + handler.getClass());
				requestMappedMethod = method;
			}
		}
		Assert.state(requestMappedMethod != null, "Unable to find @RequestMapping annotated method '"
				+ handlerMethodName + "' in " + handler.getClass());
		return requestMappedMethod;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * Set the prefix that indicates when this resolver will handle a destination. If not specified the default prefix
	 * of <tt>"@"</tt> will be used.
	 * @param prefix the destination prefix
	 */
	public void setPrefix(String prefix) {
		Assert.hasLength(prefix, "Prefix must not be empty");
		this.prefix = prefix;
	}

	// FIXME DC
	public void setPathMatcher(PathMatcher pathMatcher) {
		context.setPathMatcher(pathMatcher);
	}

	// FIXME DC
	public void setCustomArgumentResolvers(WebArgumentResolver[] customArgumentResolvers) {
		context.setCustomArgumentResolvers(customArgumentResolvers);
	}

	// FIXME DC
	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		context.setWebBindingInitializer(webBindingInitializer);
	}

	/**
	 * Holds a referecne to a class type and method. This object can be cached.
	 */
	private static class HandlerTypeAndMethod {
		private Class<?> handlerType;
		private Method method;

		public HandlerTypeAndMethod(Class<?> handlerType, Method method) {
			super();
			this.handlerType = handlerType;
			this.method = method;
		}

		public Class<?> getHandlerType() {
			return handlerType;
		}

		public Method getMethod() {
			return method;
		}
	}
}
