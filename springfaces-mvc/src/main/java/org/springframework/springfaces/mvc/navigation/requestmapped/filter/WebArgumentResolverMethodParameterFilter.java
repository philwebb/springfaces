package org.springframework.springfaces.mvc.navigation.requestmapped.filter;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * {@link MethodParameterFilter} that filters items when any of the specified {@link WebArgumentResolver}s can handle
 * the item.
 * 
 * @author Phillip Webb
 */
public class WebArgumentResolverMethodParameterFilter implements MethodParameterFilter {

	private WebArgumentResolver[] webArgumentResolvers;

	/**
	 * Create a new {@link WebArgumentResolverMethodParameterFilter}.
	 * @param webArgumentResolvers the web argument resolvers to filter
	 */
	public WebArgumentResolverMethodParameterFilter(WebArgumentResolver... webArgumentResolvers) {
		this.webArgumentResolvers = webArgumentResolvers;
	}

	public boolean isFiltered(NativeWebRequest request, MethodParameter methodParameter) {
		if (this.webArgumentResolvers != null) {
			for (WebArgumentResolver resolver : this.webArgumentResolvers) {
				try {
					if (resolver.resolveArgument(methodParameter, request) != WebArgumentResolver.UNRESOLVED) {
						return true;
					}
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}
		return false;
	}
}
