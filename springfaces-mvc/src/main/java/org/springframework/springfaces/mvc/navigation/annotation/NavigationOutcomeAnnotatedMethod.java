package org.springframework.springfaces.mvc.navigation.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.mvc.HandlerUtils;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Encapsulates information about a {@link NavigationMapping} annotated bean method. Values from the
 * {@link NavigationMapping} annotation will be used to determine if this method can {@link #canResolve resolve} a given
 * {@link NavigationContext}.
 * 
 * @author Phillip Webb
 */
public class NavigationOutcomeAnnotatedMethod {

	private static final String[] IGNORED_METHOD_PREFIXES = { "on" };

	/**
	 * The bean that contains the method.
	 */
	private Object bean;

	/**
	 * The NavigationMapping annotated method.
	 */
	private Method method;

	/**
	 * A list of outcomes that this method supports (never empty).
	 */
	private Set<String> outcomes;

	/**
	 * The fromAction constraint or <tt>null</tt> if all actions are considered.
	 */
	private String fromAction;

	/**
	 * A set of handler classes that this method supports or <tt>null</tt> if all handlers are considered.
	 */
	private Set<Class<?>> handlerClasses;

	/**
	 * Create a new {@link NavigationOutcomeAnnotatedMethod}.
	 * @param bean the bean
	 * @param method the method (this must be annotated with {@link NavigationMapping})
	 */
	public NavigationOutcomeAnnotatedMethod(Object bean, Method method) {
		Assert.notNull(bean, "Bean must not be null");
		Assert.notNull(method, "Method must not be null");
		this.bean = bean;
		this.method = method;
		NavigationMapping annotation = AnnotationUtils.findAnnotation(method, NavigationMapping.class);
		Assert.state(annotation != null, "Unable to find @NavigationMapping annotation on method "
				+ bean.getClass().getSimpleName() + "." + method.getName());
		this.outcomes = buildOutcomes(method, annotation);
		this.fromAction = buildFromAction(annotation);
		this.handlerClasses = buildHandlerClasses(annotation, bean);
	}

	private Set<String> buildOutcomes(Method method, NavigationMapping annotation) {
		if (ObjectUtils.isEmpty(annotation.value())) {
			return Collections.singleton(buildOutcomeFromMethodName(method));
		}
		return new HashSet<String>(Arrays.asList(annotation.value()));
	}

	private String buildOutcomeFromMethodName(Method method) {
		String outcome = method.getName();
		for (String ignoredPrefix : IGNORED_METHOD_PREFIXES) {
			if (outcome.length() > ignoredPrefix.length() && outcome.startsWith(ignoredPrefix)) {
				StringBuffer outcomeBuffer = new StringBuffer(outcome.substring(ignoredPrefix.length()));
				outcomeBuffer.setCharAt(0, Character.toLowerCase(outcomeBuffer.charAt(0)));
				return outcomeBuffer.toString();
			}
		}
		return outcome;
	}

	private String buildFromAction(NavigationMapping annotation) {
		if (StringUtils.hasText(annotation.fromAction())) {
			return annotation.fromAction();
		}
		return null;
	}

	private Set<Class<?>> buildHandlerClasses(NavigationMapping annotation, Object bean) {
		if (annotation.handlers().length == 0) {
			return null;
		}
		if (annotation.handlers().length == 1 && void.class.equals(annotation.handlers()[0])) {
			return Collections.<Class<?>> singleton(bean.getClass());
		}
		return new HashSet<Class<?>>(Arrays.asList(annotation.handlers()));
	}

	/**
	 * Determines if this method can be used for the given navigation context.
	 * @param context the navigation context
	 * @return <tt>true</tt> if this method can be used to handle navigation
	 */
	public boolean canResolve(NavigationContext context) {
		return fromActionMatches(context) && outcomeMatches(context) && handlerMatches(context);
	}

	private boolean fromActionMatches(NavigationContext context) {
		return fromAction == null || fromAction.equals(context.getFromAction());
	}

	private boolean outcomeMatches(NavigationContext context) {
		return outcomes.contains(context.getOutcome());
	}

	private boolean handlerMatches(NavigationContext context) {
		if (handlerClasses == null) {
			return true;
		}
		Object handler = HandlerUtils.getHandlerBean(context.getHandler());
		for (Class<?> handlerClass : handlerClasses) {
			if (handlerClass.isInstance(handler)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the bean
	 */
	public Object getBean() {
		return bean;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}
}
