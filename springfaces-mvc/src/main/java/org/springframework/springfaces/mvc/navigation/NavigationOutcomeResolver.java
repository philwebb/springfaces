package org.springframework.springfaces.mvc.navigation;

public interface NavigationOutcomeResolver {

	// FIXME subclass for implict navigation
	// FIXME how to indicate no outcome but handled
	// FIXME how to indicate re-render

	// FIXME what
	/*
	 * -View -String (resolved to view) -A XTML page?
	 * 
	 * -ReRender -Handled
	 */

	public NavigationOutcome getNavigationOutcome(NavigationContext context);

}
