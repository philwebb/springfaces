package org.springframework.springfaces.mvc.navigation.requestmapped;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;

public class RequestMappedRedirectViewContext {

	private PathMatcher pathMatcher = new AntPathMatcher();

	private WebBindingInitializer webBindingInitializer;

	WebArgumentResolver[] customArgumentResolvers;

	public PathMatcher getPathMatcher() {
		return pathMatcher;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		Assert.notNull(pathMatcher, "PathMatcher must not be null");
		this.pathMatcher = pathMatcher;
	}

	public WebArgumentResolver[] getCustomArgumentResolvers() {
		return customArgumentResolvers;
	}

	public void setCustomArgumentResolvers(WebArgumentResolver[] customArgumentResolvers) {
		this.customArgumentResolvers = customArgumentResolvers;
	}

	public WebBindingInitializer getWebBindingInitializer() {
		return webBindingInitializer;
	}

	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}
}
