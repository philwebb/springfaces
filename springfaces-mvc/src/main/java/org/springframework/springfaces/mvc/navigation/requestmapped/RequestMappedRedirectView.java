package org.springframework.springfaces.mvc.navigation.requestmapped;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpEntity;
import org.springframework.springfaces.mvc.bind.ReverseDataBinder;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableRedirectView;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.FacesWebRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.view.AbstractView;

/**
 * A {@link BookmarkableView} that redirects to a URL built dynamically from {@link RequestMapping} annotated
 * {@link Controller} methods. URLs are built by inspecting values of the {@link RequestMapping} annotation along with
 * any method parameters.
 * <p>
 * For example, given the following controller:
 * 
 * <pre>
 * @RequestMapping('/hotel')
 * public class HotelsController {
 *   @RequestMapping('/search')
 *   public void search(String s) {
 *     //...
 *   }
 * }
 * </pre>
 * 
 * A <tt>RequestMappedRedirectView</tt> for the <tt>search</tt> method would create the URL
 * <tt>/springdispatch/hotel/search?s=spring+jsf</tt>.
 * <p>
 * Method parameters are resolved against the <tt>model</tt>, in the example above the model contains the entry
 * <tt>s="spring jsf"</tt>. As well as simple data types, method parameters can also reference any object that
 * {@link DataBinder} supports. The model will also be referenced when resolving URI path template variables (for
 * example <tt>/show/{id}</tt>).
 * <p>
 * There are several limitations to the types of methods that can be used with this view, namely:
 * <ul>
 * <li>The {@link RequestMapping} must contain only a single <tt>value</tt></li>
 * <li>Paths should not contain wildcards (<tt>"*"</tt>, <tt>"?"</tt>, etc)</li>
 * <li>Custom {@link InitBinder} annotationed methods of the controller will not be called</li>
 * </ul>
 * 
 * 
 * @see RequestMappedRedirectDestinationViewResolver
 * @see RequestMappedRedirectViewContext
 * @see ReverseDataBinder
 * 
 * @author Phillip Webb
 */
public class RequestMappedRedirectView implements BookmarkableView {

	/**
	 * Annotations that are implicitly supported by MVC and hence indicate that a method parameter can be ignored by us.
	 */
	private static final Set<Class<?>> IGNORED_METHOD_PARAM_ANNOTATIONS;
	static {
		IGNORED_METHOD_PARAM_ANNOTATIONS = new LinkedHashSet<Class<?>>();
		IGNORED_METHOD_PARAM_ANNOTATIONS.add(RequestHeader.class);
		IGNORED_METHOD_PARAM_ANNOTATIONS.add(RequestBody.class);
		IGNORED_METHOD_PARAM_ANNOTATIONS.add(CookieValue.class);
		IGNORED_METHOD_PARAM_ANNOTATIONS.add(ModelAttribute.class);
		IGNORED_METHOD_PARAM_ANNOTATIONS.add(Value.class);
	}

	/**
	 * Types that are implicitly supported by MVC and hence indicate that a method parameter can be ignored by us.
	 */
	private static final Set<Class<?>> IGNORED_METHOD_PARAM_TYPES;
	static {
		IGNORED_METHOD_PARAM_TYPES = new LinkedHashSet<Class<?>>();
		IGNORED_METHOD_PARAM_TYPES.add(WebRequest.class);
		IGNORED_METHOD_PARAM_TYPES.add(ServletRequest.class);
		IGNORED_METHOD_PARAM_TYPES.add(MultipartRequest.class);
		IGNORED_METHOD_PARAM_TYPES.add(ServletResponse.class);
		IGNORED_METHOD_PARAM_TYPES.add(HttpSession.class);
		IGNORED_METHOD_PARAM_TYPES.add(Principal.class);
		IGNORED_METHOD_PARAM_TYPES.add(Locale.class);
		IGNORED_METHOD_PARAM_TYPES.add(InputStream.class);
		IGNORED_METHOD_PARAM_TYPES.add(Reader.class);
		IGNORED_METHOD_PARAM_TYPES.add(OutputStream.class);
		IGNORED_METHOD_PARAM_TYPES.add(Writer.class);
		IGNORED_METHOD_PARAM_TYPES.add(Map.class);
		IGNORED_METHOD_PARAM_TYPES.add(Model.class);
		IGNORED_METHOD_PARAM_TYPES.add(SessionStatus.class);
		IGNORED_METHOD_PARAM_TYPES.add(HttpEntity.class);
		IGNORED_METHOD_PARAM_TYPES.add(Errors.class);
	}

