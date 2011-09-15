package org.springframework.springfaces.util;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.util.Assert;

/**
 * General purpose JSF Utilities.
 * 
 * @author Phillip Webb
 */
public abstract class FacesUtils {

	/**
	 * Returns the most appropriate {@link Locale} for the given faces context.
	 * @param context the faces context
	 * @return a {@link Locale} obtained from the context
	 */
	public static Locale getLocale(FacesContext context) {
		Assert.notNull(context, "FacesContext must not be null");
		if (context.getViewRoot() != null && context.getViewRoot().getLocale() != null) {
			return context.getViewRoot().getLocale();
		}
		return context.getExternalContext().getRequestLocale();
	}

	/**
	 * Returns the first parent of the given component of the specified type.
	 * @param <T> The parent type
	 * @param component the component to search
	 * @param parentType the parent type
	 * @return a parent of the given type or <tt>null</tt> if the component does not have a suitable parent
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findParentOfType(UIComponent component, Class<T> parentType) {
		Assert.notNull(component, "Component must not be null");
		Assert.notNull(parentType, "ParentType must not be null");
		UIComponent current = component.getParent();
		while (current != null) {
			if (parentType.isInstance(current)) {
				return (T) current;
			}
			current = current.getParent();
		}
		return null;
	}

	/**
	 * Execute a {@link Callable} with a <tt>request-scope</tt> JSF variable set for the duration of the execution.
	 * 
	 * @param <V> the return type
	 * @param context the faces context
	 * @param variableName the variable name to set (can be <tt>null</tt>)
	 * @param value the value of the variable
	 * @param callable the callable to execute
	 * @return the result of the call
	 * @see #doWithRequestScopeVariable(FacesContext, String, Object, Runnable)
	 */
	public static <V> V doWithRequestScopeVariable(FacesContext context, String variableName, Object value,
			Callable<V> callable) {
		Assert.notNull(context, "Context must not be null");
		Assert.notNull(callable, "Callable must not be null");
		try {
			if (variableName == null) {
				return callable.call();
			}
			Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
			Object previousValue = requestMap.put(variableName, value);
			try {
				return callable.call();
			} finally {
				requestMap.remove(variableName);
				if (previousValue != null) {
					requestMap.put(variableName, previousValue);
				}
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Execute a {@link Runnable} with a <tt>request-scope</tt> JSF variable set for the duration of the execution.
	 * 
	 * @param context the faces context
	 * @param variableName the variable name to set (can be <tt>null</tt>)
	 * @param value the value of the variable
	 * @param runnable the runnable to execute
	 * @see #doWithRequestScopeVariable(FacesContext, String, Object, Callable)
	 */
	public static void doWithRequestScopeVariable(FacesContext context, String variableName, Object value,
			final Runnable runnable) {
		Assert.notNull(runnable, "Runnable must not be null");
		doWithRequestScopeVariable(context, variableName, value, new Callable<Object>() {
			public Object call() throws Exception {
				runnable.run();
				return null;
			}
		});
	}

}
