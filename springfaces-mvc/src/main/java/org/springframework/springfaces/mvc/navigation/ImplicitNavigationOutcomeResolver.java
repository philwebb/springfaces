package org.springframework.springfaces.mvc.navigation;

import org.springframework.springfaces.mvc.servlet.ViewIdResolver;
import org.springframework.util.StringUtils;

/**
 * A {@link NavigationOutcomeResolver} to handle implicit MVC navigation outcomes.
 * 
 * @author Phillip Webb
 */
public class ImplicitNavigationOutcomeResolver implements NavigationOutcomeResolver {

	// FIXME DC this is to prevent "Unable to find matching navigation case" errors

	private ViewIdResolver viewIdResolver;

	public ImplicitNavigationOutcomeResolver(ViewIdResolver viewIdResolver) {
		this.viewIdResolver = viewIdResolver;
	}

	public NavigationOutcome getNavigationOutcome(NavigationContext context) {
		String outcome = context.getOutcome();
		if (StringUtils.hasLength(outcome) && viewIdResolver.isResolvable(outcome)) {
			return new NavigationOutcome(outcome, null);
		}
		return null;
	}
}
