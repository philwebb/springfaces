package org.springframework.springfaces.mvc.internal;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.servlet.ViewIdResolver;
import org.springframework.springfaces.util.ConfigurableNavigationHandlerWrapper;

/**
 * A JSF {@link ConfigurableNavigationHandler} that provides integration with Spring MVC.
 *
 * @author Phillip Webb
 */
public class MvcNavigationHandler extends ConfigurableNavigationHandlerWrapper {

	private ConfigurableNavigationHandler delegate;
	private ViewIdResolver viewIdResolver;

	public MvcNavigationHandler(ConfigurableNavigationHandler delegate, ViewIdResolver viewIdResolver) {
		this.delegate = delegate;
		this.viewIdResolver = viewIdResolver;
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		return delegate;
	}

	@Override
	public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
		// Handle implicit MVC navigation
		if ((SpringFacesContext.getCurrentInstance() != null) && (viewIdResolver.isResolvable(outcome))) {
			UIViewRoot root = context.getViewRoot();
			String fromViewId = (root != null ? root.getViewId() : null);
			return new NavigationCase(fromViewId, fromAction, outcome, null, outcome, null, false, false);
		}
		return super.getNavigationCase(context, fromAction, outcome);
	}

	//	private void dunno() {
	//
	//		NavigationRegistry registry = null;
	//		NavigationContext navigationContext = null;
	//		MappedNavigation mapped = registry.getMappedNavigation(navigationContext);
	//		Object outcome = mapped.getOutcome(navigationContext);
	//		//FIXME if rendered stop
	//		if(mapped != null) {
	//			if(mapped instanceof Bookmarkable) {
	//				Bookmarkable navigationResult = (Bookmarkable) mapped;
	//				String fromViewId;
	//				String fromAction;
	//				String fromOutcome;
	//				String condition;
	//				String toViewId = MvcViewHandler.registerBookmarkView(navigationResult);
	//				Map<String, List<String>> parameters = null;
	//				boolean redirect = true;
	//				boolean includeViewParams = false;
	//				NavigationCase navigationCase = new NavigationCase(fromViewId, fromAction, fromOutcome, condition, toViewId, parameters, redirect, includeViewParams);
	//			} else {
	//				//FIXME use the string value
	//			}
	//		}
	//	}

	@Override
	public void handleNavigation(FacesContext context, String fromAction, String outcome) {
		// Handle implicit MVC navigation
		if ((SpringFacesContext.getCurrentInstance() != null) && (viewIdResolver.isResolvable(outcome))) {
			ViewHandler viewHandler = context.getApplication().getViewHandler();
			context.setViewRoot(viewHandler.createView(context, outcome));
			return;
		}
		super.handleNavigation(context, fromAction, outcome);
	}

}
