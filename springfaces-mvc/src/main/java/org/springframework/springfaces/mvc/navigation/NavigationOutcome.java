package org.springframework.springfaces.mvc.navigation;

import java.io.Serializable;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.web.servlet.View;

/**
 * The outcome of a resolved navigation. Outcomes are used to specify a {@link #getDestination() destination} and an
 * optional {@link #getImplicitModel() model}.
 * 
 * @see NavigationOutcomeResolver
 * 
 * @author Phillip Webb
 */
public final class NavigationOutcome implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object destination;

	private Map<String, Object> implicitModel;

	/**
	 * Constructor.
	 * @param destination A non-null outcome destination. The destination can be a MVC {@link View} or an object that
	 * can be {@link DestinationViewResolver resolved} to a MVC view.
	 */
	public NavigationOutcome(Object destination) {
		this(destination, null);
	}

	/**
	 * Constructor.
	 * @param destination A non-null outcome destination. The destination can be a MVC {@link View} or an object that
	 * can be {@link DestinationViewResolver resolved} to a MVC view.
	 * @param implicitModel An implicit model to be used with destination or <tt>null</tt>.
	 */
	public NavigationOutcome(Object destination, Map<String, Object> implicitModel) {
		super();
		Assert.notNull(destination, "Destination must not be null");
		this.destination = destination;
		this.implicitModel = implicitModel;
	}

	/**
	 * Returns the destination of the next view to render. The destination can be a MVC {@link View} or an object that
	 * can be resolved by a {@link DestinationViewResolver}.
	 * @return The destination
	 */
	public Object getDestination() {
		return this.destination;
	}

	/**
	 * Returns an implicit model that will be combined with any user specified model (ie &lt;f:param&gt;s) and provided
	 * when the {@link #getDestination() destination} is {@link View#render rendered}. The user specified model will
	 * take precedence in the case of duplicates keys.
	 * @return The implicit model or <tt>null</tt>
	 */
	public Map<String, Object> getImplicitModel() {
		return implicitModel;
	}
}
