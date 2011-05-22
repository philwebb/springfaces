package org.springframework.springfaces.mvc.navigation;

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
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.springfaces.mvc.servlet.view.Bookmarkable;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.View;

public class RequestMappedDestinationViewResolver implements DestinationViewResolver {

	private WebArgumentResolver[] customArgumentResolvers;

	public View resolveDestination(Object destination, Locale locale) throws Exception {
		if (destination instanceof String && ((String) destination).startsWith("@")) {
			// @bean.method
			// get bean, get method
			// create a view
		}

		// TODO Auto-generated method stub
		return null;
	}

	public static class RequestMappedView implements View, Bookmarkable {

		private static final Set<Class<?>> IGNORED_ANNOTATIONS;
		static {
			IGNORED_ANNOTATIONS = new LinkedHashSet<Class<?>>();
			IGNORED_ANNOTATIONS.add(RequestHeader.class);
			IGNORED_ANNOTATIONS.add(RequestBody.class);
			IGNORED_ANNOTATIONS.add(CookieValue.class);
			IGNORED_ANNOTATIONS.add(ModelAttribute.class);
			IGNORED_ANNOTATIONS.add(Value.class);
		}

		private static final Set<Class<?>> IGNORED_TYPES;
		static {
			IGNORED_TYPES = new LinkedHashSet<Class<?>>();
			IGNORED_TYPES.add(WebRequest.class);
			IGNORED_TYPES.add(ServletRequest.class);
			IGNORED_TYPES.add(MultipartRequest.class);
			IGNORED_TYPES.add(ServletResponse.class);
			IGNORED_TYPES.add(HttpSession.class);
			IGNORED_TYPES.add(Principal.class);
			IGNORED_TYPES.add(Locale.class);
			IGNORED_TYPES.add(InputStream.class);
			IGNORED_TYPES.add(Reader.class);
			IGNORED_TYPES.add(OutputStream.class);
			IGNORED_TYPES.add(Writer.class);
			IGNORED_TYPES.add(Map.class);
			IGNORED_TYPES.add(Model.class);
			IGNORED_TYPES.add(SessionStatus.class);
			IGNORED_TYPES.add(HttpEntity.class);
			IGNORED_TYPES.add(Errors.class);
		}

		private Method handlerMethod;

		public RequestMappedView(Method handlerMethod) {
			this.handlerMethod = handlerMethod;

		}

		public String getContentType() {
			// TODO Auto-generated method stub
			return null;
		}

		public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			// TODO Auto-generated method stub

		}

		public String getBookmarkUrl(Map<String, ?> model, HttpServletRequest request) throws IOException {

			// TODO Auto-generated method stub
			return null;
		}

		private Map<String, Object> getRelevantModel(Map<String, ?> model) {
			Map<String, Object> relevantModel = new HashMap<String, Object>();
			for (int i = 0; i < handlerMethod.getParameterTypes().length; i++) {
				MethodParameter methodParameter = new MethodParameter(handlerMethod, i);
				if (!isIgnored(methodParameter)) {
					PathVariable pathVariableAnnotation = methodParameter.getParameterAnnotation(PathVariable.class);
					RequestParam requestParamAnnotation = methodParameter.getParameterAnnotation(RequestParam.class);
					Assert.state(pathVariableAnnotation == null || requestParamAnnotation == null);
					if (pathVariableAnnotation != null) {
						addPathVariableMethodParameter(relevantModel, methodParameter, pathVariableAnnotation);
					} else {
						addMethodParameter(relevantModel, methodParameter, requestParamAnnotation);
					}
				}
			}
			return relevantModel;
		}

		private void addPathVariableMethodParameter(Map<String, Object> relevantModel, MethodParameter methodParameter,
				PathVariable pathVariableAnnotation) {
			// Path variable
		}

		private void addMethodParameter(Map<String, Object> relevantModel, MethodParameter methodParameter,
				RequestParam requestParamAnnotation) {
			// TODO Auto-generated method stub
			if (requestParamAnnotation == null) {
				if (BeanUtils.isSimpleProperty(methodParameter.getParameterType())) {
					// Simple types, get from the model and use
				} else {
					// Get by name, fallback to type
					// Reverse databind
					// add all to map
				}
			}
		}

		private boolean isIgnored(MethodParameter methodParameter) {
			for (Annotation annotation : methodParameter.getParameterAnnotations()) {
				if (IGNORED_ANNOTATIONS.contains(annotation.getClass())) {
					return true;
				}
			}
			Class<?> parameterType = methodParameter.getParameterType();
			for (Class<?> ignoredType : IGNORED_TYPES) {
				if (ignoredType.isAssignableFrom(parameterType)) {
					return true;
				}
			}
			// FIXME customerArgumentResolvers?
			return false;
		}
	}
}
