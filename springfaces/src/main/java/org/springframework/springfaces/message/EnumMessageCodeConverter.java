package org.springframework.springfaces.message;

import java.util.EnumSet;

import org.springframework.context.MessageSource;

public class EnumMessageCodeConverter implements MessageCodeConverter<Enum<?>> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean canConvert(MessageSource messageSource, Class<?> type) {
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

	public String convert(MessageSource messageSource, Enum<?> object) {
		return getCode(object);
	}

	private String getCode(Enum<?> e) {
		return e.getClass().getName() + "." + e.toString();
	}

}
