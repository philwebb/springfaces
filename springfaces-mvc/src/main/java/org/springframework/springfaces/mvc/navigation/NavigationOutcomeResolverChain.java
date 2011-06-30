package org.springframework.springfaces.mvc.navigation;

import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.util.Assert;

/**
 * {@link NavigationOutcomeResolver} that allows several resolvers to be chained together.
 * 
 * @author Phillip Webb
 */
public class NavigationOutcomeResolverChain implements NavigationOutcomeResolver {

	private List<NavigationOutcomeResolver> resolvers;

	public boolean canResolve(FacesContext facesContext, NavigationContext navigationContext) {
		return findResolver(facesContext, navigationContext) != null;
	}

	public NavigationOutcome resolve(FacesContext facesContext, NavigationContext navigationContext) throws Exception {
		NavigationOutcomeResolver resolver = findResolver(facesContext, navigationContext);
		Assert.state(resolver != null,
				"Unable to find resolver for navigation outcome '" + navigationContext.getOutcome() + "'");
		return resolver.resolve(facesContext, navigationContext);
	}

	private NavigationOutcomeResolver findResolver(FacesContext facesContext, NavigationContext navigationContext) {
		NavigationOutcomeResolver found = null;
		if (resolvers != null) {
			for (NavigationOutcomeResolver resolver : resolvers) {
				if (resolver.canResolve(facesContext, navigationContext)) {
					Assert.state(found == null, "Duplicate resolvers found for navigation outcome '"
							+ navigationContext.getOutcome() + "'");
					found = resolver;
				}
			}
		}
		return found;
	}

	/**
	 * Set the list of resolvers that will be used when resolving a destination.
	 * @param resolvers the list of resolvers
	 */
	public void setResolvers(List<NavigationOutcomeResolver> resolvers) {
		this.resolvers = resolvers;
	}
}
