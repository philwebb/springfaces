package org.springframework.springfaces.mvc.navigation;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A {@link NavigationOutcomeResolver} to handle implicit MVC navigation outcomes.
 * 
 * @author Phillip Webb
 */
public class ImplicitNavigationOutcomeResolver implements NavigationOutcomeResolver {

	// FIXME DC this is to prevent "Unable to find matching navigation case" errors

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
}
