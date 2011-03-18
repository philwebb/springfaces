package org.springframework.springfaces.mvc.internal;

import java.util.List;
import java.util.Map;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.util.ConfigurableNavigationHandlerWrapper;
import org.springframework.web.servlet.ViewResolver;

public class MvcNavigationHandler extends ConfigurableNavigationHandlerWrapper {

	private ConfigurableNavigationHandler delegate;

	public MvcNavigationHandler(ConfigurableNavigationHandler delegate) {
		this.delegate = delegate;
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		return delegate;
	}

	@Override
	public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {

		//ViewHandler h;
		//h.getBookmarkableURL(context, viewId, parameters, includeViewParams);

		if ("go2".equals(outcome)) {
			String fromViewId = null;
			String fromOutcome = null;
			String condition = null;
			String toViewId = "/wibble";
			Map<String, List<String>> parameters = null;
			boolean redirect = true;
			boolean includeViewParams = false;
			return new NavigationCase(fromViewId, fromAction, fromOutcome, condition, toViewId, parameters, redirect,
					includeViewParams);
		}

		return super.getNavigationCase(context, fromAction, outcome);
	}

	@Override
	public void handleNavigation(FacesContext context, String fromAction, String outcome) {

		ViewResolver viewResolver = null;

		//1) direct view names, if they resolve to JSF.  eg. custom/home -> /WEB-INF/pages/custom/home.xhtml
		//		View view = viewResolver.resolveViewName(outcome, context.getViewRoot().getLocale());
		//		if(view instanceof FacesViewStateHandler) {
		//			view.render(model, request, response);
		//		}

		//2) mvc view
		//		if(outcome starts with someprefix) {
		//  			View view = viewResolver.resolveViewName(outcomeWithoutPrefix, context.getViewRoot().getLocale());
		//		}

		//3) navigation on the controller

		super.handleNavigation(context, fromAction, outcome);
	}

}
