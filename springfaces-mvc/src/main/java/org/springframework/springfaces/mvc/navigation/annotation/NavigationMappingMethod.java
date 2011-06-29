package org.springframework.springfaces.mvc.navigation.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.stereotype.Controller;
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
class NavigationMappingMethod {

	private static final String[] IGNORED_METHOD_PREFIXES = { "on" };

	/**
	 * The name bean name .
	 */
	private String beanName;

	/**
	 * The bean type.
	 */
	private Class<?> beanType;

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
	 * If the method is on a {@link Controller} bean.
	 */
	private boolean controllerBeanMethod;

	/**
	 * The mapping filter.
	 */
	private NavigationMappingFilter filter;

	/**
	 * Create a new {@link NavigationMappingMethod}.
	 * @param beanName the bean name
	 * @param beanType the bean type
	 * @param method the method (this must be annotated with {@link NavigationMapping})
	 * @param controllerBeanMethod if the method is on a controller bean
	 */
	public NavigationMappingMethod(String beanName, Class<?> beanType, Method method, boolean controllerBeanMethod) {
		Assert.notNull(beanName, "BeanName must not be null");
		Assert.notNull(beanType, "BeanType must not be null");
		Assert.notNull(method, "Method must not be null");
		this.beanName = beanName;
		this.beanType = beanType;
		this.method = method;
		NavigationMapping annotation = AnnotationUtils.findAnnotation(method, NavigationMapping.class);
		Assert.state(
				annotation != null,
				"Unable to find @NavigationMapping annotation on method " + beanType.getSimpleName() + "."
						+ method.getName());
		this.outcomes = buildOutcomes(method, annotation);
		this.fromAction = buildFromAction(annotation);
		this.filter = buildFilter(annotation);
		this.controllerBeanMethod = controllerBeanMethod;
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

	private NavigationMappingFilter buildFilter(NavigationMapping annotation) {
		Class<? extends NavigationMappingFilter> filterClass = annotation.filter();
		if (NavigationMappingFilter.class.equals(filterClass)) {
			return NavigationMappingFilter.NONE;
		}
		try {
			return filterClass.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to create filter from class " + filterClass, e);
		}
	}

	/**
	 * Determines if this method can be used for the given navigation context.
	 * @param context the navigation context
	 * @return <tt>true</tt> if this method can be used to handle navigation
	 */
	public boolean canResolve(NavigationContext context) {
		return fromActionMatches(context) && outcomeMatches(context) && controllerMatches(context)
				&& filter.matches(context);
	}

	private boolean fromActionMatches(NavigationContext context) {
		return fromAction == null || fromAction.equals(context.getFromAction());
	}

	private boolean outcomeMatches(NavigationContext context) {
		return outcomes.contains(context.getOutcome());
	}

	private boolean controllerMatches(NavigationContext context) {
		if (!controllerBeanMethod) {
			return true;
		}
		return beanType.isInstance(context.getController());
	}

	public String getBeanName() {
		return beanName;
	}

	/**
	 * @return the bean type
	 */
	public Class<?> getBeanType() {
		return beanType;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}
}
