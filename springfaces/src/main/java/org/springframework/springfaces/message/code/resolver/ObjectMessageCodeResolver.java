package org.springframework.springfaces.message.code.resolver;

import java.util.Collections;
import java.util.List;

/**
 * A {@link MessageCodeResolver} that supports all {@link Object} types. The code is constructed using the fully
 * qualified name of the class. For example {@link java.lang.annotation.AnnotationFormatError} would resolve to the code
 * <tt>java.lang.annotation.AnnotationFormatError</tt>
 * 
 * @author Phillip Webb
 */
public class ObjectMessageCodeResolver implements MessageCodeResolver<Object> {

	public List<String> getMessageCodesForObject(Object object) {
		return getMessageCodesForType(object.getClass());
	}

	public List<String> getMessageCodesForType(Class<? extends Object> type) {
		return Collections.singletonList(type.getName());
	}
}
