package org.springframework.springfaces.mvc.navigation.requestmapped.filter;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Strategy interface that can be used to filter {@link MethodParameter}s.
 * 
 * @author Phillip Webb
 */
public interface MethodParameterFilter {

	/**
	 * Determine if the specified parameter should be filtered.
	 * @param request the current native web request
	 * @param methodParameter the method parameter
	 * @return if the parameter should be filtered
	 */
	boolean isFiltered(NativeWebRequest request, MethodParameter methodParameter);

}