	/**
	 * Context for the view
	 */
	private RequestMappedRedirectViewContext context;

	/**
	 * The MVC handler being referenced
	 */
	private Object handler;

	/**
	 * The MVC handler method being referenced
	 */
	private Method handlerMethod;

	/**
	 * Create a new {@link RequestMappedRedirectView}.
	 * @param context the context for redirect view
	 * @param handler the MVC handler
	 * @param handlerMethod the MVC handler method that should be used to generate the redirect URL
	 */
	public RequestMappedRedirectView(RequestMappedRedirectViewContext context, Object handler, Method handlerMethod) {
		Assert.notNull(context, "Context must not be null");
		Assert.notNull(handlerMethod, "HandlerMethod must not be null");
		Assert.notNull(handler, "Handler must not be null");
		this.context = context;
		this.handler = handler;
		this.handlerMethod = BridgeMethodResolver.findBridgedMethod(handlerMethod);
	}

	public String getContentType() {
		return AbstractView.DEFAULT_CONTENT_TYPE;
	}

	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BookmarkableRedirectView delegate = createRedirectDelegate(request);
		Map<String, Object> relevantModel = getRelevantModel(model);
		delegate.render(relevantModel, request, response);
	}

	public String getBookmarkUrl(Map<String, ?> model, HttpServletRequest request) throws IOException {
		BookmarkableRedirectView delegate = createRedirectDelegate(request);
		Map<String, Object> relevantModel = getRelevantModel(model);
		return delegate.getBookmarkUrl(relevantModel, request);
	}

	/**
	 * Factory method that creates a {@link BookmarkableRedirectView} used as a delegate to perform the actual redirect.
	 * @param request the servlet request
	 * @return a {@link BookmarkableRedirectView}
	 */
	private BookmarkableRedirectView createRedirectDelegate(HttpServletRequest request) {
		String servletPath = context.getDispatcherServletPath();
		if (servletPath == null) {
			servletPath = request.getServletPath();
		}
		String url = buildRedirectUrl();
		return new BookmarkableRedirectView(servletPath + url, true);
	}

	/**
	 * Builds a redirect URL based on the handler method
	 * @return a redirect URL
	 */
	private String buildRedirectUrl() {
		RequestMapping methodRequestMapping = AnnotationUtils.findAnnotation(handlerMethod, RequestMapping.class);
		RequestMapping typeLevelRequestMapping = AnnotationUtils.findAnnotation(handler.getClass(),
				RequestMapping.class);
		Assert.state(methodRequestMapping != null, "The handler method must declare @RequestMapping annotation");
		Assert.state(methodRequestMapping.value().length == 1,
				"@RequestMapping must have a single value to be mapped to a URL");
		Assert.state(typeLevelRequestMapping == null || typeLevelRequestMapping.value().length == 1,
				"@RequestMapping on handler class must have a single value to be mapped to a URL");
		String url = typeLevelRequestMapping == null ? "" : typeLevelRequestMapping.value()[0];
		url = context.getPathMatcher().combine(url, methodRequestMapping.value()[0]);
		if (!url.startsWith("/")) {
			url = "/" + url;
		}
		return url;
	}

	private Map<String, Object> getRelevantModel(Map<String, ?> model) {
		Map<String, Object> relevantModel = new HashMap<String, Object>();
		for (int i = 0; i < handlerMethod.getParameterTypes().length; i++) {
			MethodParameter methodParameter = new MethodParameter(handlerMethod, i);
			if (!isIgnored(methodParameter)) {
				methodParameter.initParameterNameDiscovery(context.getParameterNameDiscoverer());
				PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
				RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
				if (pathVariable != null) {
					addToRelevantModel(relevantModel, methodParameter, pathVariable, model);
				} else {
					addToRelevantModel(relevantModel, methodParameter, requestParam, model);
				}
			}
		}
		return relevantModel;
	}

	private void addToRelevantModel(Map<String, Object> relevantModel, MethodParameter methodParameter,
			PathVariable pathVariable, Map<String, ?> model) {
		String name = pathVariable.value();
		if (name.length() == 0) {
			name = getRequiredParameterName(methodParameter);
		}
		addIfPossible(relevantModel, name, model.get(name));
	}

	private void addToRelevantModel(Map<String, Object> relevantModel, MethodParameter methodParameter,
			RequestParam requestParam, Map<String, ?> model) {
		String name;
		if (requestParam != null && StringUtils.hasLength(requestParam.value())) {
			name = requestParam.value();
		} else {
			name = methodParameter.getParameterName();
		}

		Object value = StringUtils.hasLength(name) ? model.get(name) : null;
		if (value == null) {
			Map.Entry<String, ?> entry = getMapEntryByType(model, methodParameter.getParameterType());
			name = entry.getKey();
			value = entry.getValue();
		}
		if (BeanUtils.isSimpleProperty(methodParameter.getParameterType())) {
			addIfPossible(relevantModel, name, value);
		} else {
			WebDataBinder binder = new WebRequestDataBinder(value);
			WebRequest request = new FacesWebRequest(FacesContext.getCurrentInstance());
			if (context.getWebBindingInitializer() != null) {
				context.getWebBindingInitializer().initBinder(binder, request);
			}
			ReverseDataBinder reverseBinder = new ReverseDataBinder(binder);
			PropertyValues propertyValues = reverseBinder.reverseBind();
			for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
				addIfPossible(relevantModel, propertyValue.getName(), propertyValue.getValue());
			}
		}
	}

	private Entry<String, ?> getMapEntryByType(Map<String, ?> model, Class<?> type) {
		Map.Entry<String, ?> rtn = null;
		for (Map.Entry<String, ?> entry : model.entrySet()) {
			if (type.isInstance(entry.getValue())) {
				Assert.state(rtn == null, "Unable to find single unique value in model of type " + type);
				rtn = entry;
			}
		}
		Assert.state(rtn != null, "Unable to find value in model of type " + type);
		return rtn;
	}

	private void addIfPossible(Map<String, Object> model, String name, Object value) {
		if (value != null) {
			if (model.containsKey(name)) {
				return;
			}
			model.put(name, value);
		}
	}

	private String getRequiredParameterName(MethodParameter methodParam) {
		String name = methodParam.getParameterName();
		if (name == null) {
			throw new IllegalStateException("No parameter name specified for argument of type ["
					+ methodParam.getParameterType().getName()
					+ "], and no parameter name information found in class file either.");
		}
		return name;
	}

	private boolean isIgnored(MethodParameter methodParameter) {
		// Check for ignored annotations
		for (Annotation annotation : methodParameter.getParameterAnnotations()) {
			if (IGNORED_METHOD_PARAM_ANNOTATIONS.contains(annotation.getClass())) {
				return true;
			}
		}
		// Check for ignored types
		Class<?> parameterType = methodParameter.getParameterType();
		for (Class<?> ignoredType : IGNORED_METHOD_PARAM_TYPES) {
			if (ignoredType.isAssignableFrom(parameterType)) {
				return true;
			}
		}
		// Check if an argument resolver would deal with the parameter
		if (context.getCustomArgumentResolvers() != null) {
			NativeWebRequest webRequest = new FacesWebRequest(FacesContext.getCurrentInstance());
			for (WebArgumentResolver resolver : context.getCustomArgumentResolvers()) {
				try {
					if (resolver.resolveArgument(methodParameter, webRequest) != WebArgumentResolver.UNRESOLVED) {
						return true;
					}
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}
		// Not a parameter that we should ignore
		return false;
	}
}