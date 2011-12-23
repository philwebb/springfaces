package org.springframework.springfaces.mvc.navigation.requestmapped.filter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * {@link MethodParameterFilter} that filters items when they include any of the specified annotations.
 * 
 * @author Phillip Webb
 */
public class AnnotationMethodParameterFilter implements MethodParameterFilter {

	private Set<Class<?>> ignoredAnnotations;

	/**
	 * Create a new {@link AnnotationMethodParameterFilter}.
	 * @param ignoredAnnotations annotations to filter
	 */
	public AnnotationMethodParameterFilter(Class<?>... ignoredAnnotations) {
		Assert.notNull(ignoredAnnotations, "IgnoredAnnotations must not be null");
		this.ignoredAnnotations = new HashSet<Class<?>>(Arrays.asList(ignoredAnnotations));
	}

	public boolean isFiltered(NativeWebRequest request, MethodParameter methodParameter) {
		for (Annotation ignoredAnnotation : methodParameter.getParameterAnnotations()) {
			if (this.ignoredAnnotations.contains(ignoredAnnotation.annotationType())) {
				return true;
			}
		}
		return false;
	}

}
