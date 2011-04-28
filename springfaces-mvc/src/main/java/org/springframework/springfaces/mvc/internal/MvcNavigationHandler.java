package org.springframework.springfaces.mvc.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.springfaces.util.ConfigurableNavigationHandlerWrapper;
import org.springframework.util.Assert;

/**
 * A JSF {@link ConfigurableNavigationHandler} that provides integration with Spring MVC.
 * 
 * @author Phillip Webb
 */
public class MvcNavigationHandler extends ConfigurableNavigationHandlerWrapper {

	private ConfigurableNavigationHandler delegate;
	private NavigationOutcomeResolver navigationOutcomeResolver;
	private DestinationRegistry destinationRegistry;

	public MvcNavigationHandler(ConfigurableNavigationHandler delegate,
			NavigationOutcomeResolver navigationOutcomeResolver, DestinationRegistry destinationRegistry) {
		this.delegate = delegate;
		this.navigationOutcomeResolver = navigationOutcomeResolver;
		this.destinationRegistry = destinationRegistry;
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		return delegate;
	}

	@Override
	public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
		NavigationOutcome navigationOutcome = getNavigationOutcome(context, fromAction, outcome, true);
		if (navigationOutcome == null) {
			return super.getNavigationCase(context, fromAction, outcome);
		}
		Assert.state(navigationOutcome.getDestination() != null, "Unable to construct NavigationCase from outcome '"
				+ outcome + "' due to missing destination");
		// FIXME assert not rendered directly
		UIViewRoot root = context.getViewRoot();
		String fromViewId = (root != null ? root.getViewId() : null);
		String toViewId = destinationRegistry.put(navigationOutcome.getDestination());
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		if (navigationOutcome.getParameters() != null) {
			parameters.putAll(navigationOutcome.getParameters());
		}
		return new NavigationCase(fromViewId, fromAction, outcome, null, toViewId, parameters, false, false);
	}

	@Override
	public void handleNavigation(FacesContext context, String fromAction, String outcome) {
		NavigationOutcome navigationOutcome = getNavigationOutcome(context, fromAction, outcome, false);
		if (navigationOutcome == null) {
			super.handleNavigation(context, fromAction, outcome);
			return;
		}
		String viewId = destinationRegistry.put(navigationOutcome.getDestination());
		// FIXME stop if rendered directly
		UIViewRoot newRoot = context.getApplication().getViewHandler().createView(context, viewId);
		setRenderAll(context, viewId);
		context.setViewRoot(newRoot);
		// FIXME logging
	}

	private void setRenderAll(FacesContext facesContext, String viewId) {
		if (facesContext.getViewRoot().getViewId().equals(viewId)) {
			return;
		}
		PartialViewContext partialViewContext = facesContext.getPartialViewContext();
		if (partialViewContext.isRenderAll()) {
			return;
		}
		partialViewContext.setRenderAll(true);
	}

	private NavigationOutcome getNavigationOutcome(FacesContext context, String fromAction, String outcome,
			boolean preEmptive) {
		if (SpringFacesContext.getCurrentInstance() == null) {
			return null;
		}
		NavigationContext navigationContext = new NavigationContextImpl(fromAction, outcome, preEmptive);
		return navigationOutcomeResolver.getNavigationOutcome(navigationContext);
	}

	private static class NavigationContextImpl implements NavigationContext {

		private String fromAction;
		private String outcome;
		private boolean preEmptive;

		public NavigationContextImpl(String fromAction, String outcome, boolean preEmptive) {
			this.fromAction = fromAction;
			this.outcome = outcome;
			this.preEmptive = preEmptive;
		}

		public Object getHandler() {
			return SpringFacesContext.getCurrentInstance().getHandler();
		}

		public String getFromAction() {
			return fromAction;
		}

		public String getOutcome() {
			return outcome;
		}

		public boolean isPreEmptive() {
			return preEmptive;
		}
	}
}
