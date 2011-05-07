package org.springframework.springfaces.mvc.internal;

import java.util.Iterator;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationCase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.springfaces.util.ConfigurableNavigationHandlerWrapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
			String defaultDestinationViewId = getDefaultDestinationViewId(context, fromAction, outcome);
			NavigationContext navigationContext = new NavigationContextImpl(fromAction, outcome, true, null,
					defaultDestinationViewId);
			if (navigationOutcomeResolver.canResolve(navigationContext)) {
				NavigationOutcome navigationOutcome = navigationOutcomeResolver.resolve(navigationContext);
				Assert.state(navigationOutcome != null, "Unable to resolve required navigation outcome '" + outcome
						+ "'");
				UIViewRoot root = context.getViewRoot();
				String fromViewId = (root != null ? root.getViewId() : null);
				String toViewId = navigationOutcomeViewRegistry.put(context, navigationOutcome);
				return new NavigationCase(fromViewId, fromAction, outcome, null, toViewId, null, false, false);
			}
		}
		return super.getNavigationCase(context, fromAction, outcome);
	}

	@Override
	public void handleNavigation(FacesContext context, String fromAction, String outcome) {
		if (SpringFacesContext.getCurrentInstance() != null) {
			ActionEvent actionEvent = MvcNavigationActionListener.getLastActionEvent(context);
			String defaultDestinationViewId = getDefaultDestinationViewId(context, fromAction, outcome);
			NavigationContext navigationContext = new NavigationContextImpl(fromAction, outcome, false, actionEvent,
					defaultDestinationViewId);
			if (navigationOutcomeResolver.canResolve(navigationContext)) {
				NavigationOutcome navigationOutcome = navigationOutcomeResolver.resolve(navigationContext);
				if (navigationOutcome != null) {
					String viewId = navigationOutcomeViewRegistry.put(context, navigationOutcome);
					UIViewRoot newRoot = context.getApplication().getViewHandler().createView(context, viewId);
					context.setViewRoot(newRoot);
					return;
				}
			}
		}
		super.handleNavigation(context, fromAction, outcome);
	}

	private String getDefaultDestinationViewId(FacesContext context, String fromAction, String outcome) {
		int numberOfMessages = context.getMessageList().size();
		NavigationCase navigationCase = super.getNavigationCase(context, fromAction, outcome);
		// If the navigation handler has inserted warning messages about missing navigation cases we need
		// to remove them, we are subverting the use of getNavigationCase a little and it does not matter
		// if we cannot find a case
		Iterator<FacesMessage> messages = context.getMessages();
		while (messages.hasNext()) {
			messages.next();
			numberOfMessages--;
			if (numberOfMessages < 0) {
				messages.remove();
			}
		}
		if (navigationCase == null) {
			return null;
		}
		String defaultDestinationViewId = navigationCase.getToViewId(context);
		if (StringUtils.hasLength(defaultDestinationViewId) && defaultDestinationViewId.startsWith("/")) {
			defaultDestinationViewId = defaultDestinationViewId.substring(1);
		}
		return defaultDestinationViewId;
	}

	/**
	 * Allows the {@link NavigationOutcomeViewRegistry} to be changed for testing.
	 * @param navigationOutcomeViewRegistry A navigation outcome view registry
	 */
	final void setNavigationOutcomeViewRegistry(NavigationOutcomeViewRegistry navigationOutcomeViewRegistry) {
		this.navigationOutcomeViewRegistry = navigationOutcomeViewRegistry;
	}

	/**
	 * Implementation of the {@link NavigationContext}.
	 */
	private static class NavigationContextImpl implements NavigationContext {

		private String fromAction;
		private String outcome;
		private boolean preEmptive;
		private ActionEvent actionEvent;
		private String defaultDestinationViewId;

		public NavigationContextImpl(String fromAction, String outcome, boolean preEmptive, ActionEvent actionEvent,
				String defaultDestinationViewId) {
			this.fromAction = fromAction;
			this.outcome = outcome;
			this.preEmptive = preEmptive;
			this.actionEvent = actionEvent;
			this.defaultDestinationViewId = defaultDestinationViewId;
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

		public boolean isPreemptive() {
			return preEmptive;
		}

		public ActionEvent getActionEvent() {
			return actionEvent;
		}

		public String getDefaultDestinationViewId() {
			return defaultDestinationViewId;
		}
	}
}
