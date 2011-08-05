package org.springframework.springfaces.converter;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * Tests for {@link ClassToBooleansConverter}.
 * 
 * @author Phillip Webb
 */
public class ClassToBooleansConverterTest {

	private GenericConversionService conversionService;

	@Before
	@SuppressWarnings("deprecation")
	public void setup() throws Exception {
		conversionService = ConversionServiceFactory.createDefaultConversionService();
		conversionService.addConverter(new ClassToBooleansConverter());
	}

	@Test
	public void shouldConvertToObjectArray() throws Exception {
		Boolean[] b = conversionService.convert(Boolean.class, Boolean[].class);
		assertTrue(Arrays.equals(new Boolean[] { false, true }, b));
	}

	@Test
	public void shouldConvertToPrimitiveArray() throws Exception {
		boolean[] b = conversionService.convert(Boolean.class, boolean[].class);
		assertTrue(Arrays.equals(new boolean[] { false, true }, b));
	}

	@Test
	public void shouldConvertToCollection() throws Exception {
		Object o = doConvert(collectionType(Collection.class));
		assertBooleans(o);
		assertTrue(o instanceof Collection);
	}

	@Test
	public void shouldConvertToSet() throws Exception {
		Object o = doConvert(collectionType(Set.class));
		assertBooleans(o);
		assertTrue(o instanceof Set);
	}

	@Test
	public void shouldConvertToList() throws Exception {
		Object o = doConvert(collectionType(List.class));
		assertBooleans(o);
		assertTrue(o instanceof List);
	}

	@Test
	public void shouldConvertToArrayList() throws Exception {
		Object o = doConvert(collectionType(ArrayList.class));
		assertBooleans(o);
		assertTrue(o instanceof ArrayList);
	}

	@Test
	public void shouldConvertFromPrimitives() throws Exception {
		conversionService.convert(Boolean.TYPE, TypeDescriptor.forObject(Boolean.TYPE), collectionType(Set.class));
	}

	@Test(expected = ConverterNotFoundException.class)
	public void shouldNotConvertToMap() throws Exception {
		doConvert(collectionType(Map.class));
	}

	private Object doConvert(TypeDescriptor targetType) {
		return conversionService.convert(Boolean.class, TypeDescriptor.forObject(Boolean.class), targetType);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void assertBooleans(Object o) {
		Boolean[] e = {};
		if (o instanceof Boolean[]) {
			e = (Boolean[]) o;
		} else {
			e = (Boolean[]) ((Collection) o).toArray(e);
		}
		assertTrue(Arrays.equals(new Boolean[] { false, true }, e));
	}

	private TypeDescriptor collectionType(Class<?> collectionType) {
		if (Map.class.isAssignableFrom(collectionType)) {
			return TypeDescriptor.map(collectionType, TypeDescriptor.valueOf(Boolean.class),
					TypeDescriptor.valueOf(Boolean.class));
		}
		return TypeDescriptor.collection(collectionType, TypeDescriptor.valueOf(Boolean.class));
	}

}
