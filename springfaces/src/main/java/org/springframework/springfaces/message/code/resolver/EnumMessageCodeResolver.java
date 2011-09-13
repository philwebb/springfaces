package org.springframework.springfaces.message.code.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * A {@link MessageCodeResolver} that supports {@link Enum} types. The code is constructed using the fully qualified
 * name of the class combined with the Enum String value. For example {@link java.lang.annotation.ElementType#FIELD}
 * would resolve to the code <tt>java.lang.annotation.ElementType.FIELD</tt>
 * 
 * @author Phillip Webb
 */
public class EnumMessageCodeResolver implements MessageCodeResolver<Enum<?>> {

	public List<String> getMessageCodesForObject(Enum<?> object) {
		String code = object.getClass().getName() + "." + object.toString();
		return Collections.singletonList(code);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getMessageCodesForType(Class<? extends Enum<?>> type) {
		List<String> codes = new ArrayList<String>();
		EnumSet<?> enums = EnumSet.allOf((Class) type);
		for (Enum<?> e : enums) {
			codes.addAll(getMessageCodesForObject(e));
		}
		return codes;
	}

}
