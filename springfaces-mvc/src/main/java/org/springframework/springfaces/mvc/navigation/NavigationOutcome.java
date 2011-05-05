package org.springframework.springfaces.mvc.navigation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.web.servlet.View;

/**
 * The outcome of a resolved navigation.
 * 
 * @author Phillip Webb
 */
public final class NavigationOutcome implements Serializable {

	// FIXME DC

	private static final long serialVersionUID = 1L;

	private Object destination;
	private Map<String, List<String>> parameters;

	/**
	 * Constructor.
	 * @param destination A non-null outcome destination. The destination can be a MVC {@link View} or an object that
	 * can be resolved to a MVC view.
	 * @param parameters Parameters that should be included when redirecting to the destination or <tt>null</tt>.
	 */
	public NavigationOutcome(Object destination, Map<String, List<String>> parameters) {
		super();
		Assert.notNull(destination, "Destination must not be null");
		this.destination = destination;
		this.parameters = parameters;
	}

	public Object getDestination() {
		return this.destination;
	}

	// FIXME DC parameters
	// FIXME should this me named model? Should List<String> be an object
	public Map<String, List<String>> getParameters() {
		return this.parameters;
	}
}
