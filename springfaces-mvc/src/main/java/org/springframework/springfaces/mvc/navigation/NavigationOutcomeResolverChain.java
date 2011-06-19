package org.springframework.springfaces.mvc.navigation;

import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.util.Assert;

public class NavigationOutcomeResolverChain implements NavigationOutcomeResolver {

	// FIXME DC + test

	private List<NavigationOutcomeResolver> resolvers;

	public NavigationOutcomeResolverChain() {
	}

	public boolean canResolve(FacesContext facesContext, NavigationContext navigationContext) {
		return findResolver(facesContext, navigationContext) != null;
	}

	public NavigationOutcome resolve(FacesContext facesContext, NavigationContext navigationContext) throws Exception {
		NavigationOutcomeResolver resolver = findResolver(facesContext, navigationContext);
		// FIXME better error
		Assert.state(resolver != null, "Unable to find resolver for navigation");
		return resolver.resolve(facesContext, navigationContext);
	}

	private NavigationOutcomeResolver findResolver(FacesContext facesContext, NavigationContext navigationContext) {
		NavigationOutcomeResolver found = null;
		for (NavigationOutcomeResolver resolver : resolvers) {
			if (resolver.canResolve(facesContext, navigationContext)) {
				// FIXME better error
				Assert.state(found == null, "Duplicate resolvers found for navigation");
				found = resolver;
			}
		}
		return found;
	}

	public void setResolvers(List<NavigationOutcomeResolver> resolvers) {
		this.resolvers = resolvers;
	}

}
