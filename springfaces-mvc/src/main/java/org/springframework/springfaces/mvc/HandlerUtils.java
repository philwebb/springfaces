package org.springframework.springfaces.mvc;

import org.springframework.web.method.HandlerMethod;

/**
 * Simple utility class for working with the MVC Handlers.
 * 
 * @author Phillip Webb
 */
public abstract class HandlerUtils {

	/**
	 * Returns the underlying bean that is linked to the handler dealing transparently with {@link HandlerMethod}
	 * handlers.
	 * @param handler the source handler (can be <tt>null</tt>)
	 * @return the handler bean or <tt>null</tt>
	 */
	public static Object getHandlerBean(Object handler) {
		if (handler == null) {
			return null;
		}
		if (handler instanceof HandlerMethod) {
			return ((HandlerMethod) handler).createWithResolvedBean().getBean();
		}
		return handler;
	}

}
