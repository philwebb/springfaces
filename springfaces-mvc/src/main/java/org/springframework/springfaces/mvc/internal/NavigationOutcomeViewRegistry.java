package org.springframework.springfaces.mvc.internal;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.util.Assert;

/**
 * Internal utility class that provides a holding location for {@link NavigationOutcome}s. Used to allow the
 * {@link MvcNavigationHandler} to {@link #put store} outcomes that can later be {@link #get obtained} and rendered by
 * the {@link MvcViewHandler}.
 * 
 * @author webb_p
 */
class NavigationOutcomeViewRegistry {

	private static final String CONTAINER_ATTRIBUTE = NavigationOutcomeViewRegistry.class.getName() + ".CONTAINER";
	private static final String KEY_PREFIX = NavigationOutcomeViewRegistry.class.getName() + ":";

	private FacesContext getContext() {
		FacesContext context = FacesContext.getCurrentInstance();
		Assert.state(context != null, "Unable to obtain the FacesContext");
		return context;
	}

	@SuppressWarnings("unchecked")
	private Map<String, NavigationOutcome> getContainer(boolean create) {
		FacesContext context = getContext();
		Map<String, NavigationOutcome> container = (Map<String, NavigationOutcome>) context.getAttributes().get(
				CONTAINER_ATTRIBUTE);
		if (container == null && create) {
			container = new HashMap<String, NavigationOutcome>();
			context.getAttributes().put(CONTAINER_ATTRIBUTE, container);
		}
		return container;
	}

	public String put(FacesContext context, NavigationOutcome navigationOutcome) {
		Assert.notNull(navigationOutcome, "NavigationOutcome must not be null");
		synchronized (this) {
			Map<String, NavigationOutcome> container = getContainer(true);
			String key = KEY_PREFIX + String.valueOf(container.size() + 1);
			container.put(key, navigationOutcome);
			return key;
		}
	}

	public NavigationOutcome get(FacesContext context, String key) {
		if (key.startsWith(KEY_PREFIX)) {
			synchronized (this) {
				Map<String, NavigationOutcome> container = getContainer(false);
				if (container != null) {
					return container.get(key);
				}
			}
		}
		return null;
	}
}
