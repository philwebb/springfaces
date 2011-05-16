package org.springframework.springfaces.mvc.navigation;

/**
 * Strategy interface used to resolve navigation outcomes.
 * 
 * @author Phillip Webb
 */
public interface NavigationOutcomeResolver {

	/**
	 * Determines if this resolver can be used for the navigation.
	 * @param context the navigation context
	 * @return <tt>true</tt> if the resolver can be used or <tt>false</tt> if the resolve cannot handle the navigation
	 */
	boolean canResolve(NavigationContext context);

	/**
	 * Resolve an outcome for the navigation. This method will only be called when {@link #canResolve} returns
	 * <tt>true</tt>. A <tt>null</tt> return from this method is an indication that the current view should be
	 * redisplayed.
	 * @param context the navigation context
	 * @return the navigation outcome or <tt>null</tt> to redisplay the current view
	 */
	NavigationOutcome resolve(NavigationContext context);

}
