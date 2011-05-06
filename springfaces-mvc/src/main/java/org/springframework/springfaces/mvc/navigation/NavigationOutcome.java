package org.springframework.springfaces.mvc.navigation;

import java.io.Serializable;
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
	private Map<String, Object> implicitModel;

	/**
	 * Constructor.
	 * @param destination A non-null outcome destination. The destination can be a MVC {@link View} or an object that
	 * can be resolved to a MVC view.
	 * @param implicitModel An implicit model to be used with destination or <tt>null</tt>.
	 */
	public NavigationOutcome(Object destination, Map<String, Object> implicitModel) {
		super();
		Assert.notNull(destination, "Destination must not be null");
		this.destination = destination;
		this.implicitModel = implicitModel;
	}

	public Object getDestination() {
		return this.destination;
	}

	public Map<String, Object> getImplicitModel() {
		return implicitModel;
	}
}
