package org.springframework.springfaces.message;

import org.springframework.context.MessageSource;

public interface MessageCodeConverter<T> {

	boolean canConvert(MessageSource messageSource, Class<?> type);

	String convert(MessageSource messageSource, T object);

}
