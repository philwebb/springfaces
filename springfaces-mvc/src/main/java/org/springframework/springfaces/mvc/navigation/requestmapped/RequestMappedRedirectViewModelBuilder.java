package org.springframework.springfaces.mvc.navigation.requestmapped;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.HttpEntity;
import org.springframework.springfaces.mvc.bind.ReverseDataBinder;
import org.springframework.springfaces.mvc.navigation.requestmapped.filter.AnnotationMethodParameterFilter;
import org.springframework.springfaces.mvc.navigation.requestmapped.filter.MethodParameterFilter;
import org.springframework.springfaces.mvc.navigation.requestmapped.filter.MethodParameterFilterChain;
import org.springframework.springfaces.mvc.navigation.requestmapped.filter.TypeMethodParameterFilter;
import org.springframework.springfaces.mvc.navigation.requestmapped.filter.WebArgumentResolverMethodParameterFilter;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.FacesWebRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;

/**
 * A builder class that can be used to create a model containing items that are relevant to the parameters of a handler
 * method.
 * 
 * @see #build(NativeWebRequest, Map)
 * 
 * @author Phillip Webb
 */
public class RequestMappedRedirectViewModelBuilder {

	/**
	 * Annotations that are handled natively my MVC and hence should be filtered.
	 */
	private static final AnnotationMethodParameterFilter ANNOTAION_FILTER = new AnnotationMethodParameterFilter(
			RequestHeader.class, RequestBody.class, CookieValue.class, ModelAttribute.class, Value.class);

	/**
	 * Types that are handled natively my MVC and hence should be filtered.
	 */
	private static final TypeMethodParameterFilter TYPE_FILTER = new TypeMethodParameterFilter(WebRequest.class,
			ServletRequest.class, MultipartRequest.class, ServletResponse.class, HttpSession.class, Principal.class,
			Locale.class, InputStream.class, Reader.class, OutputStream.class, Writer.class, Map.class, Model.class,
			SessionStatus.class, HttpEntity.class, Errors.class);

	private RequestMappedRedirectViewContext context;

	private Method handlerMethod;

	/**
	 * The complete set of filters that will be applied to the handler method parameters.
	 */
	private MethodParameterFilter methodParameterFilter;

	/**
	 * Create a new {@link RequestMappedRedirectViewModelBuilder}.
	 * @param context the context for redirect view
	 * @param handlerMethod the MVC handler method method
	 */
	public RequestMappedRedirectViewModelBuilder(RequestMappedRedirectViewContext context, Method handlerMethod) {
		Assert.notNull(context, "Context must not be null");
		Assert.notNull(handlerMethod, "HandlerMethod must not be null");
		this.context = context;
		this.handlerMethod = handlerMethod;
		this.methodParameterFilter = new MethodParameterFilterChain(ANNOTAION_FILTER, TYPE_FILTER,
				new WebArgumentResolverMethodParameterFilter(context.getCustomArgumentResolvers()));
	}

	/**
	 * Build a model form the specified source. The resulting model contains the elements from the source that are
	 * relevant to the parameters of the handler method. The following methods will be used to find items from source:
	 * <ul>
	 * <li>The name of the parameter (specified by an annotation or generated) will be used as the key to get an element
	 * from the source.</li>
	 * <li>The source will be searched for any values that are an instance of the parameter types. When this method is
	 * used the model must contain only a single entry of each type.</li>
	 * <ul>
	 * NOTE: When the method parameter is not a simple type it will be expanded into multiple model entries using a
	 * {@link ReverseDataBinder}.
	 * 
	 * @param request the current native web request
	 * @param source a map containing the source of items to add to the model
	 * @return a model containing items relevant to the handler method parameters.
	 */
	public Map<String, Object> build(NativeWebRequest request, Map<String, ?> source) {
		ParameterNameDiscoverer parameterNameDiscoverer = this.context.getParameterNameDiscoverer();
		if (parameterNameDiscoverer == null) {
			parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		}
		Map<String, Object> model = new HashMap<String, Object>();
		for (int i = 0; i < this.handlerMethod.getParameterTypes().length; i++) {
			MethodParameter methodParameter = new MethodParameter(this.handlerMethod, i);
			if (!isIgnored(request, methodParameter)) {
				methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
				PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
				RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
				if (pathVariable != null) {
					addToPathVariableModel(model, methodParameter, pathVariable, source);
				} else {
					addRequestParameterToModel(model, methodParameter, requestParam, source);
				}
			}
		}
		return model;
	}

