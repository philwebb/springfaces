/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.mvc.navigation.requestmapped;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;

/**
 * Provides context for {@link RequestMappedRedirectView} and {@link RequestMappedRedirectViewModelBuilder}.
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
	 * @return a parameter name discoverer or <tt>null</tt>
	 */
	public ParameterNameDiscoverer getParameterNameDiscoverer();

	/**
	 * Returns the servlet path that should be used to access the dispatcher servlet. If this method returns
	 * <tt>null</tt> the path should taken from current HTTP {@link HttpServletRequest#getServletPath() request}.
	 * @return the dispatcher servlet path or <tt>null</tt>
	 */
	public String getDispatcherServletPath();

}
