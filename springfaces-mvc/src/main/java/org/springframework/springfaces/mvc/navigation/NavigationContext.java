package org.springframework.springfaces.mvc.navigation;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationHandler;
import javax.faces.event.ActionEvent;

/**
 * Provides context information relating to the current navigation.
 * 
 * @author Phillip Webb
 */
public interface NavigationContext {

	/**
	 * Returns the current MVC handler the processing the JSF request or <tt>null</tt> if the request did not originate
	 * from Spring MVC.
	 * @return The spring MVC handler or <tt>null</tt>
	 */
	Object getHandler();

	/**
	 * Returns the action binding expression that was evaluated to retrieve the specified outcome, or <tt>null</tt> if
	 * the outcome was acquired by some other means.
	 * @return The action binding expression or <tt>null</tt>
	 * @see NavigationHandler#handleNavigation
	 */
	String getFromAction();

	/**
	 * Returns the logical outcome returned by a previous invoked application action (which may be <tt>null</tt>).
	 * @return The logical outcome or <tt>null</tt>
	 * @see NavigationHandler#handleNavigation
	 */
	String getOutcome();

	/**
	 * Returns <tt>true</tt> if the navigation is preemptive ({@link ConfigurableNavigationHandler#getNavigationCase})
	 * or <tt>false</tt> if the navigation is actually being handled (
	 * {@link ConfigurableNavigationHandler#handleNavigation}). Preemptive navigation is used when constructing
	 * bookmarkable URLs.
	 * @return If the navigation is preemptive
	 */
	boolean isPreemptive();

	/**
	 * Returns the {@link ActionEvent} that triggered the navigation of <tt>null</tt> if the navigation was triggered by
	 * some other means. This method will always return <tt>null</tt> for {@link #isPreemptive() preemptive} navigation.
	 * @return THe action event or <tt>null</tt>
	 */
	ActionEvent getActionEvent();

	/**
	 * Returns the destination view ID that JSF will use if no {@link NavigationOutcomeResolver resolvers} can be used.
	 * @return The default destination view ID
	 */
	String getDefaultDestinationViewId();
}
