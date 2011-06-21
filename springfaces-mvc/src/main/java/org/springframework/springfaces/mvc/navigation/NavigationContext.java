package org.springframework.springfaces.mvc.navigation;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIComponent;

import org.springframework.springfaces.mvc.context.SpringFacesContext;

/**
 * Provides context information relating to the current navigation.
 * 
 * @author Phillip Webb
 */
public interface NavigationContext {

	/**
	 * Returns the current MVC handler processing the JSF request or <tt>null</tt> if the request did not originate from
	 * Spring MVC.
	 * @return the spring MVC handler or <tt>null</tt>
	 * @see SpringFacesContext#getHandler()
	 */
	Object getHandler();

	/**
	 * Returns the current MVC controller bean processing the JSF request of <tt>null</tt> if the request did not
	 * originate from Spring MVC.
	 * @return the spring MVC controller or <tt>null</tt>
	 * @see SpringFacesContext#getController()
	 */
	Object getController();

	/**
	 * Returns the action binding expression that was evaluated to retrieve the specified outcome, or <tt>null</tt> if
	 * the outcome was acquired by some other means.
	 * @return the action binding expression or <tt>null</tt>
	 * @see NavigationHandler#handleNavigation
	 */
	String getFromAction();

	/**
	 * Returns the logical outcome returned by a previous invoked application action (which may be <tt>null</tt>).
	 * @return the logical outcome or <tt>null</tt>
	 * @see NavigationHandler#handleNavigation
	 */
	String getOutcome();

	/**
	 * Returns <tt>true</tt> if the navigation is preemptive ({@link ConfigurableNavigationHandler#getNavigationCase})
	 * or <tt>false</tt> if the navigation is actually being handled (
	 * {@link ConfigurableNavigationHandler#handleNavigation}). Preemptive navigation is used when constructing
	 * bookmarkable URLs.
	 * @return if the navigation is preemptive
	 */
	boolean isPreemptive();

	/**
	 * Returns the {@link UIComponent} that triggered the navigation or <tt>null</tt> if the navigation was triggered by
	 * some other means.
	 * @return a component or <tt>null</tt>
	 */
	UIComponent getComponent();

	/**
	 * Returns the destination view ID that JSF will use if no {@link NavigationOutcomeResolver resolvers} can be used.
	 * @return the default destination view ID
	 */
	String getDefaultDestinationViewId();
}
