package org.springframework.springfaces.mvc.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;

import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;

/**
 * Tests for {@link GenericFacesConverter}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericFacesConverterTest {

	private GenericFacesConverter converter = new GenericFacesConverter();

	@Mock
	private SpringFacesContext springFacesContext;

	@Mock
	private FacesContext facesContext;

	@Mock
	private Application application;

	@Mock
	private Converter facesConverter;

	private TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);

	private String source = "test";

	private Object converted = new Object();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setupMocks() {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		given(springFacesContext.getFacesContext()).willReturn(facesContext);
		given(facesContext.getApplication()).willReturn(application);
		given(application.createConverter("example")).willReturn(facesConverter);
		given(application.createConverter(ClassWithConverter.class)).willReturn(facesConverter);
		given(facesConverter.getAsObject(facesContext, null, source)).willReturn(converted);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldConvertStringToObject() throws Exception {
		Set<ConvertiblePair> types = converter.getConvertibleTypes();
		assertEquals(1, types.size());
		ConvertiblePair pair = types.iterator().next();
		assertEquals(String.class, pair.getSourceType());
		assertEquals(Object.class, pair.getTargetType());
	}

	@Test
	public void shouldNotMatchIfNotSpringFacesContext() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(null);
		assertFalse(converter.matches(sourceType, TypeDescriptor.valueOf(Object.class)));
	}

	@Test
	public void shouldMatchIfHasAnnotationOnField() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getFieldTypeDescriptor();
		assertTrue(converter.matches(sourceType, targetType));
	}

	@Test
	public void shouldMatchIfHasAnnotationOnMethod() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getMethodParamTypeDescriptor();
		assertTrue(converter.matches(sourceType, targetType));
	}

	@Test
	public void shouldMatchIfConverterExists() throws Exception {
		TypeDescriptor targetType = TypeDescriptor.valueOf(ClassWithConverter.class);
		assertTrue(converter.matches(sourceType, targetType));
	}

	@Test
	public void shouldNotMatchIfNoConverterExists() throws Exception {
		TypeDescriptor targetType = TypeDescriptor.valueOf(ClassWithoutConverter.class);
		assertFalse(converter.matches(sourceType, targetType));
	}

	@Test
	public void shouldConvertNull() throws Exception {
		assertNull(converter.convert(null, sourceType, TypeDescriptor.forObject(Object.class)));
	}

	@Test
	public void shouldConvertAnnotationOnField() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getFieldTypeDescriptor();
		assertEquals(converted, converter.convert(source, sourceType, targetType));
	}

	@Test
	public void shouldConvertAnnotationOnMethod() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getMethodParamTypeDescriptor();
		assertEquals(converted, converter.convert(source, sourceType, targetType));
	}

	@Test
	public void shouldConvertByType() throws Exception {
		TypeDescriptor targetType = TypeDescriptor.valueOf(ClassWithConverter.class);
		assertEquals(converted, converter.convert(source, sourceType, targetType));
	}

	@Test
	public void shouldThrowIfNoConverter() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getFieldTypeDescriptor();
		reset(application);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No JSF converter located for ID 'example'");
		converter.convert(source, sourceType, targetType);
	}

	@Test
	public void shouldReThrowFacesException() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getFieldTypeDescriptor();
		reset(application);
		given(application.createConverter("example")).willThrow(new FacesException());
		thrown.expect(FacesException.class);
		converter.convert(source, sourceType, targetType);
	}

	@SuppressWarnings("unused")
	private static class AnnotatedClass {

		@FacesConverterId("example")
		public Object field;

		public void method(@FacesConverterId("example") Object param) {

		}

		public static TypeDescriptor getFieldTypeDescriptor() throws Exception {
			return new TypeDescriptor(AnnotatedClass.class.getField("field"));
		}

		public static TypeDescriptor getMethodParamTypeDescriptor() throws Exception {
			return new TypeDescriptor(new MethodParameter(AnnotatedClass.class.getMethod("method", Object.class), 0));
		}
	}

	public static class ClassWithConverter {
	}

	public static class ClassWithoutConverter {
	}
}
