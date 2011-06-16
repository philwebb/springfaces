package org.springframework.springfaces.mvc.navigation.requestmapped;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;

/**
 * Provides context for {@link RequestMappedRedirectView} and {@link RequestMappedRedirectViewModelBuilder}.
 * 
 * @author Phillip Webb
 */
public interface RequestMappedRedirectViewContext {

	/**
	 * Returns the PathMatcher implementation to use for combining URL paths from registered URL patterns. If this
	 * method returns <tt>null</tt> then the {@link org.springframework.util.AntPathMatcher} should be used.
	 * @return the path matcher or <tt>null</tt>
	 */
	public PathMatcher getPathMatcher();

	/**
	 * Returns a custom WebArgumentResolvers to use for special method parameter types.
	 * @return array of {@link WebArgumentResolver}s or <tt>null</tt>
	 */
	public WebArgumentResolver[] getCustomArgumentResolvers();

	/**
	 * Returns a WebBindingInitializer which will apply pre-configured configuration to every DataBinder that is used.
	 * @return a web binding initializer or <tt>null</tt>
	 */
	public WebBindingInitializer getWebBindingInitializer();

	/**
	 * Returns the ParameterNameDiscoverer to use for resolving method parameter names if needed (e.g. for default
	 * attribute names). If this method returns <tt>null</tt> the
	 * {@link org.springframework.core.LocalVariableTableParameterNameDiscoverer} should be used.
	 * @return a paramter name discoverer or <tt>null</tt>
	 */
	public ParameterNameDiscoverer getParameterNameDiscoverer();

	/**
	 * Returns the servlet path that should be used to access the dispatcher servlet. If this method returns
	 * <tt>null</tt> the path should taken from current HTTP {@link HttpServletRequest#getServletPath() request}.
	 * @return the dispatcher servlet path or <tt>null</tt>
	 */
	public String getDispatcherServletPath();

}
