package org.springframework.springfaces.mvc.internal;

import java.io.IOException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewDeclarationLanguage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.mvc.Dunno;
import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.mvc.view.FacesView;
import org.springframework.springfaces.mvc.view.Renderable;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class MvcViewHandler extends ViewHandlerWrapper {

	private static final String VIEW_ATTRIBUTE = MvcViewHandler.class.getName() + ".VIEW";
	private static final String MODEL_ATTRIBUTE = MvcViewHandler.class.getName() + ".MODEL";
	private static final String ACTION_ATTRIBUTE = MvcViewHandler.class.getName() + ".MODEL";
	private static final String DEFAULT_ACTION_URL = "";

	private Log logger = LogFactory.getLog(MvcNavigationHandler.class);

	private ViewHandler delegate;

	private Dunno dunno = new Dunno() {

		public boolean isSupported(String viewId) {
			// TODO Auto-generated method stub
			return false;
		}

		public View getView(String viewName) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public MvcViewHandler(ViewHandler delegate, ViewResolver viewResolver) {
		this.delegate = delegate;
	}

	@Override
	public ViewHandler getWrapped() {
		return delegate;
	}

	@Override
	public UIViewRoot createView(FacesContext context, String viewId) {
		return createOrRestoreView(context, viewId, true);
	}

	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		return createOrRestoreView(context, viewId, false);
	}

	private UIViewRoot createOrRestoreView(FacesContext context, String viewId, boolean create) {
		MvcResponseStateManager.setRendering(context, null);
		context.getAttributes().remove(ACTION_ATTRIBUTE);
		Renderable rendering = getRendering(context);
		if (rendering != null) {
			String actionUrl = rendering.getActionUrl();
			context.getAttributes().put(ACTION_ATTRIBUTE, actionUrl == null ? DEFAULT_ACTION_URL : actionUrl);
			MvcResponseStateManager.setRendering(context, rendering);
			viewId = rendering.getViewId();
		} else if (create && dunno.isSupported(cleanupViewId(viewId))) {
			View view = dunno.getView(cleanupViewId(viewId));
			if (view instanceof FacesView) {
				//FIXME setRendering(context, renderable, model);
				//recurse
			} else {
				return new MvcUIViewRoot(viewId, view);
			}
		}
		return (create ? super.createView(context, viewId) : super.restoreView(context, viewId));

	}

	private String cleanupViewId(String viewId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		if (viewId.startsWith("/mvc:")) {
			return null;
		}
		return super.getViewDeclarationLanguage(context, viewId);
	}

	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		if (viewToRender instanceof MvcUIViewRoot) {
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			try {
				((MvcUIViewRoot) viewToRender).getView().render(null, request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			super.renderView(context, viewToRender);
		}
	}

	private Renderable getRendering(FacesContext context) {
		if (SpringFacesContext.getCurrentInstance() != null && PhaseId.RESTORE_VIEW.equals(context.getCurrentPhaseId())) {
			return (Renderable) context.getAttributes().get(VIEW_ATTRIBUTE);
		}
		return null;
	}

	@Override
	public String getActionURL(FacesContext context, String viewId) {
		if (SpringFacesContext.getCurrentInstance() != null && context.getAttributes().containsKey(ACTION_ATTRIBUTE)) {
			String actionUrl = (String) context.getAttributes().get(ACTION_ATTRIBUTE);
			if (DEFAULT_ACTION_URL.equals(actionUrl)) {
				ExternalContext externalContext = context.getExternalContext();
				actionUrl = externalContext.getRequestContextPath() + externalContext.getRequestServletPath()
						+ externalContext.getRequestPathInfo();
			}
			return actionUrl;
		}
		return super.getActionURL(context, viewId);
	}

	private static class MvcUIViewRoot extends UIViewRoot {
		private View view;

		public MvcUIViewRoot(String viewId, View view) {
			setViewId(viewId);
			this.view = view;
		}

		public View getView() {
			return view;
		}
	}

	public static void setRendering(FacesContext facesContext, Renderable renderable, Map<String, Object> model) {
		facesContext.getAttributes().put(VIEW_ATTRIBUTE, renderable);
		facesContext.getAttributes().put(MODEL_ATTRIBUTE, model);
	}
}
