package org.springframework.springfaces.mvc.navigation.requestmapped;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.converter.FacesConverterId;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;

/**
 * Tests for {@link RequestMappedRedirectViewModelBuilder}.
 * 
 * @author Phillip Webb
 */
public class RequestMappedRedirectViewModelBuilderTest {

	@Mock
	private RequestMappedRedirectViewContext context;

	private Method handlerMethod;

	@Mock
	private NativeWebRequest nativeRequest;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	private RequestMappedRedirectViewModelBuilder builder;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		setHandlerMethod("method");
		FacesContext facesContext = mock(FacesContext.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequest()).willReturn(request);
		given(externalContext.getResponse()).willReturn(response);
		FacesContextSetter.setCurrentInstance(facesContext);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	private void setHandlerMethod(String methodName) {
		for (Method method : Methods.class.getMethods()) {
			if (method.getName().equals(methodName)) {
				handlerMethod = ReflectionUtils.findMethod(Methods.class, "ignore", Object.class, Locale.class,
						Resolvable.class);
				builder = new RequestMappedRedirectViewModelBuilder(context, handlerMethod);
				return;
			}
		}
		throw new IllegalStateException("Unable to find handler method " + methodName);
	}

	@Test
	public void shouldIgnoreAnnotations() throws Exception {
		testIgnoresAnnotation(FacesConverterId.class, false);
		testIgnoresAnnotation(RequestHeader.class, true);
		testIgnoresAnnotation(RequestBody.class, true);
		testIgnoresAnnotation(CookieValue.class, true);
		testIgnoresAnnotation(ModelAttribute.class, true);
		testIgnoresAnnotation(Value.class, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void testIgnoresAnnotation(Class<?> annotation, boolean expected) {
		MethodParameter methodParameter = mock(MethodParameter.class);
		Annotation annotationMock = mock(Annotation.class);
		given(annotationMock.annotationType()).willReturn((Class) annotation);
		given(methodParameter.getParameterAnnotations()).willReturn(new Annotation[] { annotationMock });
		given(methodParameter.getParameterType()).willReturn((Class) Object.class);
		assertEquals(expected, builder.isIgnored(nativeRequest, methodParameter));
	}

	@Test
	public void shouldIgnoreTypes() throws Exception {
		testIgnoresType(Object.class, false);
		testIgnoresType(WebRequest.class, true);
		testIgnoresType(ServletRequest.class, true);
		testIgnoresType(HttpServletRequest.class, true);
		testIgnoresType(MultipartRequest.class, true);
		testIgnoresType(ServletResponse.class, true);
		testIgnoresType(HttpSession.class, true);
		testIgnoresType(Principal.class, true);
		testIgnoresType(Locale.class, true);
		testIgnoresType(InputStream.class, true);
		testIgnoresType(Reader.class, true);
		testIgnoresType(OutputStream.class, true);
		testIgnoresType(Writer.class, true);
		testIgnoresType(Map.class, true);
		testIgnoresType(Model.class, true);
		testIgnoresType(SessionStatus.class, true);
		testIgnoresType(HttpEntity.class, true);
		testIgnoresType(Errors.class, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void testIgnoresType(Class<?> type, boolean expected) {
		MethodParameter methodParameter = mock(MethodParameter.class);
		given(methodParameter.getParameterAnnotations()).willReturn(new Annotation[] {});
		given(methodParameter.getParameterType()).willReturn((Class) type);
		assertEquals(expected, builder.isIgnored(nativeRequest, methodParameter));
	}

	@Test
	public void shouldIgnoreCertainParamters() throws Exception {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("p1", new Object());
		source.put("p2", Locale.UK);
		source.put("p3", new Resolvable("123"));
		WebArgumentResolver argumentResolver = new WebArgumentResolver() {
			public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest)
					throws Exception {
				if (Resolvable.class.equals(methodParameter.getParameterType())) {
					return null;
				}
				return UNRESOLVED;
			}
		};
		WebArgumentResolver[] resolvers = new WebArgumentResolver[] { argumentResolver };
		given(context.getCustomArgumentResolvers()).willReturn(resolvers);
		setHandlerMethod("ignore");
		Map<String, Object> model = builder.build(nativeRequest, source);
		assertTrue(model.isEmpty());
	}

	// FIXME remaining test

	public static class Resolvable extends BigDecimal {
		private static final long serialVersionUID = 1L;

		public Resolvable(String val) {
			super(val);
		}
	}

	public static class Methods {
		public void method() {

		}

		public void ignore(@CookieValue Object p1, Locale p2, Resolvable p3) {
		}
	}

}
