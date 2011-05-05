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
	private NavigationOutcomeViewRegistry navigationOutcomeViewRegistry = new NavigationOutcomeViewRegistry();

	public MvcNavigationHandler(ConfigurableNavigationHandler delegate,
			NavigationOutcomeResolver navigationOutcomeResolver) {
		this.delegate = delegate;
		this.navigationOutcomeResolver = navigationOutcomeResolver;
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		return delegate;
	}

	@Override
	public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
		if (SpringFacesContext.getCurrentInstance() != null) {
			NavigationContext navigationContext = new NavigationContextImpl(fromAction, outcome, true);
			if (navigationOutcomeResolver.canResolve(navigationContext)) {
				NavigationOutcome navigationOutcome = navigationOutcomeResolver.resolve(navigationContext);
				Assert.state(navigationOutcome != null, "Unable to get a NavigationCase from outcome '" + outcome
						+ "' due to missing outcome");
				UIViewRoot root = context.getViewRoot();
				String fromViewId = (root != null ? root.getViewId() : null);
				String toViewId = navigationOutcomeViewRegistry.put(context, navigationOutcome);
				Map<String, List<String>> parameters = new HashMap<String, List<String>>();
				if (navigationOutcome.getParameters() != null) {
					parameters.putAll(navigationOutcome.getParameters());
				}
				return new NavigationCase(fromViewId, fromAction, outcome, null, toViewId, parameters, false, false);
			}
		}
		return super.getNavigationCase(context, fromAction, outcome);
	}

	@Override
	public void handleNavigation(FacesContext context, String fromAction, String outcome) {
		if (SpringFacesContext.getCurrentInstance() != null) {
			NavigationContext navigationContext = new NavigationContextImpl(fromAction, outcome, false);
			if (navigationOutcomeResolver.canResolve(navigationContext)) {
				NavigationOutcome navigationOutcome = navigationOutcomeResolver.resolve(navigationContext);
				if (navigationOutcome != null) {
					String viewId = navigationOutcomeViewRegistry.put(context, navigationOutcome);
					UIViewRoot newRoot = context.getApplication().getViewHandler().createView(context, viewId);
					// FIXME do we need this, should it be the view handler?
					setRenderAll(context, viewId);
					context.setViewRoot(newRoot);
					return;
				}
			}
		}
		super.handleNavigation(context, fromAction, outcome);
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

	/**
	 * Implementation of the {@link NavigationContext}.
	 */
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
