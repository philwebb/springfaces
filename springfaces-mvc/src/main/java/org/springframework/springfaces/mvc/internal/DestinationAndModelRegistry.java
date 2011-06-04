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
 * @author Phillip Webb
 */
class DestinationAndModelRegistry {

	private static final String CONTAINER_ATTRIBUTE = DestinationAndModelRegistry.class.getName() + ".CONTAINER";
	private static final String KEY_PREFIX = DestinationAndModelRegistry.class.getName() + ":";

	/**
	 * Put the specified {@link DestinationAndModel} into the registry.
	 * @param context the faces context
	 * @param destinationAndModel the destination and model
	 * @return a key that can be used to {@link #get retrieve} the item from the registry
	 */
	public String put(FacesContext context, DestinationAndModel destinationAndModel) {
		Assert.notNull(destinationAndModel, "DestinationAndModel must not be null");
		synchronized (this) {
			Map<String, DestinationAndModel> container = getContainer(context, true);
			String key = KEY_PREFIX + String.valueOf(container.size() + 1);
			container.put(key, destinationAndModel);
			return key;
		}
	}

	/**
	 * Retrieve a {@link DestinationAndModel} that has previously been {@link #put stored} in the registry.
	 * @param context the faces context
	 * @param key the key of the item to retrieve
	 * @return a {@link DestinationAndModel} or <tt>null</tt>
	 */
	public DestinationAndModel get(FacesContext context, String key) {
		if (key != null && key.startsWith(KEY_PREFIX)) {
			synchronized (this) {
				Map<String, DestinationAndModel> container = getContainer(context, false);
				if (container != null) {
					return container.get(key);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<String, DestinationAndModel> getContainer(FacesContext context, boolean create) {
		Assert.state(context != null, "Unable to access the FacesContext");
		Map<String, DestinationAndModel> container = (Map<String, DestinationAndModel>) context.getAttributes().get(
				CONTAINER_ATTRIBUTE);
		if (container == null && create) {
			container = new HashMap<String, DestinationAndModel>();
			context.getAttributes().put(CONTAINER_ATTRIBUTE, container);
		}
		return container;
	}
}
