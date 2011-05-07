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
 * 
 * @author Phillip Webb
 */
public class ImplicitNavigationOutcomeResolver implements NavigationOutcomeResolver {

	private String prefix = "mvc:";

	public boolean canResolve(NavigationContext context) {
		String outcome = context.getOutcome();
		return (StringUtils.hasLength(outcome) && outcome.startsWith(prefix));
	}

	public NavigationOutcome resolve(NavigationContext context) {
		Assert.state(canResolve(context));
		String destination = context.getOutcome().substring(prefix.length());
		Assert.hasLength(destination, "The destination must be specified for implicit MVC navigation");
		return new NavigationOutcome(destination, null);
	}

	/**
	 * Sets the prefix that will be used to distinguish a MVC implicit outcome from a regular JSF outcome.
	 * @param prefix The prefix for implicit MVC navigation
	 */
	public void setPrefix(String prefix) {
		Assert.hasLength(prefix, "Prefix must contain at least character");
		this.prefix = prefix;
	}

}
