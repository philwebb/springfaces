package org.springframework.springfaces.mvc.navigation.requestmapped;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Creates a {@link RequestMappedRedirectView redirect} for destinations of the form <tt>"@method"</tt> or
 * <tt>"@bean.method"</tt> by inspecting {@link RequestMapping @RequestMapping} annotated methods of all MVC
 * {@link Controller Controllers}.
 * <p>
 * By default, any string destination prefixed with <tt>"@"</tt> will be resolved by this class (the prefix can be
 * {@link #setPrefix changed} if necessary). The destination should specify the name of the {@link Controller} method
 * that will be used create the redirect URL. If the name does not specify a bean then the
 * {@link SpringFacesContext#getHandler() current} handler will be used.
 * <p>
 * For example <tt>"@hotelsController.show"</tt> would resolve against the <tt>show()</tt> method of the
 * <tt>hotelsController</tt> bean. Where as <tt>"@search"</tt> would resolve against the <tt>search()</tt> method of the
 * current handler.
 * <p>
 * NOTE: The method that the destination references must be annotated with
 * <tt>{@link RequestMapping @RequestMapping}</tt>
 * <p>
 * Resolved destinations will expose model elements by inspecting arguments and annotations of the method in order to
 * create a complete and valid URL (see {@link RequestMappedRedirectView} for details).
 * 
 * @see RequestMappedRedirectView
 * 
 * @author Phillip Webb
 */
public class RequestMappedRedirectDestinationViewResolver implements DestinationViewResolver, ApplicationContextAware,
		RequestMappedRedirectViewContext {

	/**
	 * A cache of destination to {@link Method}s to save expensive reflection calls.
	 */
	private Map<String, Method> cachedDestinationMethods = new ConcurrentHashMap<String, Method>();

	private String dispatcherServletPath;

	private PathMatcher pathMatcher;

	private WebBindingInitializer webBindingInitializer;

	private WebArgumentResolver[] customArgumentResolvers;

	private ParameterNameDiscoverer parameterNameDiscoverer;

	private ApplicationContext applicationContext;

	private String prefix = "@";

	public ModelAndView resolveDestination(Object destination, Locale locale, SpringFacesModel model) throws Exception {
		if ((destination instanceof String) && ((String) destination).startsWith(prefix)) {
			try {
				return resolvePrefixedDestination(((String) destination).substring(prefix.length()), locale, model);
			} catch (RuntimeException e) {
				throw new IllegalStateException("Unable to resolve @RequestMapped view from destination '"
						+ destination + "' : " + e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * Resolve a {@link #setPrefix prefixed} destination.
	 * @param destination the destination (not including any prefix)
	 * @param locale the locale in which to resolve the view
	 * @param model the {@link SpringFacesModel} at the time the view was resolved.
	 * @return a resolved view
	 * @throws Exception if the view cannot be resolved
	 */
	private ModelAndView resolvePrefixedDestination(String destination, Locale locale, SpringFacesModel model)
			throws Exception {
		Object handler = resolveDestinationHandler(destination);
		Method method = cachedDestinationMethods.get(destination);
		if (method == null) {
			method = resolveDestinationMethod(handler, destination);
			cachedDestinationMethods.put(destination, method);
		}
		View view = createView(this, handler, method);
		return new ModelAndView(view, getPropagatedModel(view, model));
	}

	/**
	 * Factory method used to create the actual view once a handler and method have been resolved. The default
	 * implementation of this method returns a {@link RequestMappedRedirectView}.
	 * @param context the {@link RequestMappedRedirectViewContext context} for the created view
	 * @param handler the handler resolved from the destination
	 * @param method the handler method resolved from the destination
	 * @return A view instance
	 */
	protected View createView(RequestMappedRedirectViewContext context, Object handler, Method method) {
		return new RequestMappedRedirectView(context, handler, method);
	}

	/**
	 * Strategy method used to obtain the model that should be propagated to the resolved {@link ModelAndView}. By
	 * default this method returns all entries from the {@link SpringFacesModel}.
	 * @param view the {@link #createView created} view
	 * @param model the {@link SpringFacesModel} at the time the view was resolved.
	 * @return the model to be propagated
	 */
	protected Map<String, Object> getPropagatedModel(View view, SpringFacesModel model) {
		return (model == null ? null : model.asMap());
	}

	private Object resolveDestinationHandler(String destination) {
		int lastDot = destination.lastIndexOf(".");
		if (lastDot == -1) {
			Object handler = SpringFacesContext.getCurrentInstance(true).getController();
			Assert.state(handler != null, "Unable to locate SpringFaces MVC Controller");
			return handler;
		}
		return applicationContext.getBean(destination.substring(0, lastDot));
	}

	/**
	 * Obtain a {@link Method} for the specified destination.
	 * @param handler the handler
	 * @param destination the destination (excluding any prefix)
	 * @return a {@link Method} resolved from the destination
	 * @throws IllegalStateException if the destination cannot be resolved
	 */
	private Method resolveDestinationMethod(Object handler, String destination) {
		String handlerMethodName;
		int lastDot = destination.lastIndexOf(".");
		if (lastDot == -1) {
			Assert.state(handler != null, "Unable to locate SpringFaces MVC handler");
			handlerMethodName = destination;
		} else {
			handlerMethodName = destination.substring(lastDot + 1);
		}
		Assert.state(StringUtils.hasLength(handlerMethodName), "No method name specified as part of destination");
		Method handlerMethod = getHandlerMethod(handler, handlerMethodName);
		return handlerMethod;
	}

	/**
	 * Search the specified handler to find a {@link RequestMapping} annotated method by name.
	 * @param handler the handler
	 * @param handlerMethodName the name of the method to find
	 * @return the method
	 * @throws IllegalStateException on error locating the method
	 */
	private Method getHandlerMethod(Object handler, String handlerMethodName) {
		Method requestMappedMethod = null;
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(handler.getClass());
		for (Method method : methods) {
			if (method.getName().equals(handlerMethodName)
					&& (AnnotationUtils.findAnnotation(method, RequestMapping.class) != null)) {
				Assert.state(requestMappedMethod == null,
						"More than one @RequestMapping annotated method with the name '" + handlerMethodName
								+ "' exists in " + handler.getClass());
				requestMappedMethod = method;
			}
		}
		Assert.state(requestMappedMethod != null, "Unable to find @RequestMapping annotated method '"
				+ handlerMethodName + "' in " + handler.getClass());
		return requestMappedMethod;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * Set the prefix that indicates when this resolver will handle a destination. If not specified the default prefix
	 * of <tt>"@"</tt> will be used.
	 * @param prefix the destination prefix
	 */
	public void setPrefix(String prefix) {
		Assert.hasLength(prefix, "Prefix must not be empty");
		this.prefix = prefix;
	}

	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	/**
	 * Set the PathMatcher implementation to use for combining URL paths from registered URL patterns.
	 * @param pathMatcher The path matcher
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/**
	 * Set a custom WebArgumentResolvers to use for special method parameter types.
	 * <p>
	 * Such a custom WebArgumentResolver will kick in first, having a chance to resolve an argument value before the
	 * standard argument handling kicks in.
	 * @param argumentResolver the argument resolver
	 */
	public void setCustomArgumentResolver(WebArgumentResolver argumentResolver) {
		this.customArgumentResolvers = (new WebArgumentResolver[] { argumentResolver });
	}

	public WebArgumentResolver[] getCustomArgumentResolvers() {
		return this.customArgumentResolvers;
	}

	/**
	 * Set one or more custom WebArgumentResolvers to use for special method parameter types.
	 * <p>
	 * Any such custom WebArgumentResolver will kick in first, having a chance to resolve an argument value before the
	 * standard argument handling kicks in.
	 * @param argumentResolvers the argument resolvers
	 */
	public void setCustomArgumentResolvers(WebArgumentResolver[] argumentResolvers) {
		this.customArgumentResolvers = argumentResolvers;
	}

	public WebBindingInitializer getWebBindingInitializer() {
		return this.webBindingInitializer;
	}

	/**
	 * Specify a WebBindingInitializer which will apply pre-configured configuration to every DataBinder that is used.
	 * @param webBindingInitializer the web binding initializer
	 */
	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	public ParameterNameDiscoverer getParameterNameDiscoverer() {
		return this.parameterNameDiscoverer;
	}

	/**
	 * Set the ParameterNameDiscoverer to use for resolving method parameter names if needed (e.g. for default attribute
	 * names).
	 * <p>
	 * Default is a {@link org.springframework.core.LocalVariableTableParameterNameDiscoverer}.
	 * @param parameterNameDiscoverer the paramter name discoverer
	 */
	public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	public String getDispatcherServletPath() {
		return dispatcherServletPath;
	}

	/**
	 * Set the servlet path that should be used to access the dispatcher servlet. This path should start with a "/"
	 * character and include the path to the dipatcher servlet (but not any extra path information or a query string).
	 * When not specified the path will taken from current HTTP {@link HttpServletRequest#getServletPath() request}.
	 * @param dispatcherServletPath the dispatcher servlet path
	 */
	public void setDispatcherServletPath(String dispatcherServletPath) {
		this.dispatcherServletPath = dispatcherServletPath;
	}
}
