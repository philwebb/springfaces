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
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpEntity;
import org.springframework.springfaces.mvc.bind.ReverseDataBinder;
import org.springframework.springfaces.mvc.servlet.view.Bookmarkable;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableRedirectView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.FacesWebRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.View;

/**
 * Allows views of the form '@bean.method' to be resolved against {@link RequestMapping} annotated methods of
 * {@link Controller} beans.
 * 
 * @author Phillip Webb
 */
public class RequestMappedDestinationViewResolver implements DestinationViewResolver, ApplicationContextAware {

	// FIXME DC
	// FIXME needs a lot of tidy up

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

	private PathMatcher pathMatcher = new AntPathMatcher();

	private WebArgumentResolver[] customArgumentResolvers;

	private WebBindingInitializer webBindingInitializer;

	private ApplicationContext applicationContext;

	public View resolveDestination(Object destination, Locale locale) throws Exception {
		if (destination instanceof String && ((String) destination).startsWith("@")
				&& ((String) destination).contains(".")) {
			String[] splitDestination = ((String) destination).split("\\.");
			// FIXME do this better
			if (splitDestination.length == 2) {
				Object bean = applicationContext.getBean(splitDestination[0].substring(1));
				Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
				for (Method method : methods) {
					if (method.getName().equals(splitDestination[1])) {
						// FIXME this will not work if we have overloaded methods
						return new RequestMappedView(bean.getClass(), method);
					}
				}
			}
		}
		return null;
	}

	public PathMatcher getPathMatcher() {
		return pathMatcher;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setCustomArgumentResolvers(WebArgumentResolver[] customArgumentResolvers) {
		this.customArgumentResolvers = customArgumentResolvers;
	}

	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	public class RequestMappedView implements View, Bookmarkable {

		private Class<?> handlerType;
		private Method handlerMethod;
		private WebArgumentResolver[] customArgumentResolvers;
		private BookmarkableRedirectView redirectView;

		public RequestMappedView(Class<?> handlerType, Method handlerMethod) {
			Assert.notNull(handlerMethod, "HandlerMethod must not be null");
			Assert.notNull(handlerType, "HandlerType must not be null");
			this.handlerType = handlerType;
			this.handlerMethod = BridgeMethodResolver.findBridgedMethod(handlerMethod);
			// FIXME get dispather servlet URL
			this.redirectView = new BookmarkableRedirectView("/spring" + getUrl(), true);
		}

		private String getUrl() {
			RequestMapping methodRequestMapping = AnnotationUtils.findAnnotation(handlerMethod, RequestMapping.class);
			RequestMapping typeLevelRequestMapping = AnnotationUtils.findAnnotation(handlerType.getClass(),
					RequestMapping.class);
			Assert.state(methodRequestMapping != null, "The handler method must declare @RequestMapping annotation");
			Assert.state(methodRequestMapping.value().length == 1,
					"@RequestMapping must have a single value to be mapped to a URL");
			Assert.state(typeLevelRequestMapping == null || typeLevelRequestMapping.value().length == 1,
					"@RequestMapping on handler class must have a single value to be mapped to a URL");

			String url = typeLevelRequestMapping == null ? "" : typeLevelRequestMapping.value()[0];
			url = getPathMatcher().combine(url, methodRequestMapping.value()[0]);
			if (!url.startsWith("/")) {
				url = "/" + url;
			}
			// FIXME what about patterns?
			return url;
		}

		public String getContentType() {
			return redirectView.getContentType();
		}

		public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			redirectView.render(getRelevantModel(model), request, response);
		}

		public String getBookmarkUrl(Map<String, ?> model, HttpServletRequest request) throws IOException {
			return redirectView.getBookmarkUrl(getRelevantModel(model), request);
		}

		private Map<String, Object> getRelevantModel(Map<String, ?> model) {
			Map<String, Object> relevantModel = new HashMap<String, Object>();
			for (int i = 0; i < handlerMethod.getParameterTypes().length; i++) {
				MethodParameter methodParameter = new MethodParameter(handlerMethod, i);
				if (!isIgnored(methodParameter)) {
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
				// FIXME do we need to call @InitDataBinder methods
				WebDataBinder binder = new WebRequestDataBinder(value);
				WebRequest request = new FacesWebRequest(FacesContext.getCurrentInstance());
				if (webBindingInitializer != null) {
					webBindingInitializer.initBinder(binder, request);
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
				if (IGNORED_ANNOTATIONS.contains(annotation.getClass())) {
					return true;
				}
			}

			// Check for ignored types
			Class<?> parameterType = methodParameter.getParameterType();
			for (Class<?> ignoredType : IGNORED_TYPES) {
				if (ignoredType.isAssignableFrom(parameterType)) {
					return true;
				}
			}

			// Check if an argument resolver would deal with the parameter
			if (customArgumentResolvers != null) {
				NativeWebRequest webRequest = new FacesWebRequest(FacesContext.getCurrentInstance());
				for (WebArgumentResolver resolver : customArgumentResolvers) {
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
}
