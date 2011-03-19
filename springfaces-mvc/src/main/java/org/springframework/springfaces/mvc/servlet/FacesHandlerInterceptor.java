package org.springframework.springfaces.mvc.servlet;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.Flash;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.mvc.view.Renderable;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class FacesHandlerInterceptor extends HandlerInterceptorAdapter implements ServletContextAware {

	private ServletContext servletContext;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		new SpringFacesContextImpl(request, response);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			getSpringFacesContext().setModelMap(modelAndView.getModelMap());
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		getSpringFacesContext().release();
	}

	private SpringFacesContextImpl getSpringFacesContext() {
		//FIXME cast check
		return (SpringFacesContextImpl) SpringFacesContext.getCurrentInstance();
	}

	private class SpringFacesContextImpl extends SpringFacesContext {

		private HttpServletRequest request;
		private HttpServletResponse response;
		private ModelMap modelMap;
		private Renderable rendering;

		public SpringFacesContextImpl(HttpServletRequest request, HttpServletResponse response) {
			this.request = request;
			this.response = response;
			setCurrentInstance(this);
		}

		public void setModelMap(ModelMap modelMap) {
			this.modelMap = modelMap;
		}

		@Override
		public <T> T doWithFacesContext(final FacesContextCallbackMode mode, final FacesContextCallback<T> fcc) {
			return doWithFacesContextAndLifecycle(new FacesContextAndLifecycleCallback<T>() {
				public T doWithFacesContextAndLifeCycle(FacesContext facesContext, Lifecycle lifecycle) {
					//FIXME switch on mode
					return fcc.doWithFacesContext(facesContext);
				}
			});
		}

		public void render(Renderable view) {
			//FIXME we need probably be a little more intelligent about access to Rendering.
			//During postback navigation may want to create a different view and a the moment
			//rendering overrides all
			//Better still is to push it closed to the RestoreViewPhase
			this.rendering = view;
			try {
				doWithFacesContextAndLifecycle(new FacesContextAndLifecycleCallback<Object>() {
					public Object doWithFacesContextAndLifeCycle(FacesContext facesContext, Lifecycle lifecycle) {
						//FIXME assert life
						lifecycle.execute(facesContext);
						storeModelMapInFlash(facesContext);
						lifecycle.render(facesContext);
						return null;
					}
				});
			} finally {
				this.rendering = null;
			}
		}

		private void storeModelMapInFlash(FacesContext facesContext) {
			//FIXME perhaps better in viewScope ?  perhaps as model?
			Flash flash = facesContext.getExternalContext().getFlash();
			ModelMap modelMap = this.modelMap;
			if (modelMap == null) {
				modelMap = (ModelMap) flash.get("modelMap");
			}
			if (modelMap != null) {
				flash.put("modelMap", modelMap);
			}
		}

		public Renderable getRendering() {
			return rendering;
		}

		public void release() {
			setCurrentInstance(null);
		}

		private <T> T doWithFacesContextAndLifecycle(FacesContextAndLifecycleCallback<T> fcc) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if (facesContext != null) {
				return fcc.doWithFacesContextAndLifeCycle(facesContext, null);
			}
			Lifecycle lifecycle = getFacesFactory(LifecycleFactory.class).getLifecycle(
					LifecycleFactory.DEFAULT_LIFECYCLE);
			facesContext = getFacesFactory(FacesContextFactory.class).getFacesContext(servletContext, request,
					response, lifecycle);
			try {
				return fcc.doWithFacesContextAndLifeCycle(facesContext, lifecycle);
			} finally {
				facesContext.release();
			}
		}

		@SuppressWarnings("unchecked")
		private <T> T getFacesFactory(Class<T> factoryClass) {
			return (T) FactoryFinder.getFactory(factoryClass.getName());
			//FIXME check return type?
		}
	}

	private static interface FacesContextAndLifecycleCallback<T> {
		public T doWithFacesContextAndLifeCycle(FacesContext facesContext, Lifecycle lifecycle);
	}
}
