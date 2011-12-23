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
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		given(this.springFacesContext.getFacesContext()).willReturn(this.facesContext);
		given(this.facesContext.getApplication()).willReturn(this.application);
		given(this.application.createConverter("example")).willReturn(this.facesConverter);
		given(this.application.createConverter(ClassWithConverter.class)).willReturn(this.facesConverter);
		given(this.facesConverter.getAsObject(this.facesContext, null, this.source)).willReturn(this.converted);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldConvertStringToObject() throws Exception {
		Set<ConvertiblePair> types = this.converter.getConvertibleTypes();
		assertEquals(1, types.size());
		ConvertiblePair pair = types.iterator().next();
		assertEquals(String.class, pair.getSourceType());
		assertEquals(Object.class, pair.getTargetType());
	}

	@Test
	public void shouldNotMatchIfNotSpringFacesContext() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(null);
		assertFalse(this.converter.matches(this.sourceType, TypeDescriptor.valueOf(Object.class)));
	}

	@Test
	public void shouldMatchIfHasAnnotationOnField() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getFieldTypeDescriptor();
		assertTrue(this.converter.matches(this.sourceType, targetType));
	}

	@Test
	public void shouldMatchIfHasAnnotationOnMethod() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getMethodParamTypeDescriptor();
		assertTrue(this.converter.matches(this.sourceType, targetType));
	}

	@Test
	public void shouldMatchIfConverterExists() throws Exception {
		TypeDescriptor targetType = TypeDescriptor.valueOf(ClassWithConverter.class);
		assertTrue(this.converter.matches(this.sourceType, targetType));
	}

	@Test
	public void shouldNotMatchIfNoConverterExists() throws Exception {
		TypeDescriptor targetType = TypeDescriptor.valueOf(ClassWithoutConverter.class);
		assertFalse(this.converter.matches(this.sourceType, targetType));
	}

	@Test
	public void shouldConvertNull() throws Exception {
		assertNull(this.converter.convert(null, this.sourceType, TypeDescriptor.forObject(Object.class)));
	}

	@Test
	public void shouldConvertAnnotationOnField() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getFieldTypeDescriptor();
		assertEquals(this.converted, this.converter.convert(this.source, this.sourceType, targetType));
	}

	@Test
	public void shouldConvertAnnotationOnMethod() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getMethodParamTypeDescriptor();
		assertEquals(this.converted, this.converter.convert(this.source, this.sourceType, targetType));
	}

	@Test
	public void shouldConvertByType() throws Exception {
		TypeDescriptor targetType = TypeDescriptor.valueOf(ClassWithConverter.class);
		assertEquals(this.converted, this.converter.convert(this.source, this.sourceType, targetType));
	}

	@Test
	public void shouldThrowIfNoConverter() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getFieldTypeDescriptor();
		reset(this.application);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("No JSF converter located for ID 'example'");
		this.converter.convert(this.source, this.sourceType, targetType);
	}

	@Test
	public void shouldReThrowFacesException() throws Exception {
		TypeDescriptor targetType = AnnotatedClass.getFieldTypeDescriptor();
		reset(this.application);
		given(this.application.createConverter("example")).willThrow(new FacesException());
		this.thrown.expect(FacesException.class);
		this.converter.convert(this.source, this.sourceType, targetType);
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
