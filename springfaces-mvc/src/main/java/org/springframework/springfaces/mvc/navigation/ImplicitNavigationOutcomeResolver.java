package org.springframework.springfaces.mvc.navigation;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A {@link NavigationOutcomeResolver} to handle implicit MVC navigation outcomes. Implicit outcomes can be embedded
 * within a JSF page (if JSF implicit navigation is enabled) or used within standard JSF navigation rules. Outcomes are
 * prefixed in order to distinguish them from regular JSF outcomes. The default prefix is <tt>mvc:</tt>, however, this
 * can be {@link #setPrefix changed} if necessary. This resolver will ultimately return a String
 * {@link NavigationOutcome#getDestination() destination}, for example the implicit view "<tt>mvc:redirect:/home</tt>"
 * will return an outcome containing the destination "<tt>redirect:/home</tt>".
 * 
 * @author Phillip Webb
 */
public class ImplicitNavigationOutcomeResolver implements NavigationOutcomeResolver {

	private String prefix = "mvc:";

	public boolean canResolve(NavigationContext context) {
		return canResolve(context.getDefaultDestinationViewId()) || canResolve(context.getOutcome());
	}

	private boolean canResolve(String value) {
		return (StringUtils.hasLength(value) && value.startsWith(prefix));
	}

	public NavigationOutcome resolve(NavigationContext context) {
		String destination = null;
		if (canResolve(context.getDefaultDestinationViewId())) {
			destination = resolve(context.getDefaultDestinationViewId());
		} else if (canResolve(context.getOutcome())) {
			destination = resolve(context.getOutcome());
		}
		Assert.state(destination != null);
		return new NavigationOutcome(destination, null);
	}

	private String resolve(String value) {
		String destination = value.substring(prefix.length());
		Assert.state(StringUtils.hasLength(destination),
				"The destination must be specified for an implicit MVC navigation prefixed '" + prefix + "'");
		return destination;
	}

	/**
	 * Sets the prefix that will be used to distinguish a MVC implicit outcome from a regular JSF outcome.
	 * @param prefix the prefix for implicit MVC navigation
	 */
	public void setPrefix(String prefix) {
		Assert.hasLength(prefix, "Prefix must contain at least character");
		this.prefix = prefix;
	}
}
