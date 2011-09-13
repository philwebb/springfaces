package org.springframework.springfaces.message.resolver;

import org.springframework.context.MessageSource;

public class ObjectMessageCodeResolver implements MessageCodeResolver<Object> {

	public boolean canResolve(MessageSource messageSource, Class<?> type) {
		String code = getCode(type);
		return MessageSourceUtils.containsCode(messageSource, code);
	}

	public String resolve(MessageSource messageSource, Object object) {
		return getCode(object.getClass());
	}

	private String getCode(Class<?> objectType) {
		return objectType.getName();
	}

}
