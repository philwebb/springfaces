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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.HttpEntity;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.converter.FacesConverterId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;

/**
 * Tests for {@link RequestMappedRedirectViewModelBuilder}.
 * 
 * @author Phillip Webb
 */
public class RequestMappedRedirectViewModelBuilderTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

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
		given(externalContext.getRequest()).willReturn(this.request);
		given(externalContext.getResponse()).willReturn(this.response);
		FacesContextSetter.setCurrentInstance(facesContext);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	private void setHandlerMethod(String methodName) {
		for (Method method : Methods.class.getMethods()) {
			if (method.getName().equals(methodName)) {
				this.handlerMethod = method;
				this.builder = new RequestMappedRedirectViewModelBuilder(this.context, this.handlerMethod);
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
		assertEquals(expected, this.builder.isIgnored(this.nativeRequest, methodParameter));
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
		assertEquals(expected, this.builder.isIgnored(this.nativeRequest, methodParameter));
	}

	@Test
	public void shouldIgnoreParameters() throws Exception {
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
		given(this.context.getCustomArgumentResolvers()).willReturn(resolvers);
		setHandlerMethod("ignore");
		Map<String, Object> model = this.builder.build(this.nativeRequest, source);
		assertTrue(model.isEmpty());
	}

	@Test
	public void shouldAddPathVariables() throws Exception {
		Map<String, String> source = new HashMap<String, String>();
		source.put("pv1", "1");
		source.put("p2", "2");
		setHandlerMethod("pathVariable");
		Map<String, Object> model = this.builder.build(this.nativeRequest, source);
		assertEquals(2, model.size());
		assertEquals("1", model.get("pv1"));
		assertEquals("2", model.get("p2"));
	}

	@Test
	public void shouldAddPathVariablesByType() throws Exception {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("xa", new Long(1));
		source.put("xb", new Integer(2));
		setHandlerMethod("pathVariableByType");
		Map<String, Object> model = this.builder.build(this.nativeRequest, source);
		assertEquals(2, model.size());
		assertEquals(new Long(1), model.get("p1"));
		assertEquals(new Integer(2), model.get("p2"));
	}

	@Test
	public void shouldNeedAtLeastOneEntryAddingVariablesByType() throws Exception {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("xa", new Long(1));
		setHandlerMethod("pathVariableByType");
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to find value in model of type java.lang.Integer");
		this.builder.build(this.nativeRequest, source);
	}

	@Test
	public void shouldNeedOnlyOneEntryAddingVariablesByType() throws Exception {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("xa", new Long(1));
		source.put("xb", new Integer(2));
		source.put("xc", new Long(3));
		setHandlerMethod("pathVariableByType");
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to find single unique value in model of type java.lang.Long");
		this.builder.build(this.nativeRequest, source);
	}

	@Test
	public void shouldNeedPathVaraibleName() throws Exception {
		ParameterNameDiscoverer parameterNameDiscoverer = mock(ParameterNameDiscoverer.class);
		given(this.context.getParameterNameDiscoverer()).willReturn(parameterNameDiscoverer);
		given(parameterNameDiscoverer.getParameterNames(any(Method.class))).willReturn(null);
		Map<String, String> source = new HashMap<String, String>();
		source.put("pv1", "1");
		setHandlerMethod("pathVariable");
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("No parameter name specified for argument of type [java.lang.String], and "
				+ "no parameter name information found in class file either.");
		this.builder.build(this.nativeRequest, source);
	}

	@Test
	public void shouldAddRequestParams() throws Exception {
		Map<String, String> source = new HashMap<String, String>();
		source.put("pv1", "1");
		source.put("p2", "2");
		source.put("p3", "3");
		setHandlerMethod("requestParam");
		Map<String, Object> model = this.builder.build(this.nativeRequest, source);
		assertEquals(3, model.size());
		assertEquals("1", model.get("pv1"));
		assertEquals("2", model.get("p2"));
		assertEquals("3", model.get("p3"));
	}

	@Test
	public void shouldAddRequestParamsByType() throws Exception {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("xa", new Long(1));
		source.put("xb", new Integer(2));
		source.put("xc", new Byte((byte) 3));
		setHandlerMethod("requestParamByType");
		Map<String, Object> model = this.builder.build(this.nativeRequest, source);
		assertEquals(3, model.size());
		assertEquals(new Long(1), model.get("p1"));
		assertEquals(new Integer(2), model.get("p2"));
		assertEquals(new Byte((byte) 3), model.get("p3"));
	}

	@Test
	public void shouldMapComplexType() throws Exception {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("x", new ComplexType(1, 2L));
		setHandlerMethod("requestParamComplexType");
		Map<String, Object> model = this.builder.build(this.nativeRequest, source);
		assertEquals(2, model.size());
		assertEquals("1", model.get("a"));
		assertEquals("2", model.get("b"));
	}

	@Test
	public void shouldInitBinderForComplexType() throws Exception {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("x", new ComplexType(1, 2L));
		setHandlerMethod("requestParamComplexType");
		WebBindingInitializer webBindingInitializer = mock(WebBindingInitializer.class);
		given(this.context.getWebBindingInitializer()).willReturn(webBindingInitializer);
		this.builder.build(this.nativeRequest, source);
		verify(webBindingInitializer).initBinder(any(WebDataBinder.class), any(WebRequest.class));
	}

	@Test
	public void shouldNeedNameForSimpleRequestParamType() throws Exception {
		ParameterNameDiscoverer parameterNameDiscoverer = mock(ParameterNameDiscoverer.class);
		given(this.context.getParameterNameDiscoverer()).willReturn(parameterNameDiscoverer);
		given(parameterNameDiscoverer.getParameterNames(any(Method.class))).willReturn(null);
		Map<String, String> source = new HashMap<String, String>();
		source.put("xa", "1");
		setHandlerMethod("requestParamNotNamed");
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("No parameter name specified for argument of type [java.lang.String], and "
				+ "no parameter name information found in class file either.");
		this.builder.build(this.nativeRequest, source);
	}

	@Test
	public void shouldResolveByTypeWhenComplexTypeNotNamed() throws Exception {
		ParameterNameDiscoverer parameterNameDiscoverer = mock(ParameterNameDiscoverer.class);
		given(this.context.getParameterNameDiscoverer()).willReturn(parameterNameDiscoverer);
		given(parameterNameDiscoverer.getParameterNames(any(Method.class))).willReturn(null);
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("xa", new ComplexType(1, 2L));
		setHandlerMethod("requestParamComplexType");
		Map<String, Object> model = this.builder.build(this.nativeRequest, source);
		assertEquals(2, model.size());
		assertEquals("1", model.get("a"));
		assertEquals("2", model.get("b"));
	}

	// FIXME remaining test

	public static class Resolvable extends BigDecimal {
		private static final long serialVersionUID = 1L;

		public Resolvable(String val) {
			super(val);
		}
	}

	@Controller
	public static class Methods {
		public void method() {
		}

		@RequestMapping("/ignore")
		public void ignore(@CookieValue Object p1, Locale p2, Resolvable p3) {
		}

		@RequestMapping("/pathvariable")
		public void pathVariable(@PathVariable("pv1") String p1, @PathVariable String p2) {
		}

		@RequestMapping("/pathvariablebytype")
		public void pathVariableByType(@PathVariable Long p1, @PathVariable Integer p2) {
		}

		@RequestMapping("/requestparam")
		public void requestParam(@RequestParam("pv1") String p1, @RequestParam String p2, String p3) {
		}

		@RequestMapping("/requestparambytype")
		public void requestParamByType(@RequestParam Long p1, @RequestParam Integer p2, Byte p3) {
		}

		@RequestMapping("/requestparamcomplextype")
		public void requestParamComplexType(ComplexType p1) {
		}

		@RequestMapping("/requestparamnotnamed")
		public void requestParamNotNamed(String p1) {
		}
	}

	public static class ComplexType {
		private Integer a;
		private Long b;

		public ComplexType(Integer a, Long b) {
			super();
			this.a = a;
			this.b = b;
		}

		public Integer getA() {
			return this.a;
		}

		public void setA(Integer a) {
			this.a = a;
		}

		public Long getB() {
			return this.b;
		}

		public void setB(Long b) {
			this.b = b;
		}
	}

}
