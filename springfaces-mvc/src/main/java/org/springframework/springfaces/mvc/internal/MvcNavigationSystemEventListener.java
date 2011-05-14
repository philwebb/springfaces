package org.springframework.springfaces.mvc.internal;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * System Event Listener that stores the {@link PreRenderComponentEvent} so that the {@link MvcNavigationHandler} can
 * obtain it later.
 * 
 * @author Phillip Webb
 */
public class MvcNavigationSystemEventListener implements SystemEventListener {

	private static final String KEY = MvcNavigationSystemEventListener.class.getName();

	public void processEvent(SystemEvent event) throws AbortProcessingException {
		if (event instanceof PreRenderComponentEvent) {
			processEvent((PreRenderComponentEvent) event);
		}
	}

	private void processEvent(PreRenderComponentEvent event) throws AbortProcessingException {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getAttributes().put(KEY, event);
	}

	public boolean isListenerForSource(Object source) {
		return true;
	}

	/**
	 * Returns the last {@link PreRenderComponentEvent} that occurred.
	 * @param context The faces context
	 * @return The action event or <tt>null</tt>
	 */
	public static PreRenderComponentEvent getLastPreRenderComponentEvent(FacesContext context) {
		return (PreRenderComponentEvent) context.getAttributes().get(KEY);
	}
}
