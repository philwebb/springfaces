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
 * Tests for {@link ClassToEnumsConverter}.
 * 
 * @author Phillip Webb
 */
public class ClassToEnumsConverterTest {

	private GenericConversionService conversionService;

	@Before
	@SuppressWarnings("deprecation")
	public void setup() throws Exception {
		conversionService = ConversionServiceFactory.createDefaultConversionService();
		conversionService.addConverter(new ClassToEnumsConverter());
	}

	@Test
	public void shouldConvertToArray() throws Exception {
		ExampleEnum[] e = conversionService.convert(ExampleEnum.class, ExampleEnum[].class);
		assertEnums(e);
	}

	@Test
	public void shouldConvertToCollection() throws Exception {
		Object o = doConvert(collectionType(Collection.class));
		assertEnums(o);
		assertTrue(o instanceof Collection);
	}

	@Test
	public void shouldConvertToSet() throws Exception {
		Object o = doConvert(collectionType(Set.class));
		assertEnums(o);
		assertTrue(o instanceof Set);
	}

	@Test
	public void shouldConvertToList() throws Exception {
		Object o = doConvert(collectionType(List.class));
		assertEnums(o);
		assertTrue(o instanceof List);
	}

	@Test
	public void shouldConvertToArrayList() throws Exception {
		Object o = doConvert(collectionType(ArrayList.class));
		assertEnums(o);
		assertTrue(o instanceof ArrayList);
	}

	@Test(expected = ConverterNotFoundException.class)
	public void shouldNotConvertToMap() throws Exception {
		doConvert(collectionType(Map.class));
	}

	private Object doConvert(TypeDescriptor targetType) {
		return conversionService.convert(ExampleEnum.class, TypeDescriptor.forObject(ExampleEnum.class), targetType);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void assertEnums(Object o) {
		ExampleEnum[] e = {};
		if (o instanceof ExampleEnum[]) {
			e = (ExampleEnum[]) o;
		} else {
			e = (ExampleEnum[]) ((Collection) o).toArray(e);
		}
		assertTrue(Arrays.equals(ExampleEnum.values(), e));
	}

	private TypeDescriptor collectionType(Class<?> collectionType) {
		if (Map.class.isAssignableFrom(collectionType)) {
			return TypeDescriptor.map(collectionType, TypeDescriptor.valueOf(ExampleEnum.class),
					TypeDescriptor.valueOf(ExampleEnum.class));
		}
		return TypeDescriptor.collection(collectionType, TypeDescriptor.valueOf(ExampleEnum.class));
	}

	private enum ExampleEnum {
		ONE, TWO, THREE, FOUR
	}
}
