package org.springframework.springfaces.mvc.navigation.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.annotation.AnnotationUtils;
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

		List<NavigationMappingFilter> filters = new ArrayList<NavigationMappingFilter>();
		filters.add(new OutcomesFilter(method, annotation));
		filters.add(new FromActionFilter(annotation));
		if (controllerBeanMethod) {
			filters.add(new ControllerFilter());
		}
		if (!NavigationMappingFilter.class.equals(annotation.filter())) {
			filters.add(createSpecifiedFilter(annotation));
		}
		this.filter = new CompositeNavigationMappingFilter(filters.toArray(new NavigationMappingFilter[] {}));
	}

	private NavigationMappingFilter createSpecifiedFilter(NavigationMapping annotation) {
		try {
			return annotation.filter().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to create filter from class " + annotation.filter(), e);
		}
	}

	/**
	 * Determines if this method can be used for the given navigation context.
	 * @param context the navigation context
	 * @return <tt>true</tt> if this method can be used to handle navigation
	 */
	public boolean canResolve(NavigationContext context) {
		return filter.matches(context);
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

	private class OutcomesFilter implements NavigationMappingFilter {
		private Set<String> outcomes;

		public OutcomesFilter(Method method, NavigationMapping annotation) {
			if (ObjectUtils.isEmpty(annotation.value())) {
				this.outcomes = Collections.singleton(buildOutcomeFromMethodName(method));
			} else {
				this.outcomes = new HashSet<String>(Arrays.asList(annotation.value()));
			}
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

		public boolean matches(NavigationContext context) {
			return outcomes.contains(context.getOutcome());
		}
	}

	private class FromActionFilter implements NavigationMappingFilter {
		private String fromAction;

		public FromActionFilter(NavigationMapping annotation) {
			if (StringUtils.hasText(annotation.fromAction())) {
				this.fromAction = annotation.fromAction();
			}
		}

		public boolean matches(NavigationContext context) {
			return fromAction == null || fromAction.equals(context.getFromAction());
		}
	}

	private class ControllerFilter implements NavigationMappingFilter {
		public boolean matches(NavigationContext context) {
			return beanType.isInstance(context.getController());
		}
	}

}
