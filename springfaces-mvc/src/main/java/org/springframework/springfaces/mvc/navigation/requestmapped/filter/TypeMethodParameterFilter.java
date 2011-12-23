package org.springframework.springfaces.mvc.navigation.requestmapped.filter;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * {@link MethodParameterFilter} that filters items when their type inherits the specified values.
 * 
 * @author Phillip Webb
 */
public class TypeMethodParameterFilter implements MethodParameterFilter {

	private Collection<Class<?>> ignoredTypes;

	/**
	 * Create a new {@link TypeMethodParameterFilter}.
	 * @param ignoredTypes the ignored types
	 */
	public TypeMethodParameterFilter(Class<?>... ignoredTypes) {
		Assert.notNull(ignoredTypes, "IgnoredTypes must not be null");
		this.ignoredTypes = Arrays.asList(ignoredTypes);
	}

	public boolean isFiltered(NativeWebRequest request, MethodParameter methodParameter) {
		Class<?> parameterType = methodParameter.getParameterType();
		for (Class<?> ignoredType : this.ignoredTypes) {
			if (ignoredType.isAssignableFrom(parameterType)) {
				return true;
			}
		}
		return false;
	}

}
