package org.springframework.springfaces.message.resolver;

import org.springframework.context.MessageSource;

public interface MessageCodeResolver<T> {

	boolean canResolve(MessageSource messageSource, Class<?> type);

	String resolve(MessageSource messageSource, T object);

}
