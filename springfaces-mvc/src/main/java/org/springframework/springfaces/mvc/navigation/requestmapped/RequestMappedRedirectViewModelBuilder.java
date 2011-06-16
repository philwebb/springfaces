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

public class RequestMappedRedirectViewModelBuilder {

	public static final AnnotationMethodParameterFilter ANNOTAION_FILTER = new AnnotationMethodParameterFilter(
			RequestHeader.class, RequestBody.class, CookieValue.class, ModelAttribute.class, Value.class);

	public static final TypeMethodParameterFilter TYPE_FILTER = new TypeMethodParameterFilter(WebRequest.class,
			ServletRequest.class, MultipartRequest.class, ServletResponse.class, HttpSession.class, Principal.class,
			Locale.class, InputStream.class, Reader.class, OutputStream.class, Writer.class, Map.class, Model.class,
			SessionStatus.class, HttpEntity.class, Errors.class);

	private RequestMappedRedirectViewContext context;

	private Method handlerMethod;

	private MethodParameterFilterChain methodParameterFilter;

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
	 * Build a model form the specified source. The resulting model will be relevant to the parameters of the handler
	 * method.
	 * @param source
	 * @return
	 */
	// FIXME DC by name falling back to type
	public Map<String, Object> buildModel(NativeWebRequest request, Map<String, ?> source) {
		ParameterNameDiscoverer parameterNameDiscoverer = context.getParameterNameDiscoverer();
		if (parameterNameDiscoverer == null) {
			parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		}
		Map<String, Object> model = new HashMap<String, Object>();
		for (int i = 0; i < handlerMethod.getParameterTypes().length; i++) {
			MethodParameter methodParameter = new MethodParameter(handlerMethod, i);
			if (!isIgnored(request, methodParameter)) {
				methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
				PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
				RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
				if (pathVariable != null) {
					addToPathVariableModel(model, methodParameter, pathVariable, source);
				} else {
					addRequestParamterToModel(model, methodParameter, requestParam, source);
				}
			}
		}
		return model;
	}

	protected boolean isIgnored(NativeWebRequest request, MethodParameter methodParameter) {
		return methodParameterFilter.isFiltered(request, methodParameter);
	}

	private void addToPathVariableModel(Map<String, Object> model, MethodParameter methodParameter,
			PathVariable pathVariable, Map<String, ?> source) {
		String name = pathVariable.value();
		if (name.length() == 0) {
			name = getRequiredParameterName(methodParameter);
		}
		addIfNotContainsKey(model, name, source.get(name));
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

	private void addRequestParamterToModel(Map<String, Object> model, MethodParameter methodParameter,
			RequestParam requestParam, Map<String, ?> source) {
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
			addIfNotContainsKey(model, name, value);
		} else {
			WebDataBinder binder = new WebRequestDataBinder(value);
			WebRequest request = new FacesWebRequest(FacesContext.getCurrentInstance());
			if (context.getWebBindingInitializer() != null) {
				context.getWebBindingInitializer().initBinder(binder, request);
			}
			ReverseDataBinder reverseBinder = new ReverseDataBinder(binder);
			PropertyValues propertyValues = reverseBinder.reverseBind();
			for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
				addIfNotContainsKey(model, propertyValue.getName(), propertyValue.getValue());
			}
		}
	}

	private Entry<String, ?> getMapEntryByType(Map<String, ?> source, Class<?> type) {
		Map.Entry<String, ?> rtn = null;
		for (Map.Entry<String, ?> entry : source.entrySet()) {
			if (type.isInstance(entry.getValue())) {
				Assert.state(rtn == null, "Unable to find single unique value in model of type " + type);
				rtn = entry;
			}
		}
		Assert.state(rtn != null, "Unable to find value in model of type " + type);
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
