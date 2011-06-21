package org.springframework.springfaces.mvc.navigation.annotation;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.FacesWebRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethodSelector;
import org.springframework.web.method.annotation.support.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.support.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.support.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.support.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.support.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.support.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.support.ServletWebArgumentResolverAdapter;

public class AnnotationNavigationOutcomeResolver extends ApplicationObjectSupport implements NavigationOutcomeResolver,
		BeanFactoryAware, InitializingBean {

	// Based on AbstractHandlerMethodMapping RequestMappingHandlerAdapter

	// SOME use cases
	// : pick a destination based on @Value()
	// : write out a PDF document
	// : change something in the model and re-render
	private List<HandlerMethodArgumentResolver> customArgumentResolvers;

	private Set<NavigationOutcomeAnnotatedMethod> navigationMethods = new HashSet<NavigationOutcomeAnnotatedMethod>();

	private HandlerMethodArgumentResolverComposite argumentResolvers;

	private ConfigurableBeanFactory beanFactory;

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
		if (result == null) {
			return null;
		}
		if (result instanceof NavigationOutcome) {
			return (NavigationOutcome) result;
		}
		return new NavigationOutcome(result);
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	private void initArgumentResolvers() {
		if (argumentResolvers == null) {
			argumentResolvers = new HandlerMethodArgumentResolverComposite();
		}
		// Annotation-based resolvers
		argumentResolvers.addResolver(new ServletModelAttributeMethodProcessor(false));
		argumentResolvers.addResolver(new RequestHeaderMethodArgumentResolver(beanFactory));
		argumentResolvers.addResolver(new RequestHeaderMapMethodArgumentResolver());
		argumentResolvers.addResolver(new ServletCookieValueMethodArgumentResolver(beanFactory));
		argumentResolvers.addResolver(new ExpressionValueMethodArgumentResolver(beanFactory));

		// Custom resolvers
		argumentResolvers.addResolvers(customArgumentResolvers);

		// Type-based resolvers
		argumentResolvers.addResolver(new ServletRequestMethodArgumentResolver());
		// FIXME not sure argumentResolvers.addResolver(new ServletResponseMethodArgumentResolver());
		// FIXME need out own version argumentResolvers.addResolver(new ModelMethodProcessor());

		// FIXME ResponseBody support?

	}

	/**
	 * Set one or more custom argument resolvers to use with {@link RequestMapping}, {@link ModelAttribute}, and
	 * {@link InitBinder} methods.
	 * <p>
	 * Generally custom argument resolvers are invoked first. However this excludes default argument resolvers that rely
	 * on the presence of annotations (e.g. {@code @RequestParameter}, {@code @PathVariable}, etc.) Those resolvers can
	 * only be customized via {@link #setArgumentResolvers(List)}
	 * <p>
	 * An existing {@link WebArgumentResolver} can either adapted with {@link ServletWebArgumentResolverAdapter} or
	 * preferably converted to a {@link HandlerMethodArgumentResolver} instead.
	 */
	public void setCustomArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		this.customArgumentResolvers = argumentResolvers;
	}

	/**
	 * Set the argument resolvers to use with {@link RequestMapping} and {@link ModelAttribute} methods. This is an
	 * optional property providing full control over all argument resolvers in contrast to
	 * {@link #setCustomArgumentResolvers(List)}, which does not override default registrations.
	 * @param argumentResolvers argument resolvers for {@link RequestMapping} and {@link ModelAttribute} methods
	 */
	public void setArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		if (argumentResolvers != null) {
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
			this.argumentResolvers.addResolvers(argumentResolvers);
		}
	}

}
