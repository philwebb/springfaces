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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.View;

/**
 * Allows views of the form '@bean.method' to be resolved against {@link RequestMapping} annotated methods of
 * {@link Controller} beans.
 * 
 * @author Phillip Webb
 */
public class RequestMappedDestinationViewResolver implements DestinationViewResolver, ApplicationContextAware {

	private Map<String, HandlerClassAndHandlerMethod> cache = new HashMap<String, HandlerClassAndHandlerMethod>();

	private PathMatcher pathMatcher = new AntPathMatcher();

	private WebArgumentResolver[] customArgumentResolvers;

	private WebBindingInitializer webBindingInitializer;

	private ApplicationContext applicationContext;

	public View resolveDestination(Object destination, Locale locale) throws Exception {
		if ((destination instanceof String) && ((String) destination).startsWith("@")) {
			try {
				return resolveDestinationString(((String) destination).substring(1), locale);
			} catch (RuntimeException e) {
				throw new IllegalStateException("Unable to resolve @RequestMapped view from destination '"
						+ destination + "'", e);
			}
		}
		return null;
	}

	private View resolveDestinationString(String destination, Locale locale) throws Exception {

		HandlerClassAndHandlerMethod handlerClassAndHandlerMethod = cache.get(destination);
		if (handlerClassAndHandlerMethod == null) {
			handlerClassAndHandlerMethod = resolveHandlerClassAndHandlerMethod(destination);
			cache.put(destination, handlerClassAndHandlerMethod);
		}
		return new RequestMappedView(handlerClassAndHandlerMethod.getHandlerClass(),
				handlerClassAndHandlerMethod.getHandlerMethod(), pathMatcher, webBindingInitializer);
	}

	private HandlerClassAndHandlerMethod resolveHandlerClassAndHandlerMethod(String destination) {
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
		return new HandlerClassAndHandlerMethod(handler.getClass(), handlerMethod);
	}

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

	public void setCustomArgumentResolvers(WebArgumentResolver[] customArgumentResolvers) {
		this.customArgumentResolvers = customArgumentResolvers;
	}

	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	private static class HandlerClassAndHandlerMethod {
		private Class<?> handlerClass;
		private Method handlerMethod;

		public HandlerClassAndHandlerMethod(Class<?> handlerClass, Method handlerMethod) {
			super();
			this.handlerClass = handlerClass;
			this.handlerMethod = handlerMethod;
		}

		public Class<?> getHandlerClass() {
			return handlerClass;
		}

		public Method getHandlerMethod() {
			return handlerMethod;
		}
	}
}
