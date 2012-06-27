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
package org.springframework.springfaces.mvc.navigation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.mvc.method.support.FacesContextMethodArgumentResolver;
import org.springframework.springfaces.mvc.method.support.SpringFacesModelMethodArgumentResolver;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Annotation for mapping JSF navigation onto specific navigation methods.
 * <p>
 * Navigation methods which are annotated with this annotation are allowed to have very flexible signatures. They may
 * have arguments of the following types, in arbitrary order:
 * <ul>
 * <li>{@link java.lang.String} for access to the JSF outcome that triggered the navigation.</li>
 * <li>{@link org.springframework.springfaces.mvc.navigation.NavigationContext} object for access to a navigation
 * context providing complete navigation details.</li>
 * <li>{@link javax.faces.component.UIComponent} object for access to the component that triggered the navigation.</li>
 * <li>{@link java.util.Map} / {@link org.springframework.ui.Model} / {@link org.springframework.ui.ModelMap} /
 * {@link org.springframework.springfaces.mvc.model.SpringFacesModel} for accessing the model used when rendering the
 * page that is triggering navigation.</li>
 * <li>Non-Simple Types. A convenient shortcut to access a single item from the {@link SpringFacesModel model}. Types
 * will only be resolved when the model contains only a single element of the given type (see
 * {@link SpringFacesModelMethodArgumentResolver} for details).</li>
 * <li>{@link javax.faces.context.FacesContext}, {@link javax.faces.context.ExternalContext} and other JSF objects.
 * Provides access to various JSF infrastructure classes. (See {@link FacesContextMethodArgumentResolver} for a complete
 * list).</li>
 * <li>Request and/or response objects (Servlet API). You may choose any specific request/response type, e.g.
 * {@link javax.servlet.ServletRequest} or {@link javax.servlet.http.HttpServletRequest}.</li>
 * <li>{@link javax.servlet.http.HttpSession} object. An argument of this type will enforce the presence of a
 * corresponding session.</li>
 * <li>{@link org.springframework.web.context.request.WebRequest} or
 * {@link org.springframework.web.context.request.NativeWebRequest}. Allows for generic request parameter access as well
 * as request/session attribute access, without ties to the native Faces API.</li>
 * <li>{@link java.util.Locale} for the current request locale (obtained from the {@link UIViewRoot view root}).</li>
 * <li>{@link java.io.InputStream} / {@link java.io.Reader} for access to the request's content. This will be the raw
 * InputStream/Reader as exposed by the JSF {@link ExternalContext}.</li>
 * <li>{@link java.io.OutputStream} / {@link java.io.Writer} for generating the response's content. This will be the raw
 * OutputStream/Writer as exposed by the JSF {@link ExternalContext}.</li>
 * <li>{@link RequestHeader @RequestHeader} annotated parameters for access to specific request HTTP headers. Parameter
 * values will be converted to the declared method argument type. Additionally, {@code @RequestHeader} can be used on a
 * {@link java.util.Map Map&lt;String, String&gt;}, {@link org.springframework.util.MultiValueMap
 * MultiValueMap&lt;String, String&gt;}, or {@link org.springframework.http.HttpHeaders HttpHeaders} method parameter to
 * gain access to all request headers.</li>
 * </ul>
 * <p>
 * The following return types are supported for navigation methods:
 * <ul>
 * <li>A {@link org.springframework.springfaces.mvc.navigation.NavigationOutcome} object containing a destination and an
 * optional implicit model. Destinations contained within the outcome should be {@linkplain DestinationViewResolver
 * resolvable}.</li>
 * <li>Any {@linkplain DestinationViewResolver resolvable} {@link java.lang.Object} destination or <tt>null</tt> to
 * re-render the current JSF view.</li>
 * <li>{@link ResponseBody @ResponseBody} annotated methods for access to the Servlet response HTTP contents. The return
 * value will be converted to the response stream using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}.</li>
 * <li>A {@link org.springframework.http.HttpEntity HttpEntity&lt;?&gt;} or
 * {@link org.springframework.http.ResponseEntity ResponseEntity&lt;?&gt;} object to access to the Servlet response HTTP
 * headers and contents. The entity body will be converted to the response stream using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}.</li>
 * <li><code>void</code> if the current JSF view should be re-rendered or if the method handles the response and has
 * called {@link FacesContext#responseComplete()}.
 * </ul>
 * <p>
 * <b>NOTE:</b> {@code @NavigationMapping}s placed on methods within a {@code @Controller} are only considered for
 * requests that originated from that controller. Use {@code @NavigationController} beans to define global mappings.
 * @see NavigationMethodOutcomeResolver
 * @author Phillip Webb
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface NavigationMapping {

	/**
	 * The JSF outcomes that this mapping will handle. When not specified the outcome will be derived from the method
	 * name. The prefix <tt>'on'</tt> from method names will not be be used when deriving the outcome name (for example
	 * <tt>onClick()</tt> will map to the outcome <tt>'click'</tt>).
	 * @return the mapped JSF outcomes or an empty array to derive the outcome from the method name
	 * @see NavigationContext#getOutcome()
	 */
	String[] value() default {};

	/**
	 * An option <tt>from-action</tt> restriction can be used to limit the mapping to only navigation occurring as the
	 * result of a specific JSF action. Values are usually specified here in form of an EL Expression (e.g.
	 * '#{bean.action}').
	 * @return The <tt>from-action</tt> restriction
	 * @see NavigationContext#getFromAction()
	 */
	String fromAction() default "";

	/**
	 * An optional filter that will be used to determine if the mapping should be considered. The specified filter class
	 * must have a public zero-argument constructor. Any {@link NavigationContext} that does not
	 * {@linkplain NavigationMappingFilter#matches(NavigationContext) match} the filter will not be mapped to the
	 * method.
	 * @return A filter implementation class
	 */
	Class<? extends NavigationMappingFilter> filter() default NavigationMappingFilter.class;
}
