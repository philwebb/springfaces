package org.springframework.springfaces.mvc.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.servlet.ViewIdResolver;
import org.springframework.springfaces.mvc.servlet.view.Bookmarkable;
import org.springframework.springfaces.mvc.servlet.view.FacesView;
import org.springframework.springfaces.render.ViewArtifact;
import org.springframework.util.Assert;
import org.springframework.web.servlet.View;

/**
 * A JSF {@link ViewHandler} that provides integration with Spring MVC.
 * 
 * @author Phillip Webb
 */
public class MvcViewHandler extends ViewHandlerWrapper {

	private static final String VIEW_ARTIFACT_ATTRIBUTE = MvcViewHandler.class.getName() + ".VIEW";
	private static final String MODEL_ATTRIBUTE = MvcViewHandler.class.getName() + ".MODEL";
	private static final String ACTION_ATTRIBUTE = MvcViewHandler.class.getName() + ".ACTION";

	private Log logger = LogFactory.getLog(MvcNavigationHandler.class);

	private ViewHandler delegate;

	private ViewIdResolver viewIdResolver;

	public MvcViewHandler(ViewHandler delegate, ViewIdResolver viewIdResolver) {
		this.delegate = delegate;
		this.viewIdResolver = viewIdResolver;
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
		MvcResponseStateManager.prepare(context, null);
		context.getAttributes().remove(ACTION_ATTRIBUTE);
		ViewArtifact viewArtifact = getViewArtifact(context);
		if (viewArtifact != null) {
			MvcResponseStateManager.prepare(context, viewArtifact);
			viewId = viewArtifact.toString();
			context.getAttributes().put(ACTION_ATTRIBUTE, viewId);
		} else if (create && viewIdResolver.isResolvable(getResolvableViewId(viewId))) {
			View view = viewIdResolver.resolveViewId(getResolvableViewId(viewId), null); // FIXME
			if (view instanceof FacesView) {
				// FIXME setRendering(context, renderable, model);
				// recurse
			} else {
				return new MvcUIViewRoot(viewId, view);
			}
		}
		return (create ? super.createView(context, viewId) : super.restoreView(context, viewId));

	}

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		if (viewIdResolver.isResolvable(getResolvableViewId(viewId))) {
			return null;
		}
		return super.getViewDeclarationLanguage(context, viewId);
	}

	private String getResolvableViewId(String viewId) {
		if (viewId != null && viewId.startsWith("/")) {
			viewId = viewId.substring(1);
		}
		return viewId;
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

	private ViewArtifact getViewArtifact(FacesContext context) {
		if (SpringFacesContext.getCurrentInstance() != null && PhaseId.RESTORE_VIEW.equals(context.getCurrentPhaseId())) {
			return (ViewArtifact) context.getAttributes().get(VIEW_ARTIFACT_ATTRIBUTE);
		}
		return null;
	}

	@Override
	public String getBookmarkableURL(FacesContext context, String viewId, Map<String, List<String>> parameters,
			boolean includeViewParams) {
		if (SpringFacesContext.getCurrentInstance() != null && viewIdResolver.isResolvable(getResolvableViewId(viewId))) {
			Locale locale = context.getViewRoot().getLocale();
			View view = viewIdResolver.resolveViewId(getResolvableViewId(viewId), locale);
			Assert.isInstanceOf(Bookmarkable.class, view);
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			Map<String, Object> model = new HashMap<String, Object>();
			for (Map.Entry<String, List<String>> parameter : parameters.entrySet()) {
				if (parameter.getValue().size() == 1) {
					model.put(parameter.getKey(), parameter.getValue().get(0));
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Unable to expose multi-value parameter '" + parameter.getKey()
								+ "' to bookmark model");
					}
				}
			}
			try {
				return ((Bookmarkable) view).getBookmarkUrl(model, request);
			} catch (IOException e) {
				throw new FacesException("IOException creating MVC bookmark", e);
			}
		}
		return super.getBookmarkableURL(context, viewId, parameters, includeViewParams);
	}

	@Override
	public String getActionURL(FacesContext context, String viewId) {
		if (SpringFacesContext.getCurrentInstance() != null) {
			String actionViewId = (String) context.getAttributes().get(ACTION_ATTRIBUTE);
			if (actionViewId != null && actionViewId.equals(viewId)) {
				ExternalContext externalContext = context.getExternalContext();
				return externalContext.getRequestContextPath() + externalContext.getRequestServletPath()
						+ externalContext.getRequestPathInfo();
			}
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

	public static void prepare(FacesContext facesContext, ViewArtifact viewArtifact, Map<String, Object> model) {
		facesContext.getAttributes().put(VIEW_ARTIFACT_ATTRIBUTE, viewArtifact);
		facesContext.getAttributes().put(MODEL_ATTRIBUTE, model);
	}
}
