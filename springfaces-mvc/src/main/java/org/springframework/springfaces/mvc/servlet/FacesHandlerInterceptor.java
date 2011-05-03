package org.springframework.springfaces.mvc.servlet;

import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContextWrapper;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.internal.MvcViewHandler;
import org.springframework.springfaces.render.ModelAndViewArtifact;
import org.springframework.springfaces.render.ViewArtifact;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * MVC {@link HandlerInterceptor} to setup and release a {@link SpringFacesContext} instance.
 * 
 * @see FacesPostbackHandler
 * @author Phillip Webb
 */
public class FacesHandlerInterceptor extends HandlerInterceptorAdapter implements ServletContextAware {

	private ServletContext servletContext;
	private FacesContextFactory facesContextFactory;
	private Lifecycle lifecycle;
	private String lifecycleId;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		obtainFacesObjects();
		if (handler instanceof Postback) {
			handler = ((Postback) handler).getHandler();
		}
		new SpringFacesContextImpl(request, response, handler);
		return true;
	}

	private void obtainFacesObjects() {
		if (facesContextFactory == null) {
			facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		}
		if (lifecycle == null) {
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			if (lifecycleId == null) {
				servletContext.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
			}
			lifecycle = lifecycleFactory.getLifecycle(lifecycleId == null ? LifecycleFactory.DEFAULT_LIFECYCLE
					: lifecycleId);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		getSpringFacesContext().release();
	}

	// FIXME DC FacesServlet.LIFECYCLE_ID_ATTR
	public void setLifecycleId(String lifecycleId) {
		this.lifecycleId = lifecycleId;
	}

	/**
	 * Returns the {@link SpringFacesContextImpl}.
	 * @return the {@link SpringFacesContextImpl}
	 */
	private SpringFacesContextImpl getSpringFacesContext() {
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		Assert.isInstanceOf(SpringFacesContextImpl.class, springFacesContext, "Unable to access SpringFacesContext ");
		return (SpringFacesContextImpl) springFacesContext;
	}

	/**
	 * The {@link SpringFacesContext} implementation managed by {@link FacesHandlerInterceptor}.
	 */
	private class SpringFacesContextImpl extends SpringFacesContext {

		private HttpServletRequest request;
		private HttpServletResponse response;
		private Object handler;
		private ReferenceCountedFacesContext facesContext = new ReferenceCountedFacesContext();
		private boolean released;
		private WebApplicationContext webApplicationContext;
		private ModelAndViewArtifact rendering;

		public SpringFacesContextImpl(HttpServletRequest request, HttpServletResponse response, Object handler) {
			this.request = request;
			this.response = response;
			this.handler = handler;
			this.webApplicationContext = RequestContextUtils.getWebApplicationContext(request, servletContext);
			setCurrentInstance(this);
		}

		public void release() {
			released = true;
			facesContext.releaseDelegate();
			setCurrentInstance(null);
		}

		@Override
		public Object getHandler() {
			checkNotRelased();
			return handler;
		}

		@Override
		public FacesContext getFacesContext() {
			checkNotRelased();
			facesContext.addReference();
			return facesContext;
		}

		@Override
		public WebApplicationContext getWebApplicationContext() {
			return webApplicationContext;
		}

		@Override
		public void render(ModelAndViewArtifact modelAndViewArtifact) {
			checkNotRelased();
			Assert.state(rendering == null, "The SpringFacesContext is already rendering");
			this.rendering = modelAndViewArtifact;
			try {
				render(modelAndViewArtifact.getViewArtifact(), modelAndViewArtifact.getModel());
			} finally {
				this.rendering = null;
			}
		}

		// FIXME sort out ModelAndViewArtifact
		private void render(ViewArtifact viewArtifact, Map<String, Object> model) {
			FacesContext facesContext = getFacesContext();
			try {
				MvcViewHandler.prepare(facesContext, viewArtifact, model);
				lifecycle.execute(facesContext);
				lifecycle.render(facesContext);
			} finally {
				facesContext.release();
			}
		}

		@Override
		public ModelAndViewArtifact getRendering() {
			return rendering;
		}

		private void checkNotRelased() {
			Assert.state(!released, "The SpringFacesContext has been released");
		}

		/**
		 * A reference counted wrapper for the {@link FacesContext} that will drop the underlying context when all
		 * referenced have been {@link #release() released}.
		 */
		private class ReferenceCountedFacesContext extends FacesContextWrapper {

			private FacesContext delegate;
			int referenceCount;

			public ReferenceCountedFacesContext() {
			}

			@Override
			public FacesContext getWrapped() {
				if (delegate == null) {
					delegate = facesContextFactory.getFacesContext(servletContext, request, response, lifecycle);
				}
				return delegate;
			}

			public void addReference() {
				referenceCount++;
			}

			@Override
			public void release() {
				referenceCount--;
				if (referenceCount == 0) {
					releaseDelegate();
				}
			}

			public void releaseDelegate() {
				if (delegate != null) {
					delegate.release();
					delegate = null;
				}
			}
		}
	}
}
