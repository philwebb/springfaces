package org.springframework.springfaces.mvc.navigation;

/**
 * Strategy interface used to resolve navigation outcomes.
 * 
 * @author Phillip Webb
 */
public interface NavigationOutcomeResolver {

	/**
	 * Determines if this resolver can be used for the navigation.
	 * 
	 * @param context The navigation context
	 * @return <tt>true</tt> if the resolver can be used or <tt>false</tt> if the resolve cannot handle the navigation
	 */
	public boolean canResolve(NavigationContext context);

	/**
	 * Resolve an outcome for the navigation. This method will only be called when {@link #isResolvable} returns
	 * <tt>true</tt>. A <tt>null</tt> return from this method is an indication that the current view should be
	 * redisplayed.
	 * 
	 * @param context The navigation context
	 * @return The navigation outcome or <tt>null</tt> to redisplay the current view
	 */
	public NavigationOutcome resolve(NavigationContext context);

}
