/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.mvc.internal;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationCase;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PreRenderComponentEvent;

import org.springframework.springfaces.application.ConfigurableNavigationHandlerWrapper;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A JSF {@link ConfigurableNavigationHandler} that provides integration with Spring MVC. Note: This handler depends on
 * the {@link MvcNavigationSystemEventListener} and {@link MvcNavigationActionListener} also being registered.
 * 
 * @author Phillip Webb
 */
public class MvcNavigationHandler extends ConfigurableNavigationHandlerWrapper {

	private ConfigurableNavigationHandler delegate;
	private NavigationOutcomeResolver navigationOutcomeResolver;
	private DestinationAndModelRegistry destinationAndModelRegistry = new DestinationAndModelRegistry();

	public MvcNavigationHandler(ConfigurableNavigationHandler delegate,
			NavigationOutcomeResolver navigationOutcomeResolver) {
		this.delegate = delegate;
		this.navigationOutcomeResolver = navigationOutcomeResolver;
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		return this.delegate;
	}

	@Override
	public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
		if (SpringFacesContext.getCurrentInstance() != null) {
			PreRenderComponentEvent preRenderComponentEvent = MvcNavigationSystemEventListener
					.getLastPreRenderComponentEvent(context);
			UIComponent component = (preRenderComponentEvent == null ? null : preRenderComponentEvent.getComponent());
			String defaultDestinationViewId = getDefaultDestinationViewId(context, fromAction, outcome);
			NavigationContext navigationContext = new NavigationContextImpl(fromAction, outcome, true, component,
					defaultDestinationViewId);
			if (this.navigationOutcomeResolver.canResolve(context, navigationContext)) {
				try {
					NavigationOutcome navigationOutcome = this.navigationOutcomeResolver.resolve(context,
							navigationContext);
					Assert.state(navigationOutcome != null, "Unable to resolve required navigation outcome '" + outcome
							+ "'");
					UIViewRoot root = context.getViewRoot();
					String fromViewId = (root != null ? root.getViewId() : null);
					String toViewId = this.destinationAndModelRegistry.put(context, new DestinationAndModel(
							navigationOutcome, preRenderComponentEvent));
					return new NavigationCase(fromViewId, fromAction, outcome, null, toViewId, null, false, false);
				} catch (Exception e) {
					// FIXME
					throw new FacesException(e);
				}
			}
		}
		return super.getNavigationCase(context, fromAction, outcome);
	}

	@Override
	public void handleNavigation(FacesContext context, String fromAction, String outcome) {
		if (SpringFacesContext.getCurrentInstance() != null) {
			ActionEvent actionEvent = MvcNavigationActionListener.getLastActionEvent(context);
			UIComponent component = (actionEvent == null ? null : actionEvent.getComponent());
			String defaultDestinationViewId = getDefaultDestinationViewId(context, fromAction, outcome);
			NavigationContext navigationContext = new NavigationContextImpl(fromAction, outcome, false, component,
					defaultDestinationViewId);
			if (this.navigationOutcomeResolver.canResolve(context, navigationContext)) {
				try {
					NavigationOutcome navigationOutcome = this.navigationOutcomeResolver.resolve(context,
							navigationContext);
					if (navigationOutcome != null) {
						String viewId = this.destinationAndModelRegistry.put(context, new DestinationAndModel(
								navigationOutcome, actionEvent));
						UIViewRoot newRoot = context.getApplication().getViewHandler().createView(context, viewId);
						context.setViewRoot(newRoot);
					}
				} catch (Exception e) {
					// FIXME
					throw new FacesException(e);
				}
				return;
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
	 * Allows the {@link DestinationAndModelRegistry} to be changed for testing.
	 * @param destinationAndModelRegistry the replacement registry
	 */
	final void setDestinationAndModelRegistry(DestinationAndModelRegistry destinationAndModelRegistry) {
		this.destinationAndModelRegistry = destinationAndModelRegistry;
	}

	/**
	 * Implementation of the {@link NavigationContext}.
	 */
	private static class NavigationContextImpl implements NavigationContext {

		private String fromAction;
		private String outcome;
		private boolean preEmptive;
		private UIComponent component;
		private String defaultDestinationViewId;

		public NavigationContextImpl(String fromAction, String outcome, boolean preEmptive, UIComponent component,
				String defaultDestinationViewId) {
			this.fromAction = fromAction;
			this.outcome = outcome;
			this.preEmptive = preEmptive;
			this.component = component;
			this.defaultDestinationViewId = defaultDestinationViewId;
		}

		public Object getHandler() {
			return SpringFacesContext.getCurrentInstance().getHandler();
		}

		public Object getController() {
			return SpringFacesContext.getCurrentInstance().getController();
		}

		public String getFromAction() {
			return this.fromAction;
		}

		public String getOutcome() {
			return this.outcome;
		}

		public boolean isPreemptive() {
			return this.preEmptive;
		}

		public UIComponent getComponent() {
			return this.component;
		}

		public String getDefaultDestinationViewId() {
			return this.defaultDestinationViewId;
		}
	}
}
