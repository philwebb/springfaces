package org.springframework.springfaces.mvc.navigation.annotation;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.web.context.request.FacesWebRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethodSelector;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AnnotationNavigationOutcomeResolver extends ApplicationObjectSupport implements NavigationOutcomeResolver {

	// Based on AbstractHandlerMethodMapping

	// SOME use cases
	// : pick a destination based on @Value()
	// : write out a PDF document
	// : change something in the model and re-render

	private Set<NavigationOutcomeAnnotatedMethod> navigationMethods = new HashSet<NavigationOutcomeAnnotatedMethod>();

	private HandlerMethodArgumentResolverComposite argumentResolvers;

	@Override
	protected void initApplicationContext() throws BeansException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for navigation mappings in application context: " + getApplicationContext());
		}
		navigationMethods = new HashSet<NavigationOutcomeAnnotatedMethod>();
		for (String beanName : getApplicationContext().getBeanNamesForType(Object.class)) {
			if (isNavigationBean(getApplicationContext().getType(beanName))) {
				detectNavigationMethods(beanName);
			}
		}
	}

	public void afterPropertiesSet() {
		initArgumentResolvers();
	}

	protected boolean isNavigationBean(Class<?> beanType) {
		return AnnotationUtils.findAnnotation(beanType, Controller.class) != null;
	}

	private void detectNavigationMethods(final String beanName) {
		final Class<?> beanType = getApplicationContext().getType(beanName);
		Set<Method> methods = HandlerMethodSelector.selectMethods(beanType, new MethodFilter() {
			public boolean matches(Method method) {
				return AnnotationUtils.findAnnotation(method, NavigationMapping.class) != null;
			}
		});
		for (Method method : methods) {
			navigationMethods.add(new NavigationOutcomeAnnotatedMethod(beanName, beanType, method));
		}
	}

	public boolean canResolve(FacesContext facesContext, NavigationContext context) {
		for (NavigationOutcomeAnnotatedMethod navigationMethod : navigationMethods) {
			if (navigationMethod.canResolve(context)) {
				return true;
			}
		}
		return false;
	}

	public NavigationOutcome resolve(FacesContext facesContext, NavigationContext context) throws Exception {
		for (NavigationOutcomeAnnotatedMethod navigationMethod : navigationMethods) {
			if (navigationMethod.canResolve(context)) {
				return resolve(facesContext, navigationMethod, context);
			}
		}
		throw new IllegalStateException("Unable to find annotated method to resolve navigation");
	}

	private NavigationOutcome resolve(FacesContext facesContext, NavigationOutcomeAnnotatedMethod navigationMethod,
			NavigationContext context) throws Exception {
		Object bean = getApplicationContext().getBean(navigationMethod.getBeanName());
		InvocableHandlerMethod invocable = new InvocableHandlerMethod(bean, navigationMethod.getMethod());
		// FIXME
		// invocable.setDataBinderFactory(dataBinderFactory);
		invocable.setHandlerMethodArgumentResolvers(argumentResolvers);
		// invocable.setParameterNameDiscoverer(parameterNameDiscoverer);

		NativeWebRequest request = new FacesWebRequest(facesContext);
		ModelAndViewContainer mav = null;
		Object result = invocable.invokeForRequest(request, mav);
		if (result instanceof NavigationOutcome) {
			return (NavigationOutcome) result;
		}
		return new NavigationOutcome(result);
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
