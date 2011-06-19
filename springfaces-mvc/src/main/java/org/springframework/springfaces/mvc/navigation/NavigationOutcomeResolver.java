package org.springframework.springfaces.mvc.navigation;

import javax.faces.context.FacesContext;

/**
 * Strategy interface used to resolve navigation outcomes.
 * 
 * @author Phillip Webb
 */
public interface NavigationOutcomeResolver {

	/**
	 * Determines if this resolver can be used for the navigation.
	 * @param facesContext the faces context
	 * @param navigationContext the navigation context
	 * @return <tt>true</tt> if the resolver can be used or <tt>false</tt> if the resolve cannot handle the navigation
	 */
	boolean canResolve(FacesContext facesContext, NavigationContext navigationContext);

	/**
	 * Resolve an outcome for the navigation. This method will only be called when {@link #canResolve} returns
	 * <tt>true</tt>. A <tt>null</tt> return from this method is an indication that the current view should be
	 * redisplayed.
	 * @param facesContext the faces context
	 * @param navigationContext the navigation context
	 * @return the navigation outcome or <tt>null</tt> to redisplay the current view
	 * @throws Exception if the outcome cannot be resolved
	 */
	NavigationOutcome resolve(FacesContext facesContext, NavigationContext navigationContext) throws Exception;

}
