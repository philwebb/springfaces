package org.springframework.springfaces.converter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;

/**
 * A {@link ConditionalGenericConverter} that converts a class to an array or collection of containing the values of the
 * enum. For example <tt>conversionService.convert(MyEnum.class, MyEnum[].class)</tt> will return the same result as
 * <tt>MyEnum.values()</tt>.
 * 
 * @author Phillip Webb
 */
public class ClassToEnumsConverter implements ConditionalGenericConverter {

	public Set<ConvertiblePair> getConvertibleTypes() {
		return new HashSet<GenericConverter.ConvertiblePair>(Arrays.asList(new ConvertiblePair(Class.class,
				Object[].class), new ConvertiblePair(Class.class, Collection.class)));

	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType.isMap()) {
			return false;
		}
		TypeDescriptor elementType = targetType.getElementTypeDescriptor();
		return elementType != null && Enum.class.isAssignableFrom(elementType.getType())
				&& Class.class.equals(sourceType.getType());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Class elementType = targetType.getElementTypeDescriptor().getType();
		Set enumSet = EnumSet.allOf(elementType);
		if (targetType.isArray()) {
			Object[] target = (Object[]) Array.newInstance(elementType, enumSet.size());
			return enumSet.toArray(target);
		}
		Collection target = CollectionFactory.createCollection(targetType.getType(), enumSet.size());
		target.addAll(enumSet);
		return target;
	}
}
