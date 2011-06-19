package org.springframework.springfaces.mvc.navigation.annotation;

import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.web.method.HandlerMethodSelector;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;

public class AnnotationNavigationOutcomeResolver extends ApplicationObjectSupport implements NavigationOutcomeResolver {

	// Based on AbstractHandlerMethodMapping

	// SOME use cases
	// : pick a destination based on @Value()
	// : write out a PDF document
	// : change something in the model and re-render

	private HandlerMethodArgumentResolverComposite argumentResolvers;

	@Override
	protected void initApplicationContext() throws BeansException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for navigation mappings in application context: " + getApplicationContext());
		}
		for (String beanName : getApplicationContext().getBeanNamesForType(Object.class)) {
			if (isHandler(getApplicationContext().getType(beanName))) {
				detectNavigationMethods(beanName);
			}
		}
	}

	protected boolean isHandler(Class<?> beanType) {
		return AnnotationUtils.findAnnotation(beanType, Controller.class) != null;
	}

	/**
	 * Detect and register handler methods for the specified handler.
	 * @param handler the bean name of a handler or a handler instance
	 */
	protected void detectNavigationMethods(final Object handler) {
		final Class<?> handlerType = ((handler instanceof String) ? getApplicationContext().getType((String) handler)
				: handler.getClass());

		Set<Method> methods = HandlerMethodSelector.selectMethods(handlerType, new MethodFilter() {
			public boolean matches(Method method) {
				return getNavigationInfoForMethod(method, handlerType) != null;
			}
		});
		for (Method method : methods) {
			NavigationOutcomeAnnotatedMethod info = getNavigationInfoForMethod(method, handlerType);
			registerNavigationMethod(handler, method, info);
		}
	}

	protected NavigationOutcomeAnnotatedMethod getNavigationInfoForMethod(Method method, Class<?> handlerType) {
		// TODO Auto-generated method stub
		return null;
	}

	private void registerNavigationMethod(Object handler, Method method, NavigationOutcomeAnnotatedMethod info) {
		// TODO Auto-generated method stub
		// HandlerMethod handlerMethod = new HandlerMethod(handler, method);
		// InvocableHandlerMethod
	}

	public boolean canResolve(NavigationContext context) {
		context.getOutcome();
		context.getFromAction();
		NavigationMapping n = null;
		n.value();
		n.fromAction();
		// TODO Auto-generated method stub
		return false;
	}

	public NavigationOutcome resolve(NavigationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	private void initArgumentResolvers() {
		if (argumentResolvers == null) {
			argumentResolvers = new HandlerMethodArgumentResolverComposite();
		}

		// Annotation-based resolvers
		// argumentResolvers.addResolver(new ServletModelAttributeMethodProcessor(false));
		// argumentResolvers.addResolver(new RequestHeaderMethodArgumentResolver(beanFactory));
		// argumentResolvers.addResolver(new RequestHeaderMapMethodArgumentResolver());
		// argumentResolvers.addResolver(new ServletCookieValueMethodArgumentResolver(beanFactory));
		// argumentResolvers.addResolver(new ExpressionValueMethodArgumentResolver(beanFactory));
		//
		// // Custom resolvers
		// argumentResolvers.addResolvers(customArgumentResolvers);
		//
		// // Type-based resolvers
		// argumentResolvers.addResolver(new ServletRequestMethodArgumentResolver());
		// FIXME not sure argumentResolvers.addResolver(new ServletResponseMethodArgumentResolver());
		// FIXME need out own version argumentResolvers.addResolver(new ModelMethodProcessor());
		//
		// // Default-mode resolution

		// Not supporting
		// argumentResolvers.addResolver(new RequestParamMethodArgumentResolver(beanFactory, false));
		// argumentResolvers.addResolver(new RequestParamMapMethodArgumentResolver());
		// argumentResolvers.addResolver(new PathVariableMethodArgumentResolver());
		// argumentResolvers.addResolver(new RequestResponseBodyMethodProcessor(messageConverters));
		// argumentResolvers.addResolver(new HttpEntityMethodProcessor(messageConverters));
		// argumentResolvers.addResolver(new ErrorsMethodArgumentResolver());
		// argumentResolvers.addResolver(new RequestParamMethodArgumentResolver(beanFactory, true));
		// argumentResolvers.addResolver(new ServletModelAttributeMethodProcessor(true));

		// FIXME ResponseBody support?

	}

}
