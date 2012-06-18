/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.mvc.internal;

import java.io.IOException;
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
import javax.faces.render.RenderKitFactory;
import javax.faces.view.ViewDeclarationLanguage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableView;
import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * A JSF {@link ViewHandler} that provides integration with Spring MVC.
 * 
 * @author Phillip Webb
 */
public class MvcViewHandler extends ViewHandlerWrapper {

	private static final String ACTION_ATTRIBUTE = MvcViewHandler.class.getName() + ".ACTION";

	private ViewHandler delegate;
	private DestinationViewResolver destinationViewResolver;
	private DestinationAndModelRegistry destinationAndModelRegistry = newDestinationAndModelRegistry();

	/**
	 * Create a new MVC view resolver.
	 * @param delegate the delegate view resolver
	 * @param destinationViewResolver used to resolve navigation destinations
	 */
	public MvcViewHandler(ViewHandler delegate, DestinationViewResolver destinationViewResolver) {
		Assert.notNull(delegate, "Delegate ViewResolver must not be null");
		Assert.notNull(destinationViewResolver, "DestinationViewResolver must not be null");
		this.delegate = delegate;
		this.destinationViewResolver = destinationViewResolver;
	}

	/**
	 * Factory method used to create {@link DestinationAndModelRegistry}.
	 * @return the destination and model registry.
	 */
	protected DestinationAndModelRegistry newDestinationAndModelRegistry() {
		return new DestinationAndModelRegistry();
	}

	@Override
	public ViewHandler getWrapped() {
		return this.delegate;
	}

	@Override
	public UIViewRoot createView(FacesContext context, String viewId) {
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		if (springFacesContext == null) {
			return super.createView(context, viewId);
		}
		UIViewRoot viewRoot = createViewIfInResponseToNavigation(context, viewId);
		if (viewRoot == null) {
			viewRoot = createViewIfRenderingSpringMvcView(context, viewId);
		}
		if (viewRoot == null) {
			viewRoot = super.createView(context, viewId);
		}
		return viewRoot;
	}

	private UIViewRoot createViewIfInResponseToNavigation(FacesContext context, String viewId) {
		// Navigation response can only occur in the invoke application phase
		if (PhaseId.INVOKE_APPLICATION.equals(context.getCurrentPhaseId())) {
			ModelAndView modelAndView = getModelAndView(context, viewId, null);
			if (modelAndView != null) {
				UIViewRoot existingViewRoot = context.getViewRoot();
				String renderKitId = existingViewRoot == null ? null : existingViewRoot.getRenderKitId();
				return new NavigationResponseUIViewRoot(viewId, renderKitId, modelAndView);
			}
		}
		return null;
	}

	private UIViewRoot createViewIfRenderingSpringMvcView(FacesContext context, String viewId) {
		ModelAndViewArtifact rendering = SpringFacesContext.getCurrentInstance().getRendering();
		if (rendering != null) {
			UIViewRoot viewRoot = super.createView(context, rendering.getViewArtifact().toString());
			context.getAttributes().put(ACTION_ATTRIBUTE, rendering.getViewArtifact().toString());
			SpringFacesModelHolder.attach(context, viewRoot, rendering.getModel());
			return viewRoot;
		}
		return null;
	}

	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		if (SpringFacesContext.getCurrentInstance() != null
				&& SpringFacesContext.getCurrentInstance().getRendering() != null) {
			ModelAndViewArtifact rendering = SpringFacesContext.getCurrentInstance().getRendering();
			context.getAttributes().put(ACTION_ATTRIBUTE, rendering.getViewArtifact().toString());
			return super.restoreView(context, rendering.getViewArtifact().toString());
		}
		return super.restoreView(context, viewId);
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

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		if (SpringFacesContext.getCurrentInstance() != null) {
			// We need to ensure that views we resolve do not return a VDL. This prevents NoClassDefFoundError for
			// javax.servlet.jsp.jstl.core.Config
			if (getDestinationAndModelForViewId(context, viewId) != null) {
				return null;
			}
		}
		return super.getViewDeclarationLanguage(context, viewId);
	}

	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		if (viewToRender instanceof NavigationResponseUIViewRoot) {
			viewToRender.encodeAll(context);
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
		if (SpringFacesContext.getCurrentInstance() != null) {
			ModelAndView modelAndView = getModelAndView(context, viewId, parameters);
			View view = modelAndView == null ? null : modelAndView.getView();
			if (view == null) {
				return null;
			}
			Assert.isInstanceOf(BookmarkableView.class, view);
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			try {
				return ((BookmarkableView) view).getBookmarkUrl(modelAndView.getModel(), request);
			} catch (Exception e) {
				throw new FacesException("IOException creating MVC bookmark", e);
			}
		}
		return null;
	}

	private ModelAndView getModelAndView(FacesContext context, String viewId, Map<String, List<String>> parameters) {
		DestinationAndModel destinationAndModel = getDestinationAndModelForViewId(context, viewId);
		if (destinationAndModel != null) {
			ModelAndView modelAndView = resolveDestination(context, destinationAndModel.getDestination());
			Map<String, Object> resolvedViewModel = destinationAndModel.getModel(context, parameters,
					modelAndView.getModel());
			return new ModelAndView(modelAndView.getView(), resolvedViewModel);
		}
		return null;
	}

	private DestinationAndModel getDestinationAndModelForViewId(FacesContext context, String viewId) {
		if (viewId != null && viewId.startsWith("/")) {
			viewId = viewId.substring(1);
		}
		DestinationAndModel destinationAndModel = this.destinationAndModelRegistry.get(context, viewId);
		return destinationAndModel;
	}

	private ModelAndView resolveDestination(FacesContext context, Object destination) {
		if (destination instanceof View) {
			return new ModelAndView((View) destination);
		}
		if (destination instanceof ModelAndView) {
			return (ModelAndView) destination;
		}
		try {
			Locale locale = FacesUtils.getLocale(context);
			SpringFacesModel model = SpringFacesModelHolder.getModel(context.getViewRoot());
			return this.destinationViewResolver.resolveDestination(context, destination, locale, model);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to resolve destination '" + destination + "'", e);
		}
	}

	protected static class NavigationResponseUIViewRoot extends UIViewRoot {

		private ModelAndView modelAndView;

		public NavigationResponseUIViewRoot(String viewId, String renderKitId, ModelAndView modelAndView) {
			// FIXME test render kit
			setViewId(viewId);
			setRenderKitId(renderKitId == null ? RenderKitFactory.HTML_BASIC_RENDER_KIT : renderKitId);
			this.modelAndView = modelAndView;
		}

		public ModelAndView getModelAndView() {
			return this.modelAndView;
		}

		@Override
		public void encodeAll(FacesContext context) throws IOException {
			SpringFacesContext.getCurrentInstance(true).render(this.modelAndView.getView(),
					this.modelAndView.getModel());
		}
	}
}
