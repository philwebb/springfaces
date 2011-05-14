package org.springframework.springfaces.mvc.internal;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.util.Assert;

/**
 * Internal utility class that provides a holding location for {@link DestinationAndModel}s. Used to allow the
 * {@link MvcNavigationHandler} to {@link #put store} outcomes that can later be {@link #get obtained} and rendered by
 * the {@link MvcViewHandler}.
 * 
 * @author webb_p
 */
class DestinationAndModelRegistry {

	// FIXME this may be overkill, there is probably only ever a single active view

	private static final String CONTAINER_ATTRIBUTE = DestinationAndModelRegistry.class.getName() + ".CONTAINER";
	private static final String KEY_PREFIX = DestinationAndModelRegistry.class.getName() + ":";

	@SuppressWarnings("unchecked")
	private Map<String, DestinationAndModel> getContainer(boolean create) {
		FacesContext context = getContext();
		Map<String, DestinationAndModel> container = (Map<String, DestinationAndModel>) context.getAttributes().get(
				CONTAINER_ATTRIBUTE);
		if (container == null && create) {
			container = new HashMap<String, DestinationAndModel>();
			context.getAttributes().put(CONTAINER_ATTRIBUTE, container);
		}
		return container;
	}

	private FacesContext getContext() {
		FacesContext context = FacesContext.getCurrentInstance();
		Assert.state(context != null, "Unable to obtain the FacesContext");
		return context;
	}

	public String put(FacesContext context, DestinationAndModel destinationAndModel) {
		Assert.notNull(destinationAndModel, "DestinationAndModel must not be null");
		synchronized (this) {
			Map<String, DestinationAndModel> container = getContainer(true);
			String key = KEY_PREFIX + String.valueOf(container.size() + 1);
			container.put(key, destinationAndModel);
			return key;
		}
	}

	public DestinationAndModel get(FacesContext context, String key) {
		if (key != null && key.startsWith(KEY_PREFIX)) {
			synchronized (this) {
				Map<String, DestinationAndModel> container = getContainer(false);
				if (container != null) {
					return container.get(key);
				}
			}
		}
		return null;
	}
}