	/**
	 * Determines if the specified method parameter should be ignored.
	 * @param request the current web request
	 * @param methodParameter the method parameter
	 * @return <tt>true</tt> if the parameter should be ignored.
	 */
	protected boolean isIgnored(NativeWebRequest request, MethodParameter methodParameter) {
		return this.methodParameterFilter.isFiltered(request, methodParameter);
	}

	/**
	 * Add a path variable to the mode.
	 * @param model the model being built
	 * @param methodParameter the method parameter
	 * @param pathVariable the path variable (never null)
	 * @param source the source data map
	 */
	private void addToPathVariableModel(Map<String, Object> model, MethodParameter methodParameter,
			PathVariable pathVariable, Map<String, ?> source) {
		String name = pathVariable.value();
		if (name.length() == 0) {
			name = methodParameter.getParameterName();
			assertHasName(name, methodParameter);
		}
		Object value = source.get(name);
		if (value == null) {
			Map.Entry<String, ?> entry = getMapEntryByType(source, methodParameter.getParameterType());
			value = entry.getValue();
		}
		addIfNotContainsKey(model, name, value);
	}

	/**
	 * @param model the model being built
	 * @param methodParameter the method parameter
	 * @param requestParam The {@link RequestParam} annotation or null;
	 * @param source the source data map
	 */
	private void addRequestParameterToModel(Map<String, Object> model, MethodParameter methodParameter,
			RequestParam requestParam, Map<String, ?> source) {
		// FIXME will throw even if not required
		String name;
		if (requestParam != null && StringUtils.hasLength(requestParam.value())) {
			name = requestParam.value();
		} else {
			name = methodParameter.getParameterName();
		}
		Object value = StringUtils.hasLength(name) ? source.get(name) : null;
		if (value == null) {
			Map.Entry<String, ?> entry = getMapEntryByType(source, methodParameter.getParameterType());
			value = entry.getValue();
		}
		if (BeanUtils.isSimpleProperty(methodParameter.getParameterType())) {
			assertHasName(name, methodParameter);
			addIfNotContainsKey(model, name, value);
		} else {
			WebDataBinder binder = new WebRequestDataBinder(value);
			WebRequest request = new FacesWebRequest(FacesContext.getCurrentInstance());
			if (this.context.getWebBindingInitializer() != null) {
				this.context.getWebBindingInitializer().initBinder(binder, request);
			}
			ReverseDataBinder reverseBinder = new ReverseDataBinder(binder);
			PropertyValues propertyValues = reverseBinder.reverseBind();
			for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
				addIfNotContainsKey(model, propertyValue.getName(), propertyValue.getValue());
			}
		}
	}

	private void assertHasName(String name, MethodParameter methodParameter) {
		Assert.state(StringUtils.hasLength(name), "No parameter name specified for argument of type ["
				+ methodParameter.getParameterType().getName()
				+ "], and no parameter name information found in class file either.");
	}

	private Entry<String, ?> getMapEntryByType(Map<String, ?> source, Class<?> type) {
		Map.Entry<String, ?> rtn = null;
		for (Map.Entry<String, ?> entry : source.entrySet()) {
			if (type.isInstance(entry.getValue())) {
				Assert.state(rtn == null, "Unable to find single unique value in model of type " + type.getName());
				rtn = entry;
			}
		}
		Assert.state(rtn != null, "Unable to find value in model of type " + type.getName());
		return rtn;
	}

	private void addIfNotContainsKey(Map<String, Object> model, String name, Object value) {
		if (value != null) {
			if (model.containsKey(name)) {
				return;
			}
			model.put(name, value);
		}
	}

}
