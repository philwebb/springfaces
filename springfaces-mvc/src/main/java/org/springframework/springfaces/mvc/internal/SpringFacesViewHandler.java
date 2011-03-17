package org.springframework.springfaces.mvc.internal;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.mvc.view.Renderable;

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
		System.out.println("Create view " + context.getCurrentPhaseId() + " " + viewId);
		return super.createView(context, convertViewId(viewId));
	}

	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		System.out.println("Restore view " + context.getCurrentPhaseId() + " " + viewId);
		return super.restoreView(context, convertViewId(viewId));
	}

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		System.out.println("Get VDL " + context.getCurrentPhaseId() + " " + viewId);
		return super.getViewDeclarationLanguage(context, convertViewId(viewId));
	}

	@Override
	public String getActionURL(FacesContext context, String viewId) {
		System.out.println("Get Action URL " + context.getCurrentPhaseId() + " " + viewId);
		String actionUrl = null;
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		if (springFacesContext != null && springFacesContext.getRendering() != null) {
			Renderable rendering = springFacesContext.getRendering();
			if (viewId.equals(rendering.getViewId())) {
				//FIXME currently action URL for the rendering view is the request, could push up to allow custom postback URLs
				ExternalContext externalContext = context.getExternalContext();
				HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
				actionUrl = request.getServletPath() + request.getPathInfo();
			}
		}
		if (actionUrl == null) {
			actionUrl = super.getActionURL(context, viewId);
		}
		return actionUrl;
	}

	private String convertViewId(String viewId) {
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		if (springFacesContext != null && springFacesContext.getRendering() != null) {
			//Use the view ID of the item being rendered instead
			return springFacesContext.getRendering().getViewId();
		}
		return viewId;
	}

}
