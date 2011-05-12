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
import javax.faces.context.PartialViewContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewDeclarationLanguage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
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
	private DestinationViewResolver destinationViewResolver;
	private NavigationOutcomeViewRegistry navigationOutcomeViewRegistry = new NavigationOutcomeViewRegistry();

	public MvcViewHandler(ViewHandler delegate, DestinationViewResolver destinationViewResolver) {
		this.delegate = delegate;
		this.destinationViewResolver = destinationViewResolver;
	}

	@Override
	public ViewHandler getWrapped() {
		return delegate;
	}

	@Override
	public UIViewRoot createView(FacesContext context, String viewId) {
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		if (springFacesContext == null) {
			return super.createView(context, viewId);
		}
		UIViewRoot viewRoot = createViewIfInResponseToNavigation(context, viewId);
		if (viewRoot == null) {
			viewRoot = createViewIfRenderingMvc(context, viewId);
		}
		if (viewRoot == null) {
			viewRoot = super.createView(context, viewId);
		}
		return viewRoot;
	}

	private UIViewRoot createViewIfInResponseToNavigation(FacesContext context, String viewId) {
		if (PhaseId.INVOKE_APPLICATION.equals(context.getCurrentPhaseId())) {
			// Navigation response can only occur in the invoke application phase
			View view = getView(context, viewId);
			if (view != null) {
				return new MvcNavigationResponseUIViewRoot(viewId, view);
			}
		}
		return null;
	}

	private UIViewRoot createViewIfRenderingMvc(FacesContext context, String viewId) {
		ModelAndViewArtifact rendering = SpringFacesContext.getCurrentInstance().getRendering();
		if (rendering != null) {
			UIViewRoot viewRoot = super.createView(context, rendering.getViewArtifact().toString());
			context.getAttributes().put(ACTION_ATTRIBUTE, rendering.getViewArtifact().toString());
			storeModel(rendering, viewRoot);
			return viewRoot;
		}
		return super.createView(context, viewId);
	}

	private void storeModel(ModelAndViewArtifact modelAndViewArtifact, UIViewRoot viewRoot) {
		if (modelAndViewArtifact.getModel() != null) {
			// FIXME perhaps store as a single attribute and have an ELResolver to access?
			// FIXME do we want scope support for the mode (eg request, session)
			// FIXME is storing the model the responsibility of the ViewHandler?
			viewRoot.getViewMap().putAll(modelAndViewArtifact.getModel());
		}
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

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		// We need to ensure that views we resolve do not return a VDL. This prevents NoClassDefFoundError for
		// javax.servlet.jsp.jstl.core.Config
		if (getNavigationOutcomeForViewId(context, viewId) != null) {
			return null;
		}
		return super.getViewDeclarationLanguage(context, viewId);
	}

	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		if (viewToRender instanceof MvcNavigationResponseUIViewRoot) {
			// FIXME what if we are in AJAX here!
			// FIXME what if in portlet environment
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			try {
				// FIXME model
				((MvcNavigationResponseUIViewRoot) viewToRender).getView().render(null, request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			super.renderView(context, viewToRender);
		}
	}

	@Override
	public String getBookmarkableURL(FacesContext context, String viewId, Map<String, List<String>> parameters,
			boolean includeViewParams) {
		String url = getBookmarkUrlIfResolvable(context, viewId, parameters);
		if (url == null) {
			url = super.getBookmarkableURL(context, viewId, parameters, includeViewParams);
		}
		return url;
	}

	@Override
	public String getRedirectURL(FacesContext context, String viewId, Map<String, List<String>> parameters,
			boolean includeViewParams) {
		String url = getBookmarkUrlIfResolvable(context, viewId, parameters);
		if (url == null) {
			url = super.getRedirectURL(context, viewId, parameters, includeViewParams);
		}
		return url;
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
					// FIXME rethink when to evaluate expressions. We could end up with EL injection if the parameter
					// comes from an f:param that is already evaluated
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

	private View getView(FacesContext context, String viewId) {
		NavigationOutcome navigationOutcome = getNavigationOutcomeForViewId(context, viewId);
		if (navigationOutcome != null) {
			View view = resolveDestination(navigationOutcome.getDestination());
			return view;
		}
		return null;
	}

	private NavigationOutcome getNavigationOutcomeForViewId(FacesContext context, String viewId) {
		if (viewId != null && viewId.startsWith("/")) {
			viewId = viewId.substring(1);
		}
		// FIXME registry needs additional model from the action listener
		return navigationOutcomeViewRegistry.get(context, viewId);
	}

	private View resolveDestination(Object destination) {
		if (destination instanceof View) {
			return (View) destination;
		}
		try {
			return destinationViewResolver.resolveDestination(destination, null); // FIXME locale
		} catch (Exception e) {
			throw new IllegalStateException("Unable to resolve destination '" + destination + "'", e);
		}
	}

	private String evaluateExpression(FacesContext context, String value) {
		if (isExpression(value)) {
			return context.getApplication().evaluateExpressionGet(context, value, String.class);
		}
		return value;
	}

	private boolean isExpression(String expression) {
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

	// FIXME
	private void setRenderAll(FacesContext facesContext, String viewId) {
		if (facesContext.getViewRoot().getViewId().equals(viewId)) {
			return;
		}
		PartialViewContext partialViewContext = facesContext.getPartialViewContext();
		if (partialViewContext.isRenderAll()) {
			return;
		}
		partialViewContext.setRenderAll(true);
	}

	// FIXME make not static?
	public static void prepare(FacesContext facesContext, ViewArtifact viewArtifact, Map<String, Object> model) {
		facesContext.getAttributes().put(VIEW_ARTIFACT_ATTRIBUTE, viewArtifact);
		facesContext.getAttributes().put(MODEL_ATTRIBUTE, model);
	}

	private static class MvcNavigationResponseUIViewRoot extends UIViewRoot {
		private View view;

		public MvcNavigationResponseUIViewRoot(String viewId, View view) {
			setViewId(viewId);
			this.view = view;
		}

		public View getView() {
			return view;
		}
	}
}
