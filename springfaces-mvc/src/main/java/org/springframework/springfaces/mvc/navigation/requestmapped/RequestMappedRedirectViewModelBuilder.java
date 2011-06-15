package org.springframework.springfaces.mvc.navigation.requestmapped;

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
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.springfaces.mvc.bind.ReverseDataBinder;
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
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.FacesWebRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;

class RequestMappedRedirectViewModelBuilder {

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

	private RequestMappedRedirectViewContext context;
	private Method handlerMethod;

	public RequestMappedRedirectViewModelBuilder(RequestMappedRedirectViewContext context, Method handlerMethod) {
		this.context = context;
		this.handlerMethod = handlerMethod;
	}

	public Map<String, Object> getRelevantModel(Map<String, ?> model) {
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
