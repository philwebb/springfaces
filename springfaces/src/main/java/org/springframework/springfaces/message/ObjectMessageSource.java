package org.springframework.springfaces.message;

import java.util.Locale;

import org.springframework.context.MessageSource;

/**
 * Extension of {@link MessageSource} that can resolve objects into localized messages.
 * 
 * @author Phillip Webb
 */
public interface ObjectMessageSource extends MessageSource {

	/**
	 * Return a message for the given object, throwing an error if the object has no message.
	 * @param object the source object
	 * @param locale the locale of the message to return
	 * @return a message for the object
	 * @throws NoSuchObjectMessageException if the message cannot be returned
	 */
	String getMessage(Object object, Locale locale) throws NoSuchObjectMessageException;
}
