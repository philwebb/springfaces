package org.springframework.springfaces.message.code.resolver;

import java.util.List;

import org.springframework.context.MessageSource;

/**
 * Strategy interfaces used to resolve a object or type to a message code for use against a {@link MessageSource}.
 * 
 * @author Phillip Webb
 * 
 * @param <T>
 */
public interface MessageCodeResolver<T> {

	/**
	 * Return the codes that used be used for the given object. The first suitable code in the list will be used to
	 * resolve the message.
	 * @param object the object being resolved
	 * @return a list of message codes or <tt>null</tt>
	 */
	List<String> getMessageCodesForObject(T object);

	/**
	 * Return the codes that used be used for the given type. if any of the codes are contained in message source then
	 * the resolved is considered suitable to {@link #getMessageCodesForObject(Object) used} with objects of the given
	 * type.
	 * @param type the type being resolved
	 * @return a list of message codes or <tt>null</tt>
	 */
	List<String> getMessageCodesForType(Class<? extends T> type);

}
