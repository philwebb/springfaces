package org.springframework.springfaces.mvc.navigation.requestmapped;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.springfaces.mvc.converter.FacesConverterId;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.NativeWebRequest;

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
	private NativeWebRequest request;

	private RequestMappedRedirectViewModelBuilder builder;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		handlerMethod = getClass().getMethod("method");
		builder = new RequestMappedRedirectViewModelBuilder(context, handlerMethod);
	}

	public void method() {
	}

	@Test
	public void shouldFilterAnnotations() throws Exception {
		doTestAnnotationFilter(FacesConverterId.class, false);
		doTestAnnotationFilter(RequestHeader.class, true);
		doTestAnnotationFilter(RequestBody.class, true);
		doTestAnnotationFilter(CookieValue.class, true);
		doTestAnnotationFilter(ModelAttribute.class, true);
		doTestAnnotationFilter(Value.class, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doTestAnnotationFilter(Class<?> annotation, boolean expected) {
		MethodParameter methodParameter = mock(MethodParameter.class);
		Annotation annotationMock = mock(Annotation.class);
		given(annotationMock.annotationType()).willReturn((Class) annotation);
		given(methodParameter.getParameterAnnotations()).willReturn(new Annotation[] { annotationMock });
		given(methodParameter.getParameterType()).willReturn((Class) Object.class);
		assertEquals(expected, builder.isIgnored(request, methodParameter));
	}

	// FIXME remaining test

}
