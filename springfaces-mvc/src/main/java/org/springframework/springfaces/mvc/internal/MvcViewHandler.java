package org.springframework.springfaces.mvc.internal;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewDeclarationLanguage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.mvc.view.Renderable;

public class MvcViewHandler extends ViewHandlerWrapper {

	private Log logger = LogFactory.getLog(MvcNavigationHandler.class);

	private ViewHandler delegate;

	public MvcViewHandler(ViewHandler delegate) {
		this.delegate = delegate;
	}

	@Override
	public ViewHandler getWrapped() {
		return delegate;
	}

	@Override
	public UIViewRoot createView(FacesContext context, String viewId) {
		String mvcViewId = getMvcViewId(context, viewId);
		if (mvcViewId != null) {
			return super.createView(context, mvcViewId);
		}
		return super.createView(context, viewId);
		//FIXME
		//if viewId starts with mvc:
		//use a viewresolver to get the view
		//if it is a JSF view use the details to restore
		//otherwise create a special view root that will render the view
	}

	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		String mvcViewId = getMvcViewId(context, viewId);
		if (mvcViewId != null) {
			return super.restoreView(context, mvcViewId);
		}
		return super.restoreView(context, viewId);
	}

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		String mvcViewId = getMvcViewId(context, viewId);
		if (mvcViewId != null) {
			//	return super.getViewDeclarationLanguage(context, mvcViewId);
		}
		return super.getViewDeclarationLanguage(context, viewId);
	}

	private String getMvcViewId(FacesContext context, String viewId) {

		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();

		//Check if we are in a restore phase, createView can also be called during navigation.
		if (!PhaseId.RESTORE_VIEW.equals(context.getCurrentPhaseId())) {
			return null;
		}

		//Check if we are rendering an MVC view
		if (springFacesContext == null || springFacesContext.getRendering() == null) {
			return null;
		}

		//Use the MVC specified details to create the view
		String mvcViewId = springFacesContext.getRendering().getViewId();
		if (logger.isDebugEnabled()) {
			logger.debug("Restoring MVC handled view '" + mvcViewId + "' for JSF vew " + viewId);
		}
		return mvcViewId;
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
				actionUrl = externalContext.getRequestContextPath() + externalContext.getRequestServletPath()
						+ externalContext.getRequestPathInfo();
			}
		}
		if (actionUrl == null) {
			actionUrl = super.getActionURL(context, viewId);
		}
		return actionUrl;
	}
}
