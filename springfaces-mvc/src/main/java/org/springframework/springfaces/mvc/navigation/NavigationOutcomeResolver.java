package org.springframework.springfaces.mvc.navigation;

/**
 * Strategy interface used to resolve navigation outcomes.
 * 
 * @author Phillip Webb
 */
public interface NavigationOutcomeResolver {

	/**
	 * Attempts to resolve an outcome for the navigation. Return <tt>null</tt> if the navigation cannot be handled by
	 * this resolver. The {@link NavigationOutcome} can be used to specify a destination or an empty destination can be
	 * used if the navigation has handled by the resolver.
	 * 
	 * @param context
	 * @return The navigation outcome or <tt>null</tt>.
	 */
	public NavigationOutcome getNavigationOutcome(NavigationContext context);

}
