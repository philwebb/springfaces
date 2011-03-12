package org.springframework.springfaces;

import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.Flash;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.NamedThreadLocal;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class FacesHandlerInterceptor extends HandlerInterceptorAdapter implements ServletContextAware {

	private static ThreadLocal<SpringFacesContext> context = new NamedThreadLocal<SpringFacesContext>(
			"SpringFacesContext");

	private ServletContext servletContext;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		context.set(new SpringFacesContext(servletContext, request, response, handler));
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		getContext().setModelMap(modelAndView.getModelMap());
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		context.remove();
	}

	//FIXME null version
	public static SpringFacesContext getContext() {
		return context.get();
	}

	public static class SpringFacesContext {

		private HttpServletRequest request;
		private HttpServletResponse response;
		private Object handler;
		private ModelMap modelMap;
		private ServletContext servletContext;
		private FacesView rendering;

		public SpringFacesContext(ServletContext servletContext, HttpServletRequest request,
				HttpServletResponse response, Object handler) {
			this.servletContext = servletContext;
			this.request = request;
			this.response = response;
			this.handler = handler;
		}

		public void setModelMap(ModelMap modelMap) {
			this.modelMap = modelMap;
		}

		public void render(FacesView view) {
			this.rendering = view;
			try {
				doWithFacesContext(new FacesContextCallback() {
					public void doWith(FacesContext facesContext, Lifecycle lifecycle) {
						lifecycle.execute(facesContext);
						storeModelMapInFlash(facesContext);
						lifecycle.render(facesContext);
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

		public boolean isRendering() {
			return rendering != null;
		}

		public FacesView getRendering() {
			return rendering;
		}

		public void doWithFacesContext(FacesContextCallback fc) {
			Lifecycle lifecycle = FacesFactory.get(LifecycleFactory.class).getLifecycle(
					LifecycleFactory.DEFAULT_LIFECYCLE);
			FacesContext facesContext = FacesFactory.get(FacesContextFactory.class).getFacesContext(servletContext,
					request, response, lifecycle);
			try {
				fc.doWith(facesContext, lifecycle);
			} finally {
				facesContext.release();
			}
		}
	}

	public static interface FacesContextCallback {
		public void doWith(FacesContext facesContext, Lifecycle lifecycle);
	}

}
