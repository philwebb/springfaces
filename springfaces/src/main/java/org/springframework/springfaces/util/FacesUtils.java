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

	public static Locale getLocale(FacesContext context) {
		Assert.notNull(context, "FacesContext must not be null");
		if (context.getViewRoot() != null && context.getViewRoot().getLocale() != null) {
			return context.getViewRoot().getLocale();
		}
		return context.getExternalContext().getRequestLocale();
	}

	// FIXME test
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

	public static <V> V doWithRequestScopeVariable(FacesContext context, String key, Object value, Callable<V> callable) {
		Assert.notNull(context, "Context must not be null");
		Assert.notNull(callable, "Callable must not be null");
		try {
			if (key == null) {
				return callable.call();
			}
			Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
			Object previousValue = requestMap.put(key, value);
			try {
				return callable.call();
			} finally {
				requestMap.remove(key);
				if (previousValue != null) {
					requestMap.put(key, previousValue);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
	}

	public static void doWithRequestScopeVariable(FacesContext context, String key, Object value,
			final Runnable runnable) {
		doWithRequestScopeVariable(context, key, value, new Callable<Object>() {
			public Object call() throws Exception {
				runnable.run();
				return null;
			}
		});
	}

}
