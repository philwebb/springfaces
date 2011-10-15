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
	 * @param args array of arguments that will be filled in for params within the message (params look like "{0}",
	 * "{1,date}", "{2,time}" within a message), or <code>null</code> if none.
	 * @param locale the locale of the message to return
	 * @return a message for the object
	 * @throws NoSuchObjectMessageException if the message cannot be returned
	 */
	String getMessage(Object object, Object[] args, Locale locale) throws NoSuchObjectMessageException;
}
