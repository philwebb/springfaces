package org.springframework.springfaces.message.resolver;

import java.util.EnumSet;

import org.springframework.context.MessageSource;

public class EnumMessageCodeResolver implements MessageCodeResolver<Enum<?>> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean canResolve(MessageSource messageSource, Class<?> type) {
		if (Enum.class.isAssignableFrom(type)) {
			EnumSet<?> enums = EnumSet.allOf((Class<Enum>) type);
			for (Enum e : enums) {
				if (MessageSourceUtils.containsCode(messageSource, getCode(e))) {
					return true;
				}
			}
		}
		return false;
	}

	public String resolve(MessageSource messageSource, Enum<?> object) {
		return getCode(object);
	}

	private String getCode(Enum<?> e) {
		return e.getClass().getName() + "." + e.toString();
	}

}
