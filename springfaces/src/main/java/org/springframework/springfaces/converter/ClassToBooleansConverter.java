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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;

/**
 * A {@link ConditionalGenericConverter} that converts a class to an array or collection containing the values
 * <tt>false</tt> and <tt>true</tt>.
 * 
 * @author Phillip Webb
 */
public class ClassToBooleansConverter implements ConditionalGenericConverter {

	// FIXME this is probably a bad idea, push into select items specific interface and stop trying to reuse Spring
	// converters

	public Set<ConvertiblePair> getConvertibleTypes() {
		return new HashSet<GenericConverter.ConvertiblePair>(Arrays.asList(new ConvertiblePair(Class.class,
				Object[].class), new ConvertiblePair(Class.class, Collection.class)));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType.isMap()) {
			return false;
		}
		TypeDescriptor elementTypeDescriptor = targetType.getElementTypeDescriptor();
		return elementTypeDescriptor != null && Boolean.class.isAssignableFrom(elementTypeDescriptor.getObjectType())
				&& Class.class.equals(sourceType.getType());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		TypeDescriptor elementType = targetType.getElementTypeDescriptor();
		if (targetType.isArray()) {
			return elementType.isPrimitive() ? new boolean[] { false, true } : new Boolean[] { false, true };
		}
		Collection target = CollectionFactory.createCollection(targetType.getObjectType(), 2);
		target.addAll(Arrays.asList(false, true));
		return target;
	}
}
