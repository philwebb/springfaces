package org.springframework.springfaces.message;

import java.util.Locale;

import org.springframework.context.NoSuchMessageException;

/**
 * Strategy interface used to resolve objects into localized messages.
 * 
 * @see AbstractObjectMessageSource
 * @see DefaultObjectMessageSource
 * 
 * @author Phillip Webb
 */
public interface ObjectMessageSource {

	/**
	 * Determines if this source is capable of {@link #getMessage returning} messages for objects of the specified type.
	 * @param type the type of object
	 * @return <tt>true</tt> if messages are contained
	 */
	boolean containsMessage(Class<?> type);

	/**
	 * Return a message for the given object, throwing an error if the object has not message.
	 * @param object the source object
	 * @param locale the locale of the message to return
	 * @return a message for the object
	 * @throws NoSuchMessageException if the message cannot be returned
	 */
	String getMessage(Object object, Locale locale) throws NoSuchMessageException;

}
