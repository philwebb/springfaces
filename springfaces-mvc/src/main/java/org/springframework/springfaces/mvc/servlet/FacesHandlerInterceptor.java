package org.springframework.springfaces.mvc.servlet;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class FacesHandlerInterceptor extends HandlerInterceptorAdapter implements ServletContextAware,
		ServletConfigAware, ApplicationListener<ContextRefreshedEvent> {

	private ServletContext servletContext;
	private ServletConfig servletConfig;
	private FacesContextFactory facesContextFactory;
	private Lifecycle lifecycle;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		new SpringFacesContextImpl(request, response);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		getSpringFacesContext().release();
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setServletConfig(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		String lifecycleId = servletConfig.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
		if (lifecycleId == null) {
			servletContext.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
		}
		lifecycle = lifecycleFactory.getLifecycle(lifecycleId == null ? LifecycleFactory.DEFAULT_LIFECYCLE
				: lifecycleId);
	}

	private SpringFacesContextImpl getSpringFacesContext() {
		// FIXME cast check
		return (SpringFacesContextImpl) SpringFacesContext.getCurrentInstance();
	}

	private class SpringFacesContextImpl extends SpringFacesContext {
		private HttpServletRequest request;
		private HttpServletResponse response;
		private ModelMap modelMap;
		private FacesContext facesContext;

		public SpringFacesContextImpl(HttpServletRequest request, HttpServletResponse response) {
			this.request = request;
			this.response = response;
			setCurrentInstance(this);
		}

		public void release() {
			// TODO Auto-generated method stub
		}

		@Override
		public Object getHandler() {
			// FIXME return handler, if it is postback return real one
			return null;
		}

		@Override
		public Lifecycle getLifecycle() {
			return lifecycle;
		}

		@Override
		public FacesContext getFacesContext() {
			// FIXME make ref counted
			if (facesContext == null) {
				facesContext = facesContextFactory.getFacesContext(servletContext, request, response, lifecycle);
			}
			return facesContext;
		}
	}

	//
	// private class SpringFacesContextImpl extends SpringFacesContext {
	//
	// private HttpServletRequest request;
	// private HttpServletResponse response;
	// private ModelMap modelMap;
	// private Renderable rendering;
	//
	// public SpringFacesContextImpl(HttpServletRequest request, HttpServletResponse response) {
	// this.request = request;
	// this.response = response;
	// setCurrentInstance(this);
	// }
	//
	// public void setModelMap(ModelMap modelMap) {
	// this.modelMap = modelMap;
	// }
	//
	// @Override
	// public <T> T doWithFacesContext(final FacesContextCallbackMode mode, final FacesContextCallback<T> fcc) {
	// return doWithFacesContextAndLifecycle(new FacesContextAndLifecycleCallback<T>() {
	// public T doWithFacesContextAndLifeCycle(FacesContext facesContext, Lifecycle lifecycle) {
	// //FIXME switch on mode
	// return fcc.doWithFacesContext(facesContext);
	// }
	// });
	// }
	//
	// public void render(Renderable view) {
	// //FIXME we need probably be a little more intelligent about access to Rendering.
	// //During postback navigation may want to create a different view and a the moment
	// //rendering overrides all
	// //Better still is to push it closed to the RestoreViewPhase
	// this.rendering = view;
	// try {
	// doWithFacesContextAndLifecycle(new FacesContextAndLifecycleCallback<Object>() {
	// public Object doWithFacesContextAndLifeCycle(FacesContext facesContext, Lifecycle lifecycle) {
	// //FIXME assert life
	// lifecycle.execute(facesContext);
	// storeModelMapInFlash(facesContext);
	// lifecycle.render(facesContext);
	// return null;
	// }
	// });
	// } finally {
	// this.rendering = null;
	// }
	// }
	//
	// private void storeModelMapInFlash(FacesContext facesContext) {
	// //FIXME perhaps better in viewScope ? perhaps as model?
	// Flash flash = facesContext.getExternalContext().getFlash();
	// ModelMap modelMap = this.modelMap;
	// if (modelMap == null) {
	// modelMap = (ModelMap) flash.get("modelMap");
	// }
	// if (modelMap != null) {
	// flash.put("modelMap", modelMap);
	// }
	// }
	//
	// public Renderable getRendering() {
	// return rendering;
	// }
	//
	// public void release() {
	// setCurrentInstance(null);
	// }
	//
	// private <T> T doWithFacesContextAndLifecycle(FacesContextAndLifecycleCallback<T> fcc) {
	// FacesContext facesContext = FacesContext.getCurrentInstance();
	// if (facesContext != null) {
	// return fcc.doWithFacesContextAndLifeCycle(facesContext, null);
	// }
	// Lifecycle lifecycle = getFacesFactory(LifecycleFactory.class).getLifecycle(
	// LifecycleFactory.DEFAULT_LIFECYCLE);
	// facesContext = getFacesFactory(FacesContextFactory.class).getFacesContext(servletContext, request,
	// response, lifecycle);
	// try {
	// return fcc.doWithFacesContextAndLifeCycle(facesContext, lifecycle);
	// } finally {
	// facesContext.release();
	// }
	// }
	//
	// @SuppressWarnings("unchecked")
	// private <T> T getFacesFactory(Class<T> factoryClass) {
	// return (T) FactoryFinder.getFactory(factoryClass.getName());
	// //FIXME check return type?
	// }
	// }

	// private static interface FacesContextAndLifecycleCallback<T> {
	// public T doWithFacesContextAndLifeCycle(FacesContext facesContext, Lifecycle lifecycle);
	// }
}
