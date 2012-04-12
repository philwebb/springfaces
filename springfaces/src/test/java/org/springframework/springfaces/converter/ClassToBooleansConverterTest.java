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
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * Tests for {@link ClassToBooleansConverter}.
 * 
 * @author Phillip Webb
 */
public class ClassToBooleansConverterTest {

	private GenericConversionService conversionService;

	@Before
	public void setup() throws Exception {
		this.conversionService = new DefaultConversionService();
		this.conversionService.addConverter(new ClassToBooleansConverter());
	}

	@Test
	public void shouldConvertToObjectArray() throws Exception {
		Boolean[] b = this.conversionService.convert(Boolean.class, Boolean[].class);
		assertTrue(Arrays.equals(new Boolean[] { false, true }, b));
	}

	@Test
	public void shouldConvertToPrimitiveArray() throws Exception {
		boolean[] b = this.conversionService.convert(Boolean.class, boolean[].class);
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
		this.conversionService.convert(Boolean.TYPE, TypeDescriptor.forObject(Boolean.TYPE), collectionType(Set.class));
	}

	@Test(expected = ConverterNotFoundException.class)
	public void shouldNotConvertToMap() throws Exception {
		doConvert(collectionType(Map.class));
	}

	private Object doConvert(TypeDescriptor targetType) {
		return this.conversionService.convert(Boolean.class, TypeDescriptor.forObject(Boolean.class), targetType);
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
