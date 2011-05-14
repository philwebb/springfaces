package org.springframework.springfaces.event;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract base of all {@link ApplicationEvent}s that originate from JSF.
 * 
 * @author Phillip Webb
 */
public abstract class SpringFacesApplicationEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4769349942324437586L;

	/**
	 * Create a new FacesApplicationEvent.
	 * @param source the component that published the event (never <code>null</code>)
	 */
	public SpringFacesApplicationEvent(Object source) {
		super(source);
	}
}
