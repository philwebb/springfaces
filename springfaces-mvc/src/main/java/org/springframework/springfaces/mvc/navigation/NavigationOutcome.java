package org.springframework.springfaces.mvc.navigation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.View;

/**
 * The outcome of a resolved navigation.
 * 
 * @author Phillip Webb
 */
public final class NavigationOutcome implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object destination;
	private Map<String, List<String>> parameters;

	/**
	 * Constructor.
	 * @param destination The outcome destination. The destination can be a MVC {@link View} or an object that can be
	 * resolved to a MVC view. A <tt>null</tt> destination indicates that the outcome has been handled directly.
	 * @param parameters Parameters that should be included when redirecting to the destination.
	 */
	public NavigationOutcome(Object destination, Map<String, List<String>> parameters) {
		super();
		this.destination = destination;
		this.parameters = parameters;
	}

	public Object getDestination() {
		return this.destination;
	}

	public Map<String, List<String>> getParameters() {
		return this.parameters;
	}
}
