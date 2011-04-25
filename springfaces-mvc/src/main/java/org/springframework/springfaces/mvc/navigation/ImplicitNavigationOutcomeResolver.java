package org.springframework.springfaces.mvc.navigation;

import org.springframework.util.StringUtils;

/**
 * A {@link NavigationOutcomeResolver} to handle implicit MVC navigation outcomes.
 * 
 * @author Phillip Webb
 */
public class ImplicitNavigationOutcomeResolver implements NavigationOutcomeResolver {

	// FIXME DC this is to prevent "Unable to find matching navigation case" errors

	private String prefix = "mvc:";

	public NavigationOutcome getNavigationOutcome(NavigationContext context) {
		String outcome = context.getOutcome();
		if (StringUtils.hasLength(outcome) && outcome.startsWith(prefix)) {
			return new NavigationOutcome(outcome.substring(prefix.length()), null);
		}
		return null;
	}
}
