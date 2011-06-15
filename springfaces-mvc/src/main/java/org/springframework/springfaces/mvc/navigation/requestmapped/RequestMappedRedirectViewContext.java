package org.springframework.springfaces.mvc.navigation.requestmapped;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;

public class RequestMappedRedirectViewContext {

	private String dispatcherServletPath;

	private PathMatcher pathMatcher = new AntPathMatcher();

	private WebBindingInitializer webBindingInitializer;

	WebArgumentResolver[] customArgumentResolvers;

	private ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	public PathMatcher getPathMatcher() {
		return pathMatcher;
	}

	/**
	 * Set the PathMatcher implementation to use for matching URL paths against registered URL patterns.
	 * <p>
	 * Default is {@link org.springframework.util.AntPathMatcher}.
	 * @param pathMatcher The path matcher
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		Assert.notNull(pathMatcher, "PathMatcher must not be null");
		this.pathMatcher = pathMatcher;
	}

	public WebArgumentResolver[] getCustomArgumentResolvers() {
		return customArgumentResolvers;
	}

	/**
	 * Set a custom WebArgumentResolvers to use for special method parameter types.
	 * <p>
	 * Such a custom WebArgumentResolver will kick in first, having a chance to resolve an argument value before the
	 * standard argument handling kicks in.
	 * @param argumentResolvers the argument resolvers
	 */
	public void setCustomArgumentResolvers(WebArgumentResolver[] argumentResolvers) {
		this.customArgumentResolvers = argumentResolvers;
	}

	public WebBindingInitializer getWebBindingInitializer() {
		return webBindingInitializer;
	}

	/**
	 * Specify a WebBindingInitializer which will apply pre-configured configuration to every DataBinder that this
	 * controller uses.
	 * @param webBindingInitializer the web binding initializer
	 */
	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	public ParameterNameDiscoverer getParameterNameDiscoverer() {
		return parameterNameDiscoverer;
	}

	/**
	 * Set the ParameterNameDiscoverer to use for resolving method parameter names if needed (e.g. for default attribute
	 * names).
	 * <p>
	 * Default is a {@link org.springframework.core.LocalVariableTableParameterNameDiscoverer}.
	 * @param parameterNameDiscoverer the paramter name discoverer
	 */
	public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
		Assert.notNull(pathMatcher, "ParameterNameDiscoverer must not be null");
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	public String getDispatcherServletPath() {
		return dispatcherServletPath;
	}

	public void setDispatcherServletPath(String dispatcherServletPath) {
		this.dispatcherServletPath = dispatcherServletPath;
	}
}
