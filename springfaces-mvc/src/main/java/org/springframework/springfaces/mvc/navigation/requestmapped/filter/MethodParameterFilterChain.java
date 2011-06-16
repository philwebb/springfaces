package org.springframework.springfaces.mvc.navigation.requestmapped.filter;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Allows multiple {@link MethodParameterFilter}s to be chained together into a single filter.
 * 
 * @author Phillip Webb
 */
public class MethodParameterFilterChain implements MethodParameterFilter {

	private MethodParameterFilter[] filters;

	/**
	 * Create a new {@link MethodParameterFilterChain}.
	 * @param filters the chain of filters
	 */
	public MethodParameterFilterChain(MethodParameterFilter... filters) {
		this.filters = filters;
	}

	public boolean isFiltered(NativeWebRequest request, MethodParameter methodParameter) {
		if (filters != null) {
			for (MethodParameterFilter filter : filters) {
				if (filter.isFiltered(request, methodParameter)) {
					return true;
				}
			}
		}
		return false;
	}

}
