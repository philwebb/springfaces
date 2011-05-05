package org.springframework.springfaces.mvc.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.servlet.ViewIdResolver;
import org.springframework.springfaces.mvc.servlet.view.Bookmarkable;
import org.springframework.springfaces.render.ModelAndViewArtifact;
import org.springframework.springfaces.render.ViewArtifact;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
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
	private NavigationOutcomeViewRegistry navigationOutcomeViewRegistry = new NavigationOutcomeViewRegistry();

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
		if (SpringFacesContext.getCurrentInstance() != null) {
			if (PhaseId.INVOKE_APPLICATION.equals(context.getCurrentPhaseId())) {
				// Creating view in response to a navigation event
				View view = getView(context, viewId);
				if (view != null) {
					return new MvcUIViewRoot(viewId, view);
				}
			} else if (SpringFacesContext.getCurrentInstance().getRendering() != null) {
				// Creating a view that was triggered from MVC
				ModelAndViewArtifact rendering = SpringFacesContext.getCurrentInstance().getRendering();
				UIViewRoot viewRoot = super.createView(context, rendering.getViewArtifact().toString());
				context.getAttributes().put(ACTION_ATTRIBUTE, rendering.getViewArtifact().toString());
				if (rendering.getModel() != null) {
					// FIXME perhaps store as a single attribute and have an ELResolver to access?
					// FIXME do we want scope support for the mode (eg request, session)
					// FIXME is storing the model the responsibility of the ViewHandler?
					viewRoot.getViewMap().putAll(rendering.getModel());
				}
				return viewRoot;
			}
		}
		return super.createView(context, viewId);
	}

	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		if (SpringFacesContext.getCurrentInstance() != null
				&& SpringFacesContext.getCurrentInstance().getRendering() != null) {
			ModelAndViewArtifact rendering = SpringFacesContext.getCurrentInstance().getRendering();
			return super.restoreView(context, rendering.getViewArtifact().toString());
		}
		return super.restoreView(context, viewId);
	}

	private View getView(FacesContext context, String viewId) {
		viewId = getResolvableViewId(viewId);
		NavigationOutcome navigationOutcome = navigationOutcomeViewRegistry.get(context, viewId);
		if (navigationOutcome != null) {
			View view = getViewForDestination(navigationOutcome.getDestination());
			return view;
		}
		if (viewIdResolver.isResolvable(viewId)) {
			return viewIdResolver.resolveViewId(viewId, null); // FIXME locale
		}
		return null;
	}

	private View getViewForDestination(Object destination) {
		if (destination instanceof View) {
			return (View) destination;
		}
		if (viewIdResolver.isResolvable(destination.toString())) {
			return viewIdResolver.resolveViewId(destination.toString(), null); // FIXME locale
		}
		return viewIdResolver.resolveDirectViewId(destination.toString(), null); // FIXME locale
	}

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		if (navigationOutcomeViewRegistry.get(context, viewId) != null
				|| viewIdResolver.isResolvable(getResolvableViewId(viewId))) {
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
			// FIXME what if we are in AJAX here!
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			try {
				// FIXME model
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
		String url = getBookmarkUrlIfResolvable(context, viewId, parameters);
		if (url != null) {
			return url;
		}
		return super.getBookmarkableURL(context, viewId, parameters, includeViewParams);
	}

	@Override
	public String getRedirectURL(FacesContext context, String viewId, Map<String, List<String>> parameters,
			boolean includeViewParams) {
		String url = getBookmarkUrlIfResolvable(context, viewId, parameters);
		if (url != null) {
			return url;
		}
		return super.getRedirectURL(context, viewId, parameters, includeViewParams);
	}

	private String getBookmarkUrlIfResolvable(FacesContext context, String viewId, Map<String, List<String>> parameters) {
		// FIXME do we need to worry about char encoding
		if (SpringFacesContext.getCurrentInstance() != null) {
			View view = getView(context, viewId);
			if (view == null) {
				return null;
			}
			Assert.isInstanceOf(Bookmarkable.class, view);
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			Map<String, Object> model = new HashMap<String, Object>();
			for (Map.Entry<String, List<String>> parameter : parameters.entrySet()) {
				if (parameter.getValue().size() == 1) {
					model.put(parameter.getKey(), evaluateExpression(context, parameter.getValue().get(0)));
				} else {
					if (logger.isWarnEnabled()) {
						// FIXME should we be able to expose theses
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
		return null;
	}

	private String evaluateExpression(FacesContext context, String value) {
		if (isExpression(value)) {
			return context.getApplication().evaluateExpressionGet(context, value, String.class);
		}
		return value;
	}

	public static boolean isExpression(String expression) {
		// FIXME implementation as ELUtils, can we use the RegEx
		if (!StringUtils.hasLength(expression)) {
			return false;
		}
		int start = expression.indexOf("#{");
		int end = expression.indexOf('}');
		return (start != -1) && (start < end);
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

	// FIXME make not static?
	public static void prepare(FacesContext facesContext, ViewArtifact viewArtifact, Map<String, Object> model) {
		facesContext.getAttributes().put(VIEW_ARTIFACT_ATTRIBUTE, viewArtifact);
		facesContext.getAttributes().put(MODEL_ATTRIBUTE, model);
	}
}
