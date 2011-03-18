package org.springframework.springfaces.mvc.internal;

import java.io.IOException;
import java.util.Locale;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.mvc.view.FacesView;
import org.springframework.springfaces.mvc.view.Renderable;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class MvcViewHandler extends ViewHandlerWrapper {

	private Log logger = LogFactory.getLog(MvcNavigationHandler.class);

	private ViewHandler delegate;

	private ViewResolver viewResolver;

	public MvcViewHandler(ViewHandler delegate, ViewResolver viewResolver) {
		this.delegate = delegate;
		this.viewResolver = viewResolver;
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
		//FIXME
		//if viewId starts with mvc:
		//use a viewresolver to get the view
		//if it is a JSF view use the details to restore
		//otherwise create a special view root that will render the view

		if (viewId.startsWith("mvc:")) {
			String viewName = viewId.substring(4);
			Locale locale = Locale.ENGLISH; //FIXME
			try {
				View view = viewResolver.resolveViewName(viewName, locale);
				if (view instanceof FacesView) {
					//FIXME create suing super
				}
				return new MvcUIViewRoot(view);
			} catch (Exception e) {
				//FIXME
				e.printStackTrace();
			}
		}

		return super.createView(context, viewId);
	}

	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		String mvcViewId = getMvcViewId(context, viewId);
		if (mvcViewId != null) {
			return super.restoreView(context, mvcViewId);
		}
		return super.restoreView(context, viewId);
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

	private static class MvcUIViewRoot extends UIViewRoot {
		private View view;

		public MvcUIViewRoot(View view) {
			this.view = view;
		}

		@Override
		public void encodeEnd(FacesContext context) throws IOException {
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			try {
				view.render(null, request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
