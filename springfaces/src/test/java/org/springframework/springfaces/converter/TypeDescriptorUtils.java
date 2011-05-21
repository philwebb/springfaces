package org.springframework.springfaces.converter;

import java.lang.reflect.Field;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

public class TypeDescriptorUtils {

	public static <T> TypeDescriptor forCollection(Class<?> collectionClass, Class<T> elementType) {
		Assert.notNull(collectionClass, "CollectionClass must not be null");
		Assert.notNull(elementType, "ElementType must not be null");
		try {
			TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(collectionClass);
			Field elementTypeField = TypeDescriptor.class.getDeclaredField("elementType");
			elementTypeField.setAccessible(true);
			elementTypeField.set(typeDescriptor, TypeDescriptor.valueOf(elementType));
			return typeDescriptor;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
