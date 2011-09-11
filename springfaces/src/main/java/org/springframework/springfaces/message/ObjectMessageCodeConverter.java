package org.springframework.springfaces.message;

import org.springframework.context.MessageSource;

public class ObjectMessageCodeConverter implements MessageCodeConverter<Object> {

	public boolean canConvert(MessageSource messageSource, Class<?> type) {
		String code = getCode(type);
		return MessageSourceUtils.containsCode(messageSource, code);
	}

	public String convert(MessageSource messageSource, Object object) {
		return getCode(object.getClass());
	}

	protected String getCode(Class<?> objectType) {
		return objectType.getName();
	}

}
