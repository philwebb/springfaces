package org.springframework.springfaces.messagesource;

import org.springframework.context.MessageSource;

/**
 * Strategy interface used to resolve {@link MessageSource} arguments.
 * 
 * @author Phillip Webb
 */
public interface MessageArgumentResolver {

	/**
	 * {@link MessageArgumentResolver} that returns the argument unchanged.
	 */
	public static final MessageArgumentResolver NONE = new MessageArgumentResolver() {
		public Object resolveMessageArgument(Object argument) {
			return argument;
		}
	};

	/**
	 * Resolve the specified argument before it is used with a {@link MessageSource}.
	 * @param argument the argument to resolve
	 * @return the resolved argument
	 */
	public Object resolveMessageArgument(Object argument);
}
