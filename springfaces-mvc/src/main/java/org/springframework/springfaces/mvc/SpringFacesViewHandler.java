package org.springframework.springfaces.mvc;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;

public class SpringFacesViewHandler extends ViewHandlerWrapper {

	private ViewHandler delegate;

	public SpringFacesViewHandler(ViewHandler delegate) {
		this.delegate = delegate;
	}

	@Override
	public ViewHandler getWrapped() {
		return delegate;
	}

	@Override
	public UIViewRoot createView(FacesContext context, String viewId) {
		return super.createView(context, convertViewId(viewId));
	}

	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		return super.restoreView(context, convertViewId(viewId));
	}

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		return super.getViewDeclarationLanguage(context, convertViewId(viewId));
	}

	@Override
	public String getActionURL(FacesContext context, String viewId) {
		String actionUrl = null;
		if (SpringFacesContext.getCurrentInstance() != null) {
			//FIXME get the action URL, will always be postback
			ExternalContext extContext = context.getExternalContext();
			String contextPath = extContext.getRequestContextPath();
			return contextPath + "/spring/simple";
		}
		if (actionUrl == null) {
			actionUrl = super.getActionURL(context, viewId);
		}
		return actionUrl;
	}

	private String convertViewId(String viewId) {
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		if (springFacesContext != null && springFacesContext.getRendering() != null) {
			return springFacesContext.getRendering().getViewId();
		}
		return viewId;
	}

}
