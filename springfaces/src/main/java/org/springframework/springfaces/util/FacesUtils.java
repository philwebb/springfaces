package org.springframework.springfaces.util;

import java.util.Locale;

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
		UIComponent current = component.getParent();
		while (current != null) {
			if (parentType.isInstance(current)) {
				return (T) current;
			}
			current = current.getParent();
		}
		return null;
	}

}
