package org.springframework.springfaces.event;

import javax.faces.application.Application;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEventListener;

/**
 * A {@link SpringFacesApplicationEvent} that indicates that a JSF {@link Application} has been created. This event is
 * roughly equivalent to the standard JSF {@link PostConstructApplicationEvent event} with the exception that this event
 * is guaranteed to occur after Spring initialization. This event will be re-published whenever the Spring application
 * context is refreshed.
 * <p>
 * A common usage pattern is to use this event to trigger additional {@link Application#subscribeToEvent subscription}
 * of JSF {@link SystemEventListener}s.
 * 
 * @author Phillip Webb
 */
public class PostConstructApplicationSpringFacesEvent extends SpringFacesApplicationEvent {

	private static final long serialVersionUID = -1329729109758547764L;

	/**
	 * Create a new PostConstructFacesApplicationEvent.
	 * @param source the {@link Application} that caused the event to be published (never <code>null</code>)
	 */
	public PostConstructApplicationSpringFacesEvent(Application source) {
		super(source);
	}

	@Override
	public Application getSource() {
		return (Application) super.getSource();
	}
}
